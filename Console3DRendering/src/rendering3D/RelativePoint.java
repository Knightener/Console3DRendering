package rendering3D;

import classes3D.R3Point;
import rendering2D.ShadeHandling;

public class RelativePoint extends RelativeComponent {

	private R3Point point;
	R3Point perceived;
	
	public RelativePoint(R3Point point, Observer observer) {
		
		super(observer);
		
		this.point = new R3Point(point);
		perceived = point;
	}
	
	public void determineMostAndLeastForward() {
		mostForward = point.getZ();
		leastForward = point.getZ();
	}
	
	public R3Point getPoint() {
		return new R3Point(point);
	}
	
	public void updatePerspective() {
		perceived = observer.perspective(point);
	}
	
	public void render() {
		observer.point(perceived, ShadeHandling.MAX_SHADE);
	}

}
