package classes2D;

public class IntPoint {

	protected int x;
	protected int y;

	public IntPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public IntPoint() {
		this(0, 0);
	}
	
	public IntPoint(IntPoint point) {
		x = point.x;
		y = point.y;
	}
	
	public void incrementX(int delta) {
		x += delta;
	}
	
	public void incrementY(int delta) {
		y += delta;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return x + " " + y;
	}
	
	public void flip() {
		int rightCopy = x;
		x = y;
		y = rightCopy;
	}
	
	public void scale(int r) {
		x *= r;
		y *= r;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IntPoint)) {
			return false;
		}
		IntPoint point = (IntPoint) obj;
		return (point.x == x) && (point.y == y);
	}

	public boolean oppositeEquals(IntPoint point) {
		return (point.x == y) && (point.y == x);
	}

	public boolean unorderedEquals(IntPoint point) {
		return oppositeEquals(point) || equals(point);
	}
}
