package rendering3D;

import java.util.ArrayList;

import classes3D.LightSource;
import classes3D.R3Point;
import classes3D.Triangle;
import functionalInterfaces.R3Norm;
import other.MiscFunctions;
import rendering2D.ShadeHandling;
import zBuffered2DRendering.ZFigure;

public class RelativePolygon extends RelativeSimplex {

	int shade;

	protected ArrayList<R3Point> points;
	protected ArrayList<R3Point> perceivedPoints;
	
	// Represents the orientation of the actual polygon.
	protected R3Point orientation;

	protected RelativePolygon() {

	}

	/*
	 * Polygon assumed to lie entirely on some plane and be convex. The latter
	 * condition would be quite expensive to check an throw an exception for, so it
	 * is not checked. However, adding a non convex polygon would lead to visual
	 * artifacts.
	 */
	public RelativePolygon(ArrayList<R3Point> points, int shade) {

		if (points.size() < 3) {
			throw new IllegalArgumentException("Polygon must have at least 3 points");
		}

		this.points = new ArrayList<R3Point>(points);
		this.perceivedPoints = new ArrayList<R3Point>(points);
		
		orientation = points.get(1).difference(points.get(0)).cross(points.get(2).difference(points.get(0)));
		
		orientation.normalize(R3Norm.EUCLIDIAN);
		
		// Checks if the polygon lies on a plane. 
		for (int i = 3; i < points.size(); i++) {
			if (!MiscFunctions.nearlyEquals(orientation.dot(points.get(i)), 0)) {
				
				throw new IllegalArgumentException("Polygon must lie on a plane");
			}
		}
		
	}

	public RelativePolygon(Triangle triangle, int shade) {

		points = new ArrayList<R3Point>();

		points.add(new R3Point(triangle.getPointA()));
		points.add(new R3Point(triangle.getPointB()));
		points.add(new R3Point(triangle.getPointC()));

		perceivedPoints = new ArrayList<R3Point>(points);
		
		orientation = triangle.getOrientation();

		this.shade = shade;
	}

	public RelativePolygon(Triangle triangle, LightSource lightSource) {
		this(triangle, lightSource.shade(triangle));
	}

	public RelativePolygon(R3Point pointA, R3Point pointB, R3Point pointC, int shade) {
		this(new Triangle(pointA, pointB, pointC), shade);
	}

	public RelativePolygon(R3Point pointA, R3Point pointB, R3Point pointC, LightSource lightSource) {
		this(new Triangle(pointA, pointB, pointC), lightSource);
	}

	public void determineMostAndLeastForward() {
		
		mostForward = perceivedPoints.get(0).getForward();
		leastForward = mostForward;

		double currForward;

		for (int i = 1; i < points.size(); i++) {

			currForward = points.get(i).getForward();

			if (currForward > mostForward) {
				mostForward = currForward;
			} else if (currForward < leastForward) {
				leastForward = currForward;
			}
		}
	}

	public void updatePerspective(Observer observer) {
		
		
		for(int i = 0; i < points.size(); i++) {
			perceivedPoints.set(i, observer.perspective(points.get(i)));
		}
		
	}
	
	public ZFigure viewedBy(Observer observer) {
		return observer.polygon(perceivedPoints, shade);	
	}

	// Returns the outward pointing unit normal vector of the triangle.
	public RelativeLine getUnitNormal() {
		
		R3Point vectorTail = new R3Point();

		for (R3Point point : points) {
			vectorTail.translate(point);
		}

		vectorTail.scale(1 / (double) points.size());

		R3Point vectorTip = new R3Point(vectorTail);		
		
		vectorTip.translate(orientation);

		return new RelativeLine(vectorTail, vectorTip);
	}
}
