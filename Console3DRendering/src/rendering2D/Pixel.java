package rendering2D;

public class Pixel {

	private int right;
	private int down;
	private int shade;
	
	public Pixel(int right, int down, int shade) {
		if (shade < 0 || shade > ShadeHandling.getMaxPossibleShade()) {
			throw new IllegalArgumentException();
		}
		this.right = right;
		this.down = down;
		this.shade = shade;
	}

	public Pixel() {
		right = 0;
		down = 0;
		shade = 0;
	}

	public Pixel(Pixel pixel) {
		right = pixel.right;
		down = pixel.down;
		shade = pixel.shade;
	}

	
	public int getRight() {
		return right;
	}

	public void moveRight(int delta) {
		right += delta;
	}
	public void moveDown(int delta) {
		down += delta;
	}
	
	public int getDown() {
		return down;
	}

	public void translate(Pixel pixel) {
		right += pixel.right;
		down += pixel.down;
	}
	public int getShade() {
		return shade;
	}
	
	public String toString() {
		return right + " " + down + " | " + shade;
	}

	public void setShade(int shade) {
		this.shade = shade;
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
