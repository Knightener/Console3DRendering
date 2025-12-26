package classes3D;

import functionalInterfaces.R3Norm;

public class Triangle {

	private R3Point pointA;
	private R3Point pointB;
	private R3Point pointC;
	
	/*
	 * Orientation is a unit vector normal to the polygon formed by perceivedPointA, perceivedPointB,
	 * perceivedPointC. 
	 * 
	 * The vector represents which face of the polygon is up.
	 */
	private R3Point orientation;

	public Triangle(R3Point pointA, R3Point pointB, R3Point pointC){
		this.pointA = pointA;
		this.pointB = pointB;
		this.pointC = pointC;
		
		orientation = pointB.difference(pointA).cross(pointC.difference(pointA));
		orientation.normalize(R3Norm.EUCLIDIAN);
	}

	public R3Point getPointA() {
		return new R3Point(pointA);
	}

	public R3Point getPointB() {
		return new R3Point(pointB);
	}

	public R3Point getPointC() {
		return new R3Point(pointC);
	}

	public R3Point getOrientation() {
		return new R3Point(orientation);
	}
	
	
	
}
