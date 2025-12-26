package rendering3D;

import classes3D.*;
import other.MiscFunctions;
import rendering2D.ShadeHandling;
import rendering2D.Figure;

public class RelativeTriangle extends RelativeSimplex {

	// These fields are immutable and represent the actual position of the triangle
	R3Point pointA;
	R3Point pointB;
	R3Point pointC;
	
	// These fields are mutable and represent the perceived position of the triangle
	R3Point perceivedPointA;
	R3Point perceivedPointB;
	R3Point perceivedPointC;
	
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

	public void determineMostAndLeastForward() {
		leastForward = Math.min(Math.min(perceivedPointA.getForward(), perceivedPointB.getForward()), perceivedPointC.getForward());
		mostForward = Math.max(Math.max(perceivedPointA.getForward(), perceivedPointB.getForward()), perceivedPointC.getForward());
	}

	public int compareTo(RelativeTriangle triangle) {

		if (!MiscFunctions.nearlyEquals(leastForward, triangle.leastForward)) {

			if (leastForward < triangle.leastForward) {
				return 1;
			}
			if (leastForward > triangle.leastForward) {
				return -1;
			}
		}
		
		// Tie breaker
		if (mostForward < triangle.mostForward) {
			return 1;
		}
		if (mostForward > triangle.mostForward) {
			return -1;
		}

		return 0;

	}

	public void updatePerspective(Observer observer) {
		
		perceivedPointA = observer.perspective(pointA);
		perceivedPointB = observer.perspective(pointB);
		perceivedPointC = observer.perspective(pointC);
		
		determineMostAndLeastForward();
	}
	
	public Figure viewedBy(DrawingMethods3D observer) {
		return observer.triangleDefault(perceivedPointA, perceivedPointB, perceivedPointC, shade, false);	
	}

}
