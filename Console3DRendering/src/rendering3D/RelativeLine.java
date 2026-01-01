package rendering3D;

import classes3D.R3Point;
import rendering2D.ShadeHandling;
import zBuffered2DRendering.ZFigure;

public class RelativeLine extends RelativeSimplex {

	// Lines require a second integer to specify the shade of it's border when rendered
	private int borderShade;
	
	// These fields are immutable and represent the actual position of the line.
	private R3Point pointA;
	private R3Point pointB;

	// These fields are mutable and represent the perceived position of the line.
	private R3Point perceivedPointA;
	private R3Point perceivedPointB;

	public RelativeLine(R3Point pointA, R3Point pointB, int shade, int borderShade) {

		this.pointA = pointA;
		this.pointB = pointB;

		perceivedPointA = pointA;
		perceivedPointB = pointB;

		this.shade = shade;
		this.borderShade = borderShade;
	}

	public RelativeLine(R3Point pointA, R3Point pointB) {
		this(pointA, pointB, ShadeHandling.getMaxPossibleShade(), 0);
	}

	/*
	 * Unique added here to avoid the line overlapping itself. This is a temporary
	 * solution. I eventually plan on reworking the .replace method to work on
	 * figures with non unique points. That or rework the line method to only return
	 * a figure with unique points
	 */
	public ZFigure viewedBy(Observer observer) {
		return observer.lineDefault(perceivedPointA, perceivedPointB, shade, borderShade);
	}

	public void determineMostAndLeastForward() {
		
		leastForward = Math.min(perceivedPointA.getForward(), perceivedPointB.getForward());
		mostForward = Math.max(perceivedPointA.getForward(), perceivedPointB.getForward());
	}

	public void updatePerspective(Observer observer) {
		
		perceivedPointA = observer.perspective(pointA);
		perceivedPointB = observer.perspective(pointB);
		
		determineMostAndLeastForward();
	}
}
