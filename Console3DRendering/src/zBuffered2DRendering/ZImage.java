package zBuffered2DRendering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import array2D.BooleanArray2D;
import array2D.DoubleArray2D;
import array2D.IntArray2D;
import functionalInterfaces.RealFunction;
import other.MiscFunctions;
import rendering2D.Image;
import rendering2D.ImageBase;
import rendering2D.ShadeHandling;
import rendering3D.LightSource;
import rendering3D.RelativeComponent;
import rendering3D.RelativePolygon;

public class ZImage extends ImageBase {

	/*
	 * Stores the depth value of each pixel. The version of the depth buffer that is
	 * being here has a range 0 to infinity. Furthermore, a higher zBuffer indicates
	 * a closer object.
	 */
	private DoubleArray2D zBuffer;

	/*
	 * Smallest 24 bits store the ID of the polygon from which the pixel came from.
	 * 0 if the pixel did not come from a polygon. Largest 8 bits store the shadow
	 * value (number of light sources that don't hit the pixel).
	 */
	private IntArray2D renderInfo;
	
	// Buffer for rendering polygons. 
	private ArrayList<ZInt>[] polygonBuffer;
	
	
	// Ad hoc class that stores an int with zBuffer info.
	private static class ZInt implements Comparable<ZInt> {
		int position;
		double zBuffer;

		public ZInt(int position, double zBuffer) {
			this.position = position;
			this.zBuffer = zBuffer;
		}
		
		public ZInt(ZInt zInt) {
			position = zInt.position;
			zBuffer = zInt.zBuffer;
		}
		
		@Override
		public int compareTo(ZInt zInt) {
			return position - zInt.position;
		}
		
		public void incrementZ(double delta) {
			zBuffer += delta;
		}
		
		public void incrementPosition(int delta) {
			position += delta;
		}
		
		@Override
		public String toString() {
			return "(" + position + ", " + zBuffer + ")"; 
		}
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		zBuffer = new DoubleArray2D(imageRows, imageCols);
		renderInfo = new IntArray2D(imageRows, imageCols);
		polygonBuffer = (ArrayList<ZInt>[]) new ArrayList[imageCols];
		for (int i = 0; i < imageCols; i++) {
			polygonBuffer[i] = new ArrayList<>();
		}
	}

	public ZImage(int leftEnd, int rightEnd, int upEnd, int downEnd) {
		super(leftEnd, rightEnd, upEnd, downEnd);
		initialize();

	}

	public ZImage(int[][] arr, int left, int up) {
		super(arr, left, up);
		initialize();
	}

	private ZImage() {
		super();
	}
	
	/*
	 * Debug function. Draws the current polygonBuffer to the current ZImage.
	 */
	public void drawPolygonBuffer() {
		ZInt curr;
		for (int i = 0; i < imageCols; i++) {
			for (int j = 0; j < polygonBuffer[i].size(); j++) {
				curr = polygonBuffer[i].get(j); 
				draw(i + leftBound, curr.position, ShadeHandling.MAX_SHADE, Double.MAX_VALUE, 0);
			}
		}
	}
	
	// Debug function. Returns an image with pixels colored according to their zBuffer.
	public Image getZBufferImage(ShadeHandling shadeHandling, RealFunction sigmoid) {
		
		Image image = new Image(this);
		image.clear();
		
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				if (zBuffer.get(i, j) != 0)  {
				image.setShade(j+leftBound, i+upBound, shadeHandling.determineShade(sigmoid.f(zBuffer.get(i, j))));			
				}
			}
		}
		
		return image;
	}
	
	// Debug function. Returns an image with pixels colored according to their stencil value.
	public Image getStencilImage() {
		
		Image image = new Image(this);
		image.clear();

		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				image.setShade(j + leftBound, i + upBound, Math
					.min(renderInfo.get(i, j) >> ZPixel.STENCIL_BIT_POS, ShadeHandling.MAX_SHADE));
			}
		}

		return image;
	}

	public void clear() {
		Arrays.fill(image.getArray(), 0);
		Arrays.fill(zBuffer.getArray(), 0.0);
		Arrays.fill(renderInfo.getArray(), 0);
	}

	public void shade() {
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				image.set(ShadeHandling.darken(image.get(i, j),
					renderInfo.get(i, j) >>> ZPixel.STENCIL_BIT_POS), i, j);
			}
		}
	}

	// Replaces a pixel iff the new pixel has a greater zBuffer
	public void draw(ZFigure figure) {

		int currRight;
		int currDown;
		double currZBuffer;
		
		for (ZPixel pixel : figure.figure) {

			currRight = pixel.getX() - leftBound;
			currDown = pixel.getY() - upBound;
			currZBuffer = pixel.getZBuffer();

			if (currRight >= 0 && currRight < imageCols && currDown >= 0 && currDown < imageRows
				&& currZBuffer > zBuffer.get(currDown, currRight)) {

				zBuffer.set(currZBuffer, currDown, currRight);
				image.set(pixel.getShade(), currDown, currRight);
				renderInfo.set(pixel.getRenderInfo(), currDown, currRight);
			}
		}
	}

	public void draw(ZPixel pixel) {
		draw(pixel.getX(), pixel.getY(), pixel.getShade(), pixel.getZBuffer(),
			pixel.getRenderInfo());
	}

	public void draw(int right, int down, int shade, double zBuffer, int polygonID) {
		int adjustedRight = right - leftBound;
		int adjustedDown = down - upBound;

		if (adjustedRight >= 0 && adjustedRight < imageCols && adjustedDown >= 0
			&& adjustedDown < imageRows && zBuffer > this.zBuffer.get(adjustedDown, adjustedRight)) {

			this.zBuffer.set(zBuffer, adjustedDown, adjustedRight);
			image.set(shade, adjustedDown, adjustedRight);
			this.renderInfo.set(polygonID, adjustedDown, adjustedRight);
		}
	}
	
	// Increments/decrements on Z-fail, depending on if polygon is facing or not.
	public void writeToStencil(int right, int down, double zBuffer, boolean isFacing) {
		int adjustedRight = right - leftBound;
		int adjustedDown = down - upBound;

		if (adjustedRight >= 0 && adjustedRight < imageCols && adjustedDown >= 0
			&& adjustedDown < imageRows
			&& zBuffer < this.zBuffer.get(adjustedDown, adjustedRight)) {
			renderInfo.map(n -> n + (isFacing ? ZPixel.STENCIL_BIT : -ZPixel.STENCIL_BIT), adjustedDown,
				adjustedRight);
		}
	}
	
	public void writeToStencil(ZPixel pixel, boolean isFacing) {
		writeToStencil(pixel.getX(), pixel.getY(), pixel.getZBuffer(), isFacing);
	}

	public void texturize() {
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				if ((renderInfo.get(i, j) & 1) == 1) {
					image.set(RelativeComponent
						.<RelativePolygon>get(renderInfo.get(i, j) & ZPixel.POLYGON_BITS)
						.determineShade(j + leftBound, i + upBound, zBuffer.get(i, j)), i, j);
				}
			}
		}
	}
	
	public void texturize(LightSource lightSource) {
		int curr;
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				curr = renderInfo.get(i, j);
				// Skips lighting calculations if pixel is in shadow.
				if ((curr & 1) == 1) {
					if (curr >>> ZPixel.STENCIL_BIT_POS == 0) {
						image.set(RelativeComponent
							.<RelativePolygon>get(renderInfo.get(i, j) & ZPixel.POLYGON_BITS)
							.determineShade(j + leftBound, i + upBound, zBuffer.get(i, j),
								lightSource),
							i, j);
					} else {
						image.set(
							RelativeComponent
								.<RelativePolygon>get(renderInfo.get(i, j) & ZPixel.POLYGON_BITS)
								.determineShade(j + leftBound, i + upBound, zBuffer.get(i, j)),
							i, j);
					}
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
			movingPixel.incrementX(-1);
			movingPixel.setShade(borderShade);

			borderedLine.add(movingPixel);
		}

		// Middle point up/y borders
		for (int i = 0; i < line.size(); i++) {

			movingPixel = line.get(i);

			// Middle
			borderedLine.add(movingPixel);

			// One y
			movingPixel = new ZPixel(movingPixel);
			movingPixel.incrementY(1);
			movingPixel.setShade(borderShade);

			borderedLine.add(movingPixel);

			// One up
			movingPixel = new ZPixel(movingPixel);
			movingPixel.incrementY(-2);

			borderedLine.add(movingPixel);
		}

		// End point x border
		movingPixel = new ZPixel(movingPixel);
		movingPixel.incrementY(1);
		movingPixel.incrementX(1);

		borderedLine.add(movingPixel);
		
		return borderedLine;
	}
	
	/*
	 * Vertical distance between p1 and p2 assumed to be lesser than their
	 * horizontal distance.
	 */
	private ZFigure borderedLineAux2(ZPixel p1, ZPixel p2, int borderShade) {
		
		if (p1.getX() < p2.getX()) {
			return borderedLineAux1(p1, p2, borderShade);
		}
		return borderedLineAux1(p2, p1, borderShade);
	}


	public ZFigure borderedLine(ZPixel p1, ZPixel p2, int borderShade) {

		if (Math.abs(p1.getX() - p2.getX()) >= Math.abs(p1.getY() - p2.getY())) {
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
	 * linearly interpolated between points. p2 assumed to be further x than p1.
	 * Takes on shade and polygonID of p1.
	 */
	private ZFigure lineWithoutHorizontalRepetition(ZPixel p1, ZPixel p2) {

		
		ZFigure line = new ZFigure();

		// Difference
		int rightDist = p2.getX() - p1.getX();
		int downDif = p2.getY() - p1.getY();

		if (rightDist == 0 || p1.getX() > rightBound) {
			return line;
		}

		// Direction
		int downDir = MiscFunctions.sign(downDif);

		// Distance
		int downDist = Math.abs(downDif);
	
		int minDownStep = downDif / rightDist;
	
		// Remaining y distance that will need to be distributed across iterations.
		int excess = downDist % rightDist;
	
		int currMod = excess;

		// Horizontal length of the visible line.
		int visibleLength = Math.min(rightDist, rightBound - p1.getX());
		
		double zStep = (p2.getZBuffer() - p1.getZBuffer()) / rightDist;
		
		ZPixel movingPixel = new ZPixel(p1);
		
		for (int i = 0; i < visibleLength; i++) {
			line.add(new ZPixel(movingPixel));
			
			movingPixel.incrementX(1);
			movingPixel.incrementY(minDownStep);
			movingPixel.incrementZBuffer(zStep);

			currMod += excess;
			
			if (currMod >= rightDist) {
				currMod -= rightDist;
				movingPixel.incrementY(downDir);
			}
		}
		return line;
	}
	
	// Cuts the line to start at leftBound if it crosses it. p2 assumed to be further x than p1. 
	private ZFigure lWHRCut(ZPixel p1, ZPixel p2) {

		int r1 = p1.getX();
		int r2 = p2.getX();

		if (r1 > leftBound) {
			return lineWithoutHorizontalRepetition(p1,p2);
		}
		if (r2 > leftBound) {
			
			int d1 = p1.getY();
			int d2 = p2.getY();

			double z1 = p1.getZBuffer();
			double z2 = p2.getZBuffer();
			
			double ratio = ((double)(leftBound - r1))/(r2-r1);
			
			// First point is the new start. 
			return lineWithoutHorizontalRepetition(new ZPixel(leftBound, d1 + (int) (ratio * (d2 - d1)), p1.getShade(), z1 + ratio * (z2 - z1), p1.getRenderInfo()),p2);
		}
		
		return new ZFigure();
	}

	/*
	 * Similar lineWithoutHorizontalRepetition but draws directly to polygonBuffer.
	 * Maintains sorted order of each bucket polygonBuffer.
	 */
	private void lWHRWriteToPolygonBuffer(ZPixel p1, ZPixel p2) {

		int rightDist = p2.getX() - p1.getX();
		int downDif = p2.getY() - p1.getY();

		if (p1.getX() > rightBound || rightDist == 0) {
			return;
		}

		int downDir = MiscFunctions.sign(downDif);
		int minDownStep = downDif / rightDist;
		int excess = Math.abs(downDif) % rightDist;
		int currMod = excess;

		double zStep = (p2.getZBuffer() - p1.getZBuffer()) / rightDist;
		
		ZInt curr = new ZInt(p1.getY(), p1.getZBuffer());
		ArrayList<ZInt> currList;
		int startIndex = p1.getX() - leftBound;
		int insertionIndex;

		// i bound is visibleLnegth
		for (int i = 0; i < Math.min(rightDist, rightBound - p1.getX() - 1); i++) {
			
			currList = polygonBuffer[i+startIndex];
			
			insertionIndex = Collections.binarySearch(currList, curr);

			if (insertionIndex >= 0) {
				currList.add(insertionIndex, new ZInt(curr));
			} else {
				currList.add(-insertionIndex - 1, new ZInt(curr));
			}
			curr.incrementPosition(minDownStep);
			curr.incrementZ(zStep);
			currMod += excess;

			if (currMod >= rightDist) {
				currMod -= rightDist;
				curr.incrementPosition(downDir);
			}
		}
	}

	// Similar to lWHRCut but draws directly to polygonBuffer. Also sorts p1 and p2. 
	public void lWHRCutWriteToPolygonBuffer(ZPixel p1, ZPixel p2) {
		int r1 = p1.getX();
		int r2 = p2.getX();
		
		if (r1 < r2) {
			if (r1 > leftBound) {
				lWHRWriteToPolygonBuffer(p1, p2);
			} else if (r2 > leftBound) {

				int d1 = p1.getY();
				int d2 = p2.getY();

				double z1 = p1.getZBuffer();
				double z2 = p2.getZBuffer();

				double ratio = ((double) (leftBound - r1)) / (r2 - r1);

				// First point is the new start.
				lWHRWriteToPolygonBuffer(new ZPixel(leftBound, d1 + (int) (ratio * (d2 - d1)),
					p1.getShade(), z1 + ratio * (z2 - z1), p1.getRenderInfo()), p2);
			}
		} else {
			if (r2 > leftBound) {
				lWHRWriteToPolygonBuffer(p2, p1);
			} else if (r1 > leftBound) {

				int d1 = p1.getY();
				int d2 = p2.getY();

				double z1 = p1.getZBuffer();
				double z2 = p2.getZBuffer();

				double ratio = ((double) (leftBound - r2)) / (r1 - r2);

				// First point is the new start.
				lWHRWriteToPolygonBuffer(new ZPixel(leftBound, d2 + (int) (ratio * (d1 - d2)),
					p2.getShade(), z2 + ratio * (z1 - z2), p2.getRenderInfo()), p1);
			}
		}
	}
	
	/*
	 * zStep is the slope delta zBuffer / delta y. This isn't calculated within
	 * the method to optimize the polygon method. p1 assumed to be above p2.
	 */
	private void verticalLineAuxiliary(ZPixel p1, ZPixel p2, double zStep) {
		ZPixel movingPixel = new ZPixel(p1);

		if (upBound > p1.getY()) {
			movingPixel.setY(upBound);
			movingPixel.incrementZBuffer(zStep * (upBound - p1.getY()));
		}
		int visibleLength = Math.min(p2.getY() - movingPixel.getY(),
			downBound - movingPixel.getY() - 1);

		for (int i = 0; i <= visibleLength; i++) {
			draw(movingPixel);

			movingPixel.incrementY(1);
			movingPixel.incrementZBuffer(zStep);
		}
	}

	/*
	 * Similar to the method of the same name in the Image class. zStep is the slope
	 * delta zBuffer / delta y. This can be calculated easily, however, it is not
	 * calculated within the method and instead provided for the method call to
	 * optimize the jaggedTriangle method. Takes on shade and polygonID of top pixel. 
	 */
	public void verticalLine(ZPixel p1, ZPixel p2, double zStep) {
		if (p1.getY() < p2.getY()) {
			verticalLineAuxiliary(p1, p2, zStep);
		} else {
			verticalLineAuxiliary(p2, p1, zStep);
		}
	}

	private void verticalLine(ZInt start, ZInt end, int right, int shade, double zStep,
		int polygonID, boolean writeToStencil, boolean isFacing) {

		/*
		 * For the specific application of drawing polygons, we do not have to worry
		 * about the value of start changing.
		 */
		if (upBound > start.position) {
			start.incrementZ(zStep * (upBound - start.position));
			start.position = upBound;
		}

		int visibleEnd = Math.min(end.position, downBound - 1);
		double currZ = start.zBuffer;
		for (int i = start.position; i < visibleEnd; i++) {
			if (writeToStencil) {
				writeToStencil(right, i, currZ, isFacing);
			} else {
				draw(right, i, shade, currZ, polygonID);
			}
			currZ += zStep;
		}
	}

	/*
	 * Renders a polygon specified by points. All points assumed to be in the same
	 * plane. Putting in any other set of points may lead to visual artifacts. Takes
	 * on shade and polygonID of first point.
	 */
	public void polygon(List<ZPixel> points, boolean writeToStencil, boolean isFacing) {
		int length = points.size();

		if (length < 3) {
			return;
		}

		int shade = points.get(0).getShade();
		
		// The RenderInfo will be the polygonID in most cases. 
		int polygonID = points.get(0).getRenderInfo();
		double slope = 0;

		{
			// Local variables that are only used to calculate the slope
			double r1 = points.get(0).getX();
			double r2 = points.get(1).getX();
			double r3 = points.get(2).getX();

			double d1 = points.get(0).getY();
			double d2 = points.get(1).getY();
			double d3 = points.get(2).getY();

			double z1 = points.get(0).getZBuffer();
			double z2 = points.get(1).getZBuffer();
			double z3 = points.get(2).getZBuffer();

			/*
			 * Slope of the line (delta zBuffer / delta y) formed by intersection of the
			 * plane on which the polygon lies on and the plane(s) x = x for any x (it
			 * is independent of x).
			 */
			slope = ((z2 - z1) * (r3 - r1) - (z3 - z1) * (r2 - r1)) / 
					((d2 - d1) * (r3 - r1) - (d3 - d1) * (r2 - r1));
		}
		

		int rightMost = points.get(0).getX();
		int leftMost = points.get(0).getX();

		{
			ZPixel curr;
			int currRight;
			/*
			 * Writing edges to polygonBuffer. Also finds rightmost and leftmost x of
			 * polygon.
			 */
			for (int i = 0; i < length; i++) {
				curr = points.get(i);
				currRight = curr.getX();
				
				if (currRight > rightMost) {
					rightMost = currRight;
				} else if (currRight < leftMost) {
					leftMost = currRight;
				}
				
				lWHRCutWriteToPolygonBuffer(curr, points.get((i + 1) % length));
			}
		}

		// If polygon goes further x than the screen.
		if (rightMost >= rightBound) {
			rightMost = rightBound - 1;
		}
		
		// If polygon goes further left than the screen.
		if (leftMost < leftBound) {
			leftMost = leftBound;
		}

		ArrayList<ZInt> currList;
		
		for (int i = leftMost; i <= rightMost; i++) {
			currList = polygonBuffer[i - leftBound];

			/*
			 * Draws a line from every even indexed intersection to every odd indexed
			 * intersection.
			 */
			for (int j = 1; j < currList.size(); j += 2) {
				verticalLine(currList.get(j - 1), currList.get(j), i, shade, slope, polygonID,
					writeToStencil, isFacing);
			}
			currList.clear();
		}

	}

	public void polygon(List<ZPixel> points) {
		polygon(points, false, false);
	}
}