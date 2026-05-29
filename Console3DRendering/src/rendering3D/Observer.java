package rendering3D;

import java.util.ArrayList;
import java.util.HashMap;

import classes2D.R2Point;
import classes3D.R3Matrix;
import classes3D.R3Point;
import other.Constants;
import zBuffered2DRendering.ZFigure;
import zBuffered2DRendering.ZImage;
import zBuffered2DRendering.ZPixel;

public class Observer {

	private static HashMap<Integer, Observer> IDMap = new HashMap<Integer, Observer>();
	private static int currentGreatestID = 1;

	private int ID;

	// What the observer is seeing.
	ZImage view;

	double fov;

	R3Point position;

	/*
	 * The rotation matrix that will be applied to points when they are observed.
	 * 
	 * The columns of the inverse of this matrix corresponds to the observer's
	 * orientation in space. This is because rotating your head to the left looks
	 * like keeping your head fixed and the world around you rotating to the right.
	 * 
	 * Since rotation matrices are orthogonal, its inverse is equal to its
	 * transpose, hence the rows of the matrix correspond to the observer's
	 * orientation.
	 */
	R3Matrix rotation;

	/*
	 * Theta rotates the observer to the left and phi rotates the observer up. Phi
	 * is restricted to the interval -pi/2 , pi/2 to avoid flipping the observer
	 * upside down.
	 * 
	 * The formula for the rotation matrix was derived by multiplying the rotation
	 * matrices for theta alone and phi alone, which can be easily written down.
	 */
	
	// Cos/Sin of theta/phi of current orientation. Stored for more efficient calculations.
	double sinT;
	double cosT;
	double sinP;
	double cosP;

	// sin / cos of small angle delta. For more efficient rotation. Defaults to 0.05 rad. 
	double sinDelta = Math.sin(0.05);
	double cosDelta = Math.cos(0.05);

	public Observer(R3Point position, double theta, double phi, ZImage view, double fov) {

		
		this.view = view;
		this.position = new R3Point(position);
		this.fov = fov;

		setOrientation(theta, phi);

		ID = currentGreatestID++;
		IDMap.put(ID, this);
	}

	public R2Point lookAt(R3Point point) {
		return perspective(point).project(fov);
	}

	// Returns the position of the point from the observer's perspective
	public R3Point perspective(R3Point point) {
		return rotation.transform(point.difference(position));
	}

	// Rotates the point via the observer's rotation matrix
	public R3Point rotate(R3Point point) {
		return rotation.transform(point);
	}

	public ZImage getView() {
		return view;
	}

	public void setOrientation(double theta, double phi) {
		if (-Math.PI / 2 > phi || phi > Math.PI / 2) {
			throw new IllegalArgumentException();
		}

		sinT = Math.sin(theta);
		cosT = Math.cos(theta);
		sinP = Math.sin(phi);
		cosP = Math.cos(phi);

		rotation = new R3Matrix(cosT, -sinT * sinP, sinT * cosP, 0, cosP, sinP, -sinT, 
			-cosT * sinP, cosT * cosP);
	}

	// Rotates more efficiently compared to recalculating sin/cos.
	public void increment(String direction) {
		double temp;
		switch (direction) {
		case ("UP"):
			if (sinP >= 1) {
				return;
			}
			temp = cosP;
			cosP = cosP * cosDelta - sinP * sinDelta;
			sinP = sinP * cosDelta + temp * sinDelta;
			break;
		case ("DOWN"):
			if (sinP <= -1) {
				return;
			}
			temp = cosP;
			cosP = cosP * cosDelta + sinP * sinDelta;
			sinP = sinP * cosDelta - temp * sinDelta;
			break;
		case ("LEFT"):
			temp = cosT;
			cosT = cosT * cosDelta - sinT * sinDelta;
			sinT = sinT * cosDelta + temp * sinDelta;
			break;
		case ("RIGHT"):
			temp = cosT;
			cosT = cosT * cosDelta + sinT * sinDelta;
			sinT = sinT * cosDelta - temp * sinDelta;
			break;
		}
		rotation = new R3Matrix(cosT, -sinT * sinP, sinT * cosP, 0, cosP, sinP, -sinT, -cosT * sinP,
			cosT * cosP);
	}

	public void setPosition(R3Point position) {
		this.position = position;
	}

	public int getID() {
		return ID;
	}

	public static Observer get(int ID) {
		return IDMap.get(ID);
	}

	public double getFov() {
		return fov;
	}
	// Everything past this point is drawing methods

	// Draws a point
	public ZFigure point(R3Point p, int shade) {

		ZFigure point = new ZFigure();

		if (p.getForward() > Constants.EPSILON) {
			point.add(p.project(fov, shade));
		}
		return point;
	}

	// Observer assumed to be in default state, p1 assumed to be further back than
	// p2.
	public ZFigure lineDefaultAuxiliary(R3Point p1, R3Point p2, int shade, int borderShade) {

		if (p1.getForward() > Constants.EPSILON) {
			return view.borderedLine(p1.project(fov, shade), p2.project(fov, shade), borderShade);
		}

		if (p2.getForward() > Constants.EPSILON) {
			R3Point start = p1.forward0Intersection(p2);

			return view.borderedLine(start.project(fov, shade), p2.project(fov, shade), borderShade);

		}

		return new ZFigure();
	}

	// Observer assumed to be in default state.
	public void lineDefault(R3Point p1, R3Point p2, int shade, int borderShade) {

		if (p2.getForward() > p1.getForward()) {
			view.draw(lineDefaultAuxiliary(p1, p2, shade, borderShade));
		}
		view.draw(lineDefaultAuxiliary(p2, p1, shade, borderShade));
	}

	public void line(R3Point p1, R3Point p2, int shade, int borderShade) {

		lineDefault(perspective(p1), perspective(p2), shade, borderShade);
	}

	/*
	 * Points assumed to lie on a plane. Will lead to visual artifacts otherwise
	 */
	public void polygon(ArrayList<R3Point> points, int shade, int polygonID, boolean writeToStencil) {

		ArrayList<ZPixel> viewedPolygon = new ArrayList<ZPixel>();

		int length = points.size();

		for (int i = 0; i < length; i++) {

			R3Point curr = points.get(i);
			R3Point next = points.get((i + 1) % length);

			double currF = curr.getForward();
			double nextF = next.getForward();

			if (currF > Constants.EPSILON) {
				viewedPolygon.add(curr.project(fov, shade, polygonID));
			}

			// If currF and nextF differ in sign, this intersection is added.
			if (currF > Constants.EPSILON ^ nextF > Constants.EPSILON) {
				viewedPolygon.add(curr.forward0Intersection(next).project(fov, shade, polygonID));
			}
		}

		view.polygon(viewedPolygon, writeToStencil);
	}

	public void renderDirectly(RelativeComponent component) {
		component.viewed();
	}
	
	public void renderDirectly(Form form) {
		for (RelativeComponent face : form.components) {
			renderDirectly(face);
		}
	}

	public void renderDirectly(Mesh mesh) {
		for (RelativePolygon face : mesh.faces) {
			renderDirectly(face);
		}
	}
}
