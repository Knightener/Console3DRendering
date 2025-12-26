package classes3D;

public class Line {
	
	private R3Point pointA;
	private R3Point pointB;
	
	public Line(R3Point pointA, R3Point pointB) {
		this.pointA = pointA;
		this.pointB = pointB;
	}
	
	public R3Point getPointA() {
		return new R3Point(pointA);
	}
	
	public R3Point getPointB() {
		return new R3Point(pointB);
	}
	

}
