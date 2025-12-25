package rendering3D;

import classes3D.*;
import other.MiscFunctions;
import rendering2D.ShadeHandling;

public class RelativeTriangle implements Comparable<RelativeTriangle> {

	R3Point pointA;
	R3Point pointB;
	R3Point pointC;
	
	R3Point perceivedPointA;
	R3Point perceivedPointB;
	R3Point perceivedPointC;
	
	private int shade;

	private double leastForward;
	private double mostForward;
	
	public RelativeTriangle(Triangle triangle, int shade) {
		
		pointA = triangle.getPointA();
		pointB = triangle.getPointB();
		pointC = triangle.getPointC();
		
		perceivedPointA = pointA;
		perceivedPointB = pointB;
		perceivedPointC = pointC;
		
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
	/*
	 * Since leastForward isn't independent of the observer, it is not set in the
	 * constructor
	 */
	public void determineMostAndLeastForward() {
		leastForward = Math.min(Math.min(perceivedPointA.getForward(), perceivedPointB.getForward()), perceivedPointC.getForward());
		mostForward = Math.max(Math.max(perceivedPointA.getForward(), perceivedPointB.getForward()), perceivedPointC.getForward());
	}

	/*
	 * The closer triangle is determined to be "greater". The further point is
	 * rendered first, and hence due to overlap, the triangle will appear to be in
	 * front of the other triangle
	 */
	public int compareTo(RelativeTriangle triangle) {

		if (!MiscFunctions.nearlyEquals(leastForward, triangle.leastForward)) {

			if (leastForward < triangle.leastForward) {
				return 1;
			}
			if (leastForward > triangle.leastForward) {
				return -1;
			}
		}
		
		// tiebreaker
		if (mostForward < triangle.mostForward) {
			return 1;
		}
		if (mostForward > triangle.mostForward) {
			return -1;
		}

		return 0;

	}


	public int getShade() {
		return shade;
	}

}
