package zBuffered2DRendering;

import java.util.ArrayList;

import functionalInterfaces.RealFunction;
import other.MiscFunctions;
import rendering2D.Image;
import rendering2D.ImageBase;
import rendering2D.ShadeHandling;
import rendering3D.RelativeComponent;
import rendering3D.RelativePolygon;

public class ZImage extends ImageBase {

	/*
	 * Stores the depth value of each pixel. The version of the depth buffer that is
	 * being here has a range 0 to infinity. Furthermore, a higher zBuffer indicates
	 * a closer object.
	 */
	double[][] zBuffer;

	/*
	 * Stores the ID of the polygon from which the pixel came from. 0 if the pixel
	 * did not come from a polygon.
	 */
	int[][] renderInfo;
	
	boolean[][] stencil;

	public ZImage(int leftEnd, int rightEnd, int upEnd, int downEnd) {
		super(leftEnd, rightEnd, upEnd, downEnd);
		zBuffer = new double[imageRows][imageCols];
		renderInfo = new int[imageRows][imageCols];
		stencil = new boolean[imageRows][imageCols];

	}

	public ZImage(int[][] arr, int left, int up) {
		super(arr, left, up);
		zBuffer = new double[imageRows][imageCols];
		renderInfo = new int[imageRows][imageCols];
		stencil = new boolean[imageRows][imageCols];
	}
	
	private ZImage() {
		super();
	}
	
	// Debug function. Returns an image with pixels colored according to their zBuffer
	public Image getZBufferImage(ShadeHandling shadeHandling, RealFunction sigmoid) {
		
		Image image = new Image(this);
		
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				if (zBuffer[i][j] != 0)  {
				image.setShade(j+leftBound, i+upBound, shadeHandling.determineShade(sigmoid.f(zBuffer[i][j])));			
				}
			}
		}
		
		return image;
	}

	public void clear() {
		
		image = new int[imageRows][imageCols];
		zBuffer = new double[imageRows][imageCols];
		renderInfo = new int[imageRows][imageCols];
	}

	
	// Replaces a pixel iff the new pixel has a greater zBuffer
	public void draw(ZFigure figure) {
		
		int currRight;
		int currDown;
		double currZBuffer;
		
		for (ZPixel pixel : figure.figure) {

			currRight = pixel.getRight() - leftBound;
			currDown = pixel.getDown() - upBound;
			currZBuffer = pixel.getZBuffer();

			if (currRight >= 0 && currRight < imageCols && currDown >= 0 && currDown < imageRows
				&& currZBuffer > zBuffer[currDown][currRight]) {

				zBuffer[currDown][currRight] = currZBuffer;
				image[currDown][currRight] = pixel.getShade();
				renderInfo[currDown][currRight] = pixel.getRenderInfo();
			}
		}
	}

	public void draw(ZPixel pixel) {
		draw(pixel.getRight(), pixel.getDown(), pixel.getShade(), pixel.getZBuffer(),
			pixel.getRenderInfo());
	}

	public void draw(int right, int down, int shade, double zBuffer, int polygonID) {
		int adjustedRight = right - leftBound;
		int adjustedDown = down - upBound;

		if (adjustedRight >= 0 && adjustedRight < imageCols && adjustedDown >= 0
			&& adjustedDown < imageRows && zBuffer > this.zBuffer[adjustedDown][adjustedRight]) {

			this.zBuffer[adjustedDown][adjustedRight] = zBuffer;
			image[adjustedDown][adjustedRight] = shade;
			this.renderInfo[adjustedDown][adjustedRight] = polygonID;
		}
	}

	public void texturize() {
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				
				if ((renderInfo[i][j] & 1) == 1)  {
					
					image[i][j] = RelativeComponent.<RelativePolygon>get(renderInfo[i][j]).determineShade(j+leftBound, i+upBound, zBuffer[i][j]);
				}
			}
		}
	}
	// Everything past this point is drawing methods

	/*
	 * Here p1 is assumed to be further to the left than p2 and the vertical
	 * distance between p1 and p2 is lesser than their horizontal distance
	 */
	private ZFigure borderedLineAux1(ZPixel p1, ZPixel p2, int borderShade) {

		ZFigure line = lWHRCut(p1, p2);

		ZFigure borderedLine = new ZFigure();

		ZPixel movingPixel = new ZPixel();
		
		// Starting point left border
		if (line.size() != 0) {
			movingPixel = new ZPixel(line.get(0));
			movingPixel.moveRight(-1);
			movingPixel.setShade(borderShade);

			borderedLine.add(movingPixel);
		}

		// Middle point up/down borders
		for (int i = 0; i < line.size(); i++) {

			movingPixel = line.get(i);

			// Middle
			borderedLine.add(movingPixel);

			// One down
			movingPixel = new ZPixel(movingPixel);
			movingPixel.moveDown(1);
			movingPixel.setShade(borderShade);

			borderedLine.add(movingPixel);

			// One up
			movingPixel = new ZPixel(movingPixel);
			movingPixel.moveDown(-2);

			borderedLine.add(movingPixel);
		}

		// End point right border
		movingPixel = new ZPixel(movingPixel);
		movingPixel.moveDown(1);
		movingPixel.moveRight(1);

		borderedLine.add(movingPixel);
		
		return borderedLine;
	}
	
	/*
	 * Vertical distance between p1 and p2 assumed to be lesser than their
	 * horizontal distance
	 */
	private ZFigure borderedLineAux2(ZPixel p1, ZPixel p2, int borderShade) {
		
		if (p1.getRight() < p2.getRight()) {
			return borderedLineAux1(p1, p2, borderShade);
		}
		return borderedLineAux1(p2, p1, borderShade);
	}


	public ZFigure borderedLine(ZPixel p1, ZPixel p2, int borderShade) {

		if (Math.abs(p1.getRight() - p2.getRight()) >= Math.abs(p1.getDown() - p2.getDown())) {
			return borderedLineAux2(p1,p2,borderShade);
		}
		
		ZPixel p1Flipped = new ZPixel(p1);
		ZPixel p2Flipped = new ZPixel(p2);
		
		p1Flipped.flip();
		p2Flipped.flip();

		ZImage flippedImage = new ZImage();
		flippedImage.setBorders(upBound, downBound, leftBound, rightBound);
		
		ZFigure borderedLine = flippedImage.borderedLine(p1Flipped, p2Flipped, borderShade);

		borderedLine.change(pixel -> ((ZPixel) pixel).flip());
		
		return borderedLine;
	}

	/*
	 * Similar to the method of the same name in the image class, but the zBuffer is
	 * linearly interpolated between points.
	 * 
	 */
	private ZFigure lineWithoutHorizontalRepetition(ZPixel p1, ZPixel p2) {

		ZFigure line = new ZFigure();

		// Difference
		int rightDist = p2.getRight() - p1.getRight();
		int downDif = p2.getDown() - p1.getDown();
	
		if (rightDist == 0 || p1.getRight() > rightBound) {
			return line;
		}
	
		// Direction
		int downDir = MiscFunctions.sign(downDif);
	
		// Distance
		int downDist = Math.abs(downDif);
	
		int minDownStep = downDif / rightDist;
	
		// Remaining down distance that will need to be distributed across iterations.
		int excess = downDist % rightDist;
	
		int currMod = excess;

		// Horizontal length of the visible line.
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
	
	// Cuts the line to start at leftBound if it crosses it
	private ZFigure lWHRCut(ZPixel p1, ZPixel p2) {

		int r1 = p1.getRight();
		int r2 = p2.getRight();

		if (r1 > leftBound) {
			return lineWithoutHorizontalRepetition(p1,p2);
		}
		if (r2 > leftBound) {
			
			int d1 = p1.getDown();
			int d2 = p2.getDown();

			double z1 = p1.getZBuffer();
			double z2 = p2.getZBuffer();
			
			double ratio = ((double)(leftBound - r1))/(r2-r1);
			
			ZPixel start = new ZPixel(leftBound, d1 + (int) (ratio * (d2 - d1)), p1.getShade(), z1 + ratio * (z2 - z1), p1.getRenderInfo());
			
			return lineWithoutHorizontalRepetition(start,p2);
		}
		
		return new ZFigure();
	}

	/*
	 * zStep is the slope delta zBuffer / delta down. This isn't calculated within
	 * the method to optimize the polygon method.
	 */
	private void verticalLineAuxiliary(ZPixel p1, ZPixel p2, double zStep) {
		ZPixel movingPixel = new ZPixel(p1);

		if (upBound > p1.getDown()) {
			movingPixel.setDown(upBound);
			movingPixel.incrementZBuffer(zStep * (upBound - p1.getDown()));
		}
		int visibleLength = Math.min(p2.getDown() - movingPixel.getDown(), downBound - movingPixel.getDown() - 1);

		for (int i = 0; i <= visibleLength; i++) {
			draw(movingPixel);
			movingPixel.moveDown(1);
			movingPixel.incrementZBuffer(zStep);
		}
	}

	/*
	 * Similar to the method of the same name in the Image class. zStep is the slope
	 * delta zBuffer / delta down. This can be calculated easily, however, it is not
	 * calculated within the method and instead provided for the method call to
	 * optimize the jaggedTriangle method.
	 */
	public void verticalLine(ZPixel p1, ZPixel p2, double zStep) {
		if (p1.getDown() < p2.getDown()) {
			verticalLineAuxiliary(p1, p2, zStep);
		}
		verticalLineAuxiliary(p2, p1, zStep);
	}


	/*
	 * Renders a polygon specified by points.
	 * 
	 * All points assumed to be in the same plane and convex. Putting in any other
	 * set of points may lead to visual artifacts.
	 */
	public void polygon(ArrayList<ZPixel> points) {
		int length = points.size();

		if (length < 3) {
			return;
		}
		
		double slope = 0;
		
		// Index of the left/right most points in the list of points
		int leftMostIndex = 0;
		int rightMostIndex = 0;

		{
			// Local variables that are only used to calculate the slope
			int r1 = points.get(0).getRight();
			int r2 = points.get(1).getRight();
			int r3 = points.get(2).getRight();

			int d1 = points.get(0).getDown();
			int d2 = points.get(1).getDown();
			int d3 = points.get(2).getDown();

			double z1 = points.get(0).getZBuffer();
			double z2 = points.get(1).getZBuffer();
			double z3 = points.get(2).getZBuffer();

			/*
			 * Slope of the line (delta zBuffer / delta down) formed by intersection of the
			 * plane on which the polygon lies on and the plane(s) right = x for any x (it
			 * is independent of x).
			 */
			slope = ((z2 - z1) * (r3 - r1) - (z3 - z1) * (r2 - r1)) / 
					((d2 - d1) * (r3 - r1) - (d3 - d1) * (r2 - r1));

		}

		// Finds leftMostIndex/rightMostIndex
		for (int i = 1; i < length; i++) {

			double curr = points.get(i).getRight();

			if (curr < points.get(leftMostIndex).getRight()) {
				leftMostIndex = i;
			}

			if (curr > points.get(rightMostIndex).getRight()) {
				rightMostIndex = i;
			}

		}

		/*
		 * Line formed when winding counter clockwise through polygon from leftMost to
		 * rightMost
		 */
		ZFigure counterClockwise = new ZFigure();

		// Line formed when winding clockwise through polygon from leftMost to rightMost
		ZFigure clockwise = new ZFigure();

		
		for (int i = leftMostIndex; i != rightMostIndex; i = (i + 1) % length) {
			clockwise.add(lWHRCut(points.get(i), points.get((i + 1) % length)));
		}

		for (int i = leftMostIndex; i != rightMostIndex; i = MiscFunctions.mod((i - 1), length)) {
			counterClockwise.add(lWHRCut(points.get(i), points.get(MiscFunctions.mod((i - 1), length))));
		}
		/*
		 * In most cases, clockwise and counterclockwise will have equal lengths.
		 * However, there are rare edge cases where they aren't, so to avoid the code
		 * halting I have added a try-catch block
		 */
		for (int i = 0; i < clockwise.size(); i++) {
			try {
				verticalLine(clockwise.get(i), counterClockwise.get(i), slope);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
		}
	}

}
