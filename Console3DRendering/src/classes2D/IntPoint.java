package classes2D;

public class IntPoint {

	protected int right;
	protected int down;

	public IntPoint(int right, int down) {
		this.right = right;
		this.down = down;
	}

	public IntPoint() {
		this(0, 0);
	}
	
	public IntPoint(IntPoint point) {
		right = point.right;
		down = point.down;
	}
	
	public void moveRight(int delta) {
		right += delta;
	}
	
	public void moveDown(int delta) {
		down += delta;
	}
	
	public int getRight() {
		return right;
	}

	public int getDown() {
		return down;
	}
	
	public String toString() {
		return right + " " + down;
	}
	
	public void flip() {
		int rightCopy = right;
		right = down;
		down = rightCopy;
	}
	
	public void scale(int r) {
		right *= r;
		down *= r;
	}

	public void setRight(int right) {
		this.right = right;
	}

	public void setDown(int down) {
		this.down = down;
	}
	
}
