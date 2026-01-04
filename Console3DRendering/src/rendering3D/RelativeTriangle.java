package rendering3D;

import classes3D.LightSource;
import classes3D.R3Point;
import classes3D.Triangle;
import rendering2D.ShadeHandling;
import zBuffered2DRendering.ZFigure;

public class RelativeTriangle extends RelativeSimplex {

	int shade;
	
	protected RelativePoint pointA;
	protected RelativePoint pointB;
	protected RelativePoint pointC;
	
	// Represents the orientation of the actual triangle. 
	protected R3Point orientation;

	protected RelativeTriangle() {
		
	}
	
	public RelativeTriangle(Triangle triangle, int shade) {
		
		pointA = new RelativePoint(triangle.getPointA());
		pointB = new RelativePoint(triangle.getPointB());
		pointC = new RelativePoint(triangle.getPointC());

		orientation = triangle.getOrientation();
		
		this.shade = shade;
	}

	public RelativeTriangle(Triangle triangle, LightSource lightSource) {
		this(triangle, lightSource.shade(triangle));
	}

	public RelativeTriangle(Triangle triangle) {
		this(triangle, ShadeHandling.getMaxPossibleShade());
	}

	public RelativeTriangle(R3Point pointA, R3Point pointB, R3Point pointC) {
		this(new Triangle(pointA, pointB, pointC));
	}

	public RelativeTriangle(R3Point pointA, R3Point pointB, R3Point pointC, int shade) {
		this(new Triangle(pointA, pointB, pointC), shade);
	}

	public RelativeTriangle(R3Point pointA, R3Point pointB, R3Point pointC, LightSource lightSource) {
		this(new Triangle(pointA, pointB, pointC), lightSource);
	}

	public void determineMostAndLeastForward() {
		leastForward = Math.min(Math.min(pointA.mostForward, pointB.mostForward), pointC.mostForward);
		mostForward = Math.max(Math.max(pointA.mostForward, pointB.mostForward), pointC.mostForward);
	}

	public void updatePerspective(Observer observer) {
		
		pointA.updatePerspective(observer);
		pointB.updatePerspective(observer);
		pointC.updatePerspective(observer);
		
		determineMostAndLeastForward();
	}
	
	public ZFigure viewedBy(Observer observer) {
		return observer.triangleDefault(pointA.perceived, pointB.perceived, pointC.perceived, shade);	
	}

	// Returns the outward pointing unit normal vector of the triangle.
	public RelativeLine getUnitNormal() {
		
		R3Point vectorTail = new R3Point();

		vectorTail.translate(pointA.getPoint());
		vectorTail.translate(pointB.getPoint());
		vectorTail.translate(pointC.getPoint());

		vectorTail.scale(1 / (double) 3);

		R3Point vectorTip = new R3Point(vectorTail);		
		
		vectorTip.translate(orientation);

		return new RelativeLine(vectorTail, vectorTip);
	}
}
