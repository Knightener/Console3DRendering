package rendering3D;

import classes3D.R3Point;
import rendering2D.ShadeHandling;
import zBuffered2DRendering.ZFigure;

public class RelativePoint extends RelativeSimplex {

	private R3Point point;
	R3Point perceived;
	
	public RelativePoint(R3Point point) {
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
	
	public void updatePerspective(Observer observer) {
		perceived = observer.perspective(point);
	}
	
	public ZFigure viewedBy(Observer observer) {
		return observer.point(perceived, ShadeHandling.getMaxPossibleShade());
	}
	
}
