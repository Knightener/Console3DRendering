package zBuffered2DRendering;

import rendering2D.*;

public class ZPixel extends Pixel {
	
	public double zBuffer;

	// ID of the polygon from which the pixel came from (if it came from a polygon).
	public int renderInfo;

	// 0000 0000 1111 1111 1111 1111 1111 1111. renderInfo & polygonBits = renderInfo of pixel. 
	public static final int POLYGON_BITS = (1 << 24) - 1;
	
	// 0000 0001 0000 0000 0000 0000 0000 0000. First bit for shadow info.
	public static final int SHADE_BIT = 1 << 24;
	
	// renderInfo >>> SHADE_BIT_POS = shadow value. 
	public static final int SHADE_BIT_POS = 24;

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
		this.renderInfo = polygonID;
	}

	public ZPixel(ZPixel pixel) {
		super(pixel);
		zBuffer = pixel.zBuffer;
		renderInfo = pixel.renderInfo;
	}

	public ZPixel(Pixel pixel, double zBuffer) {
		super(pixel);
		this.zBuffer = zBuffer;
	}
	
	public void setRenderInfo(int polygonID) {
		this.renderInfo = polygonID;
	}
	
	public int getRenderInfo() {
		return renderInfo;
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
