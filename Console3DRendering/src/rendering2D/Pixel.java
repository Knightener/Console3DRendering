package rendering2D;

import classes2D.IntPoint;

public class Pixel extends IntPoint {

	private int shade;

	public Pixel(int right, int down, int shade) {
		super(right, down);
		if (shade < 0 || shade > ShadeHandling.getMaxPossibleShade()) {
			throw new IllegalArgumentException();
		}
		this.shade = shade;
	}

	public Pixel() {
		super();
		shade = 0;
	}

	public Pixel(Pixel pixel) {
		super(pixel);
		shade = pixel.shade;
	}

	public void translate(Pixel pixel) {
		right += pixel.right;
		down += pixel.down;
	}
	public int getShade() {
		return shade;
	}
	
	@Override
	public String toString() {
		return super.toString() + " | " + shade;
	}

	public void setShade(int shade) {
		this.shade = shade;
	}
	
	
	
	

}
