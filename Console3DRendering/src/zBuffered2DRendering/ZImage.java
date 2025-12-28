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

	public void clear() {
		image = new int[imageRows][imageCols];
		
		zBuffer = new double[imageRows][imageCols];
		
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				zBuffer[i][j] = 1;
			}
		}
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
	
	/*
	 * Similar to the method of the same name in the image class, but the zBuffer is
	 * linearly interpolated between points
	 */
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
	
	private ZFigure verticalLineAuxiliary(ZPixel p1, ZPixel p2, double zStep) {
		ZFigure line = new ZFigure();

		ZPixel movingPixel = new ZPixel(p1);
		
		if (upBound > p1.getDown()) {
			movingPixel.setDown(upBound);
			movingPixel.incrementZBuffer(zStep * (upBound - p1.getDown()));
		}
		int visibleLength = Math.min(p2.getDown() - movingPixel.getDown(), downBound - movingPixel.getDown() - 1);

		for (int i = 0; i <= visibleLength; i++) {
			line.add(new ZPixel(movingPixel));
			movingPixel.moveDown(1);
			movingPixel.incrementZBuffer(zStep);
		}

		return line;
	}

	/*
	 * Similar to the method of the same name in the Image class. zStep is the slope
	 * delta zBuffer / delta down. This can be calculated easily, however, it is not
	 * calculated within the method and instead provided for the method call to
	 * optimize the jaggedTriangle method.
	 */
	public ZFigure verticalLine(ZPixel p1, ZPixel p2, double zStep) {
		if (p1.getDown() < p2.getDown()) {
			return verticalLineAuxiliary(p1, p2, zStep);
		}
		return verticalLineAuxiliary(p2, p1, zStep);
	}

	public ZFigure jaggedTriangleAuxiliary(ZPixel p1, ZPixel p2, ZPixel p3) {

		int r1 = p1.getRight();
		int r2 = p2.getRight();
		int r3 = p3.getRight();

		int d1 = p1.getDown();
		int d2 = p2.getDown();
		int d3 = p3.getDown();
	
		double z1 = p1.getZBuffer();
		double z2 = p2.getZBuffer();
		double z3 = p3.getZBuffer();

		int shade = p1.getShade();
		
		/*
		 * This is just the slope of the line (delta zBuffer / delta down) formed by
		 * intersection of the plane determined by the triangle and the plane(s) right =
		 * x for any x (it is independent of x). The derivation is not particularly
		 * interesting
		 */
		double slope = ((z2 - z1) * (r3 - r1) - (z3 - z1) * (r2 - r1))
			/ ((d2 - d1) * (r3 - r1) - (d3 - d1) * (r2 - r1));

		ZFigure triangle = new ZFigure();

		/*
		 * No part of the triangle is visible because the whole triangle is further
		 * right [resp left] than the rightmost [resp leftmost] visible point.
		 */
		if (r1 > rightBound || r3 < leftBound) {
	
			return triangle;
		}
	
		/*
		 * If the method gets this far, the leftmost point must have a horizontal
		 * component within the range visible by image.
		 */
		if (r1 > leftBound) {

			ZFigure line23 = lineWithoutHorizontalRepetition(p2, p3);
			ZFigure line13 = lineWithoutHorizontalRepetition(p1, p3);
			ZFigure line12 = lineWithoutHorizontalRepetition(p1, p2);

			for (int i = 0; i < line12.size(); i++) {
				triangle.add(verticalLine(line13.get(i), line12.get(i), slope));

			}

			for (int i = 0; i < line23.size(); i++) {
				triangle.add(verticalLine(line13.get(i + line12.size()), line23.get(i), slope));

			}

			return triangle;
		}

		/*
		 * If the method gets this far, the leftmost point is outside the visible range
		 * but the middle point is within it.
		 */
		if (r2 > leftBound) {

			double ratio12 = ((double)(leftBound - r1))/(r2-r1);
			double ratio13 = ((double)(leftBound - r1))/(r3-r1);

			/*
			 * This is the intersection of the line formed by p1 and p2 and the plane right
			 * = leftBound. Note that truncating here warps the triangle a little bit. This
			 * isn't a huge problem, however, as the effect diminishes significantly with
			 * the size of the image
			 */
			ZPixel start12 = new ZPixel(leftBound, d1 + (int) (ratio12 * (d2 - d1)), shade, z1 + ratio12 * (z2 - z1));

			/*
			 * This is the intersection of the line formed by p1 and p3 and the plane right
			 * = leftBound
			 */
			ZPixel start13 = new ZPixel(leftBound, d1 + (int) (ratio13 * (d3 - d1)), 
				shade, z1 + ratio13 * (z3 - z1));

			ZFigure line23 = lineWithoutHorizontalRepetition(p2, p3);
			ZFigure line13 = lineWithoutHorizontalRepetition(start13, p3);
			ZFigure line12 = lineWithoutHorizontalRepetition(start12, p2);
	
			for (int i = 0; i < line12.size(); i++) {
				triangle.add(verticalLine(line13.get(i), line12.get(i), slope));
	
			}
	
			for (int i = 0; i < line23.size(); i++) {
				triangle.add(verticalLine(line13.get(i + line12.size()), line23.get(i), slope));
	
			}
	
			return triangle;
		}
	
		/*
		 * If the method gets this far, only the rightmost point is within the visible
		 * range.
		 */

		// Idea here similar to the previous case.
		double ratio23 = ((double) (leftBound - r2)) / (r3 - r2);
		double ratio13 = ((double) (leftBound - r1)) / (r3 - r1);

		ZPixel start23 = new ZPixel(leftBound, d2 + (int) (ratio23 * (d3 - d2)), 
			shade, z2 + ratio23 * (z3 - z2));

		ZPixel start13 = new ZPixel(leftBound, d1 + (int) (ratio13 * (d3 - d1)), 
			shade, z1 + ratio13 * (z3 - z1));

		ZFigure line23 = lineWithoutHorizontalRepetition(start23, p3);
		ZFigure line13 = lineWithoutHorizontalRepetition(start13, p3);

		for (int i = 0; i < line13.size(); i++) {
			triangle.add(verticalLine(line13.get(i), line23.get(i), slope));

		}

		return triangle;
	}

	/*
	 * Similar to the method of the same name in the Image class. zBuffer value is
	 * interpolated linearly for the points between p1, p2, p3
	 */
	public ZFigure jaggedTriangle(ZPixel p1, ZPixel p2, ZPixel p3) {

		boolean r2r3 = p2.getRight() <= p3.getRight();
		boolean r1r3 = p1.getRight() <= p3.getRight();
		boolean r1r2 = p1.getRight() <= p2.getRight();

		if (r1r2) {
			if (r2r3) {
				return jaggedTriangleAuxiliary(p1, p2, p3);
			}
			if (r1r3) {
				return jaggedTriangleAuxiliary(p1, p3, p2);
			}
			return jaggedTriangleAuxiliary(p3, p1, p2);
		}
		if (r1r3) {
			return jaggedTriangleAuxiliary(p2, p1, p3);
		}
		if (r2r3) {
			return jaggedTriangleAuxiliary(p2, p3, p1);
		}
		return jaggedTriangleAuxiliary(p3, p2, p1);
		
	}
}
