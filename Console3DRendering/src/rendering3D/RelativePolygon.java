package rendering3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes2D.R2Point;
import classes3D.LightSource;
import classes3D.R3Point;
import classes3D.Triangle;
import functionalInterfaces.R3Norm;
import zBuffered2DRendering.ZFigure;

public class RelativePolygon extends RelativeSimplex {

	int shade;

	// Unique integer assigned to each polygon.
	private int ID;
	
	private static int currentGreatestID = 1;
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
	
	protected RelativePolygon() {

	}

	/*
	 * Polygon assumed to lie entirely on some plane and be convex. 
	 */
	public RelativePolygon(List<R3Point> points, int shade) {

		if (points.size() < 3) {
			throw new IllegalArgumentException("Polygon must have at least 3 points");
		}

		this.shade = shade;
		
		this.perceivedPoints = new ArrayList<R3Point>(points);
		
		offset = points.get(0);
		
		vectorA = points.get(1).difference(offset);
		
		orientation = vectorA.cross(points.get(2).difference(offset));
		
		orientation.normalize(R3Norm.EUCLIDIAN);
		vectorA.normalize(R3Norm.EUCLIDIAN);

		vectorB = vectorA.cross(orientation);

		uVPoints = new ArrayList<R2Point>();

		perceivedVectorA = new R3Point(vectorA);
		perceivedVectorB = new R3Point(vectorB);
		perceivedOffset = new R3Point(offset);
		
		ID = currentGreatestID++;
		
		for (int i = 0; i < points.size(); i++) {

			R3Point adjusted = points.get(i).difference(offset);
			
			uVPoints.add(new R2Point(adjusted.dot(vectorA),adjusted.dot(vectorB)));
			
		}	
	}

	public RelativePolygon(R3Point pointA, R3Point pointB, R3Point pointC, int shade) {
		this(Arrays.asList(pointA, pointB, pointC), shade);
	}

	public int getID() {
		return ID;
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

	public void updatePerspective(Observer observer) {

		perceivedVectorA = observer.rotate(vectorA);
		perceivedVectorB = observer.rotate(vectorB);
		perceivedOffset = observer.perspective(offset);

		R2Point curr;

		for (int i = 0; i < uVPoints.size(); i++) {
			curr = uVPoints.get(i);

			perceivedPoints.set(i,
				R3Point.linearCombination(curr.getRight(), curr.getDown(), perceivedVectorA, perceivedVectorB));
		}

		for (R3Point point : perceivedPoints) {
			point.translate(perceivedOffset);
		}

	}

	public ZFigure viewedBy(Observer observer) {
		return observer.polygon(perceivedPoints, shade);
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

		return new RelativeLine(vectorTail, vectorTip);
	}
}
