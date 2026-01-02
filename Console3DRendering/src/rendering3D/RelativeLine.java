package rendering3D;

import classes3D.R3Point;
import rendering2D.ShadeHandling;
import zBuffered2DRendering.ZFigure;

public class RelativeLine extends RelativeSimplex {

	/*
	 * Lines require a second integer to specify the shade of it's border when
	 * rendered. This is to make them clearly visible amongst triangles
	 */
	int shade;
	private int borderShade;

	private RelativePoint pointA;
	private RelativePoint pointB;

	public RelativeLine(R3Point pointA, R3Point pointB, int shade, int borderShade) {

		this.pointA = new RelativePoint(pointA);
		this.pointB = new RelativePoint(pointB);
		
		this.shade = shade;
		this.borderShade = borderShade;
	}

	public RelativeLine(R3Point pointA, R3Point pointB) {
		this(pointA, pointB, ShadeHandling.getMaxPossibleShade(), 0);
	}

	public ZFigure viewedBy(Observer observer) {
		return observer.lineDefault(pointA.perceived, pointB.perceived, shade, borderShade);
	}

	public void determineMostAndLeastForward() {
		
		leastForward = Math.min(pointA.mostForward,pointB.mostForward);
		mostForward = Math.max(pointA.mostForward,pointB.mostForward);
	}

	public void updatePerspective(Observer observer) {

		pointA.updatePerspective(observer);
		pointB.updatePerspective(observer);
		
		determineMostAndLeastForward();
	}
}
