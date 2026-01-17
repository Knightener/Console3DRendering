package rendering3D;

import classes3D.R3Point;
import rendering2D.ShadeHandling;
import zBuffered2DRendering.ZFigure;

public class RelativePoint extends RelativeSimplex {

	private R3Point point;
	R3Point perceived;
	
	public RelativePoint(R3Point point, Observer observer) {
		
		super(observer);
		
		this.point = new R3Point(point);
		perceived = point;
	}
	
	public void determineMostAndLeastForward() {
		mostForward = point.getForward();
		leastForward = point.getForward();
	}
	
	public R3Point getPoint() {
		return new R3Point(point);
	}
	
	public void updatePerspective() {
		perceived = getObserver().perspective(point);
	}
	
	public ZFigure viewed() {
		return getObserver().point(perceived, ShadeHandling.getMaxPossibleShade());
	}

}
