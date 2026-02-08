package rendering3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes2D.R2Point;
import classes3D.R3Point;
import other.Constants;
import texturing.PolygonTexture;
import texturing.Texture;
import zBuffered2DRendering.ZFigure;
import zBuffered2DRendering.ZPixel;

public class RelativePolygon extends RelativeComponent {

	PolygonTexture texture;

	/*
	 * Stores the points using two variables u and v representing their position on
	 * the plane. Rotation and translation invariant
	 */
	protected ArrayList<R2Point> uVPoints;

	// Stores the vertices of the polygon as viewed by the observer.
	protected ArrayList<R3Point> perceivedPoints;

	// Represents the orientation of the actual polygon.
	protected R3Point orientation;

	// Represents the offset of the plane the polygon lies on from the origin
	protected R3Point offset;

	// Vectors that specify the directions of the plane. Forms an orthogonal basis with orientation.
	protected R3Point vectorA;
	protected R3Point vectorB;

	protected R3Point perceivedOffset;
	protected R3Point perceivedVectorA;
	protected R3Point perceivedVectorB;

	/*
	 * vA/BFis perceivedVectorA/B's forward component multiplied by the FOV of whatever
	 * observer is associated with this polygon. 
	 * 
	 * vABDotOffset is (-percievedVectorA dot perceivedOffset,-percievedVectorB dot perceivedOffset).
	 * 
	 * These variables are stored to cut down operations for the findUV method.
	 */
	private double vAF;
	private double vBF;
	private R2Point vABDotOffset;

	/*
	 * Polygon assumed to lie entirely on some plane and be convex.
	 */
	public RelativePolygon(Observer observer, Texture texture, List<R3Point> points) {

		super(observer); 
		
		if (points.size() < 3) {
			throw new IllegalArgumentException("Polygon must have at least 3 points");
		}
		
		this.perceivedPoints = new ArrayList<R3Point>(points);
		
		offset = points.get(0);
		
		vectorA = points.get(1).difference(offset);
		
		orientation = points.get(1).difference(offset).cross(points.get(2).difference(offset));
		
		orientation.normalize();
	
		
		try {
			
			vectorA = orientation.cross(new R3Point(0, -1, 0));
			vectorA.normalize();
			vectorB = vectorA.cross(orientation);
			
		} catch (ArithmeticException e) {
			// Code only gets here if the polygon is orthogonal to the vertical axis
			
			vectorA = new R3Point(1,0,0);
			vectorB = new R3Point(0,0,1);
		}

		uVPoints = new ArrayList<R2Point>();

		for (int i = 0; i < points.size(); i++) {

			R3Point adjusted = points.get(i).difference(offset);

			// Checks if all points lie on the same plane
			if (adjusted.dot(orientation) > Constants.EPSILON) {
				throw new IllegalArgumentException("All points must lie in one plane");
			}
			uVPoints.add(new R2Point(adjusted.dot(vectorA), adjusted.dot(vectorB)));

		}
		
		this.texture = new PolygonTexture(uVPoints, texture);
		
		updatePerspective();


	}
	public RelativePolygon(Observer observer, Texture texture, R3Point...points) {
		this(observer,texture,Arrays.asList(points));
	}

	public void findUVVariables() {

		Observer observer = getObserver();

		vAF = perceivedVectorA.getForward() * observer.getFov();
		vBF = perceivedVectorB.getForward() * observer.getFov();

		vABDotOffset = new R2Point(-perceivedVectorA.dot(perceivedOffset), -perceivedVectorB.dot(perceivedOffset));
		
	}

	public void determineMostAndLeastForward() {

		mostForward = perceivedPoints.get(0).getForward();
		leastForward = mostForward;

		double currForward;

		for (int i = 1; i < perceivedPoints.size(); i++) {

			currForward = perceivedPoints.get(i).getForward();

			if (currForward > mostForward) {
				mostForward = currForward;
			} else if (currForward < leastForward) {
				leastForward = currForward;
			}
		}
	}

	// Updates the 3 vectors that determine the plane.
	public void updatePlaneVectors() {
		Observer observer = getObserver();
		
		perceivedVectorA = observer.rotate(vectorA);
		perceivedVectorB = observer.rotate(vectorB);
		perceivedOffset = observer.perspective(offset);
	}
	
	public void updatePerspective() {

		updatePlaneVectors();

		R2Point curr;

		for (int i = 0; i < uVPoints.size(); i++) {
			curr = uVPoints.get(i);

			perceivedPoints.set(i,
				R3Point.linearCombination(curr.getRight(), curr.getDown(), perceivedVectorA, perceivedVectorB));
		}

		for (R3Point point : perceivedPoints) {
			point.translate(perceivedOffset);
		}

		findUVVariables();
	}

	/*
	 * Given a pixel that is assumed to be projected by a given observer and comes
	 * from this polygon, finds (an approximation of) the coordinates of this points
	 * in u-v space.
	 */
	public R2Point findUV(int right, int down, double zBuffer) {

		R2Point uvPoint = new R2Point(
			perceivedVectorA.getRight() * right + perceivedVectorA.getDown() * down + vAF,
			perceivedVectorB.getRight() * right + perceivedVectorB.getDown() * down + vBF);

		
		uvPoint.scale(1/zBuffer);
		
		uvPoint.translate(vABDotOffset);
		
		return uvPoint;
	}

	public int determineShade(int right, int down, double zBuffer) {
		return texture.determineShadeAt(findUV(right, down, zBuffer));
	}
	
	public ZFigure viewed() {
		
		ZFigure polygon = getObserver().polygon(perceivedPoints, 1);

		polygon.change(pixel -> ((ZPixel) pixel).setPolygonID(getID()));
		
		return polygon;
	}

	// Returns the outward pointing unit normal vector of the triangle.
	public RelativeLine getUnitNormal() {

		R2Point uVVectorTail = new R2Point();

		for (R2Point point : uVPoints) {
			uVVectorTail.translate(point);
		}

		uVVectorTail.scale(1 / (double) uVPoints.size());

		R3Point vectorTail = R3Point.linearCombination(
			uVVectorTail.getRight(), uVVectorTail.getDown(), vectorA, vectorB);
		
		vectorTail.translate(offset);
		
		R3Point vectorTip = new R3Point(vectorTail);

		vectorTip.translate(orientation);

		return new RelativeLine(vectorTail, vectorTip, getObserver());
	}
}
