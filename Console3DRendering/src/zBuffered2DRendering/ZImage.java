package zBuffered2DRendering;

import other.MiscFunctions;
import rendering2D.*;

public class ZImage extends ImageBase {

	double[][] zBuffer;

	public ZImage(int leftEnd, int rightEnd, int upEnd, int downEnd) {
		super(leftEnd, rightEnd, upEnd, downEnd);
		zBuffer = new double[imageRows][imageCols];
		
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				zBuffer[i][j] = 1;
			}
		}

	}

	public ZImage(int[][] arr, int left, int up) {
		super(arr, left, up);
		zBuffer = new double[imageRows][imageCols];
		
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				zBuffer[i][j] = 1;
			}
		}
	}
	
	// Debug function. Returns an image with pixels colored according to their zBuffer
	public Image getZBufferImage(ShadeHandling shadeHandling) {
		
		Image image = new Image(this);
		
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				image.setShade(j, i, shadeHandling.determineShade(1 - zBuffer[i][j]));				
			}
		}
		
		return image;
	}

	public void draw(ZFigure figure) {
		
		int currRight;
		int currDown;
		double currZBuffer;
		
		for (ZPixel pixel : figure.figure) {

			currRight = pixel.getRight() - leftBound;
			currDown = pixel.getDown() - upBound;
			currZBuffer = pixel.getZBuffer();

			if (currRight >= 0 && currRight < imageCols && currDown >= 0 && currDown < imageRows
				&& zBuffer[currDown][currRight] > currZBuffer) {

				
				zBuffer[currDown][currRight] = currZBuffer;
				image[currDown][currRight] = pixel.getShade();
			}

		}
		
	}
	
	/*Similar to the method of the same name in the image class, but the zBuffer
	 * is linearly interpolated between points */
	public ZFigure lineWithoutHorizontalRepetition(ZPixel p1, ZPixel p2) {
		
		ZFigure line = new ZFigure();
	
		// difference
	
		int rightDist = p2.getRight() - p1.getRight();
		int downDif = p2.getDown() - p1.getDown();
	
		if (rightDist == 0 || p1.getRight() > rightBound) {
			return line;
		}
	
		// direction
		int downDir = MiscFunctions.sign(downDif);
	
		// distance
		int downDist = Math.abs(downDif);
	
		int minDownStep = downDif / rightDist;
	
		// remaining down distance that will need to be distributed across iterations.
		int excess = downDist % rightDist;
	
		int currMod = excess;
		
		int visibleLength = Math.min(rightDist, rightBound - p1.getRight());
		
		double zStep = (p2.getZBuffer() - p1.getZBuffer()) / rightDist;
		
		ZPixel movingPixel = new ZPixel(p1);
		
		for (int i = 0; i < visibleLength; i++) {
			line.add(new ZPixel(movingPixel));
			
			movingPixel.moveRight(1);
			movingPixel.moveDown(minDownStep);
			movingPixel.incrementZBuffer(zStep);
			
			currMod += excess;
			if (currMod >= rightDist) {
				currMod -= rightDist;
				movingPixel.moveDown(downDir);
			}
		}
	
		return line;
	}
	
	
	private ZFigure verticalLineAuxiliary(ZPixel p1, ZPixel p2) {
		ZFigure line = new ZFigure();
	
		ZPixel movingPixel = new ZPixel(p1);
		
		int downDist = p2.getDown() - p1.getDown();
		
		double zStep = (p2.getZBuffer() - p1.getZBuffer()) / downDist;
		
		if (upBound > p1.getDown()) {
			movingPixel.setDown(upBound);
			movingPixel.incrementZBuffer(zStep*(upBound - p1.getDown()));
		}
		int visibleLength = Math.min(p2.getDown() - movingPixel.getDown(), downBound - movingPixel.getDown() - 1);
	
		for (int i = 0; i <= visibleLength; i++) {
			line.add(new ZPixel(movingPixel));
			movingPixel.moveDown(1);
			movingPixel.incrementZBuffer(zStep);
		}
	
		return line;
	}
	
	public ZFigure verticalLine(ZPixel p1, ZPixel p2) {
		if (p1.getDown() < p2.getDown()) {
			return verticalLineAuxiliary(p1, p2);
		}
		return verticalLineAuxiliary(p2, p1);
	}
	
}
