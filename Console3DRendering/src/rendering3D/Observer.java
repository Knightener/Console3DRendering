package rendering3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import classes2D.R2Point;
import classes3D.R3Matrix;
import classes3D.R3Point;
import control.RotationDirection;
import control.TranslationDirection;
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
	 * matrices for theta around the vertical axis, and phi around the relative left axis.
	 */
	
	// Cos/Sin of theta/phi of current orientation. Stored for more efficient calculations.
	double sinT;
	double cosT;
	double sinP;
	double cosP;

	public Observer(R3Point position, double theta, double phi, ZImage view, double fov) {
		this.view = view;
		this.position = new R3Point(position);
		this.fov = fov;

		setOrientation(theta, phi);

		ID = currentGreatestID++;
		IDMap.put(ID, this);
	}

	public Observer(ZImage view, double fov) {
		this(new R3Point(0, 0, 0), 0, 0, view, fov);
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
			throw new IllegalArgumentException("phi must be within [-pi/2 , pi/2]");
		}

		sinT = Math.sin(theta);
		cosT = Math.cos(theta);
		sinP = Math.sin(phi);
		cosP = Math.cos(phi);

		rotation = new R3Matrix(cosT, 0, sinT, -sinT * sinP, cosP, cosT * sinP, -sinT * cosP, -sinP,
			cosT * cosP);
	}

	/*
	 * Turns the observer left/right/up/down, depending on the specified direction.
	 */
	public void turn(RotationDirection rotationDirection, double cosDelta, double sinDelta) {
		double temp;
		switch (rotationDirection) {
		case UP:
			temp = sinP;
			// If phi >= pi/2, does nothing.
			sinP = sinP * cosDelta + cosP * sinDelta;
			cosP = cosP * cosDelta - temp * sinDelta;
			break;
		case DOWN:
			// If phi <= -pi/2, does nothing.
			temp = sinP;
			sinP = sinP * cosDelta - cosP * sinDelta;
			cosP = cosP * cosDelta + temp * sinDelta;
			break;
		case LEFT:
			temp = cosT;
			cosT = cosT * cosDelta - sinT * sinDelta;
			sinT = sinT * cosDelta + temp * sinDelta;
			break;
		case RIGHT:
			temp = cosT;
			cosT = cosT * cosDelta + sinT * sinDelta;
			sinT = sinT * cosDelta - temp * sinDelta;
			break;
		}
		rotation.set(cosT, 0, sinT, -sinT * sinP, cosP, cosT * sinP, -sinT * cosP, -sinP,
			cosT * cosP);
	}

	public void move(TranslationDirection translationDirection, double delta) {
		switch (translationDirection) {
		case LEFT:
			position.incrementRight(-delta * cosT);
			position.incrementForward(-delta * sinT);
			break;
		case RIGHT:
			position.incrementRight(delta * cosT);
			position.incrementForward(delta * sinT);
			break;
		case BACKWARDS:
			position.incrementRight(delta * sinT);
			position.incrementForward(-delta * cosT);
			break;
		case FORWARDS:
			position.incrementRight(-delta * sinT);
			position.incrementForward(delta * cosT);
			break;
		case UP:
			position.incrementDown(-delta);
			break;
		case DOWN:
			position.incrementDown(delta);
			break;
		}
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

	// Observer assumed to be in default state. Draws a point.
	public void point(R3Point p, int shade) {
		if (p.getForward() > Constants.NEAR_EPSILON) {
			view.draw(p.project(fov, shade));
		}
	}

	// Observer assumed to be in default state, p1 assumed to be further back than
	// p2.
	public ZFigure lineDefaultAuxiliary(R3Point p1, R3Point p2, int shade, int borderShade) {

		if (p1.getForward() > Constants.NEAR_EPSILON) {
			return view.borderedLine(p1.project(fov, shade), p2.project(fov, shade), borderShade);
		}

		if (p2.getForward() > Constants.NEAR_EPSILON) {
			R3Point start = p1.forward0Intersection(p2);

			return view.borderedLine(start.project(fov, shade), p2.project(fov, shade), borderShade);

		}

		return new ZFigure();
	}

	// Observer assumed to be in default state.
	public void lineDefault(R3Point p1, R3Point p2, int shade, int borderShade) {

		if (p2.getForward() > p1.getForward()) {
			view.draw(lineDefaultAuxiliary(p1, p2, shade, borderShade));
		} else {
			view.draw(lineDefaultAuxiliary(p2, p1, shade, borderShade));
		}
	}

	public void line(R3Point p1, R3Point p2, int shade, int borderShade) {

		lineDefault(perspective(p1), perspective(p2), shade, borderShade);
	}

	/*
	 * Points assumed to lie on a plane. Will lead to visual artifacts otherwise
	 */
	public void polygon(List<R3Point> points, int shade, int polygonID, boolean writeToStencil,
		boolean isFacing) {

		ArrayList<ZPixel> viewedPolygon = new ArrayList<ZPixel>();

		int length = points.size();

		for (int i = 0; i < length; i++) {

			R3Point curr = points.get(i);
			R3Point next = points.get((i + 1) % length);

			double currF = curr.getForward();
			double nextF = next.getForward();

			if (currF > Constants.NEAR_EPSILON) {
				viewedPolygon.add(curr.project(fov, shade, polygonID));
			}

			// If currF and nextF differ in sign, this intersection is added.
			if (currF > Constants.NEAR_EPSILON ^ nextF > Constants.NEAR_EPSILON) {
				viewedPolygon.add(curr.forward0Intersection(next).project(fov, shade, polygonID));
			}
		}

		view.polygon(viewedPolygon, writeToStencil, isFacing);
	}
	
	public void printView() {
		view.texturize();
		view.display();
		view.clear();
	}
	
	// Prints the position and angle of the observer. 
	@Override
	public String toString() {
		return "Position: " + position.toString() + "  Orientation: " + "(" + Math.atan2(sinT, cosT)
			+ ", " + Math.atan2(sinP, cosP) + ")";
	}
}
