package zBuffered2DRendering;

import rendering2D.*;

public class ZPixel extends Pixel {
	
	public double zBuffer;

	// ID of the polygon from which the pixel came from (if it came from a polygon).
	public int polygonID;
	
	public ZPixel() {
		super();
		zBuffer = 0;
	}

	
	public ZPixel(int right, int down, int shade, double zBuffer) {
		super(right, down, shade);
		this.zBuffer = zBuffer;
	}

	public ZPixel(int right, int down, int shade, double zBuffer, int polygonID) {
		super(right, down, shade);
		this.zBuffer = zBuffer;
		this.polygonID = polygonID;
	}

	public ZPixel(ZPixel pixel) {
		super(pixel);
		zBuffer = pixel.zBuffer;
		polygonID = pixel.polygonID;
	}

	public ZPixel(Pixel pixel, double zBuffer) {
		super(pixel);
		this.zBuffer = zBuffer;
	}
	
	public void setPolygonID(int polygonID) {
		this.polygonID = polygonID;
	}
	
	public int getPolygonID() {
		return polygonID;
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
