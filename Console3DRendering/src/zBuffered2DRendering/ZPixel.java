package zBuffered2DRendering;

import rendering2D.*;

public class ZPixel extends Pixel {
	
	public double zBuffer;

	public ZPixel() {
		super();
		zBuffer = 0;
	}

	public ZPixel(int right, int down, int shade, double zBuffer) {
		super(right, down, shade);
		this.zBuffer = zBuffer;
	}

	public ZPixel(ZPixel pixel) {
		super(pixel);
		zBuffer = pixel.zBuffer;
	}

	public void setZBuffer(double zBuffer) {
		this.zBuffer = zBuffer;
	}

	public double getZBuffer() {
		return zBuffer;
	}
	
	public void incrementZBuffer(double delta) {
		zBuffer += delta;
	}

	@Override
	public String toString() {
		return super.toString() + " | " + zBuffer;
	}
}
