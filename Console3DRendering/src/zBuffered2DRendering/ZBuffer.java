package zBuffered2DRendering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import array2D.DoubleArray2D;
import functionalInterfaces.RealFunction;
import other.MiscFunctions;
import rendering2D.Image;
import rendering2D.ShadeHandling;

public class ZBuffer {

	/*
	 * Essentially just a stripped down version of ZImage that only write to
	 * zBuffer. All code is identical save comments, unnecessary methods, and
	 * parameters.
	 */

	private DoubleArray2D zBuffer;

	private int leftBound, rightBound, upBound, downBound, rows, cols;
	
	private ArrayList<ZInt>[] polygonBuffer;
	
	// Only method in ZBuffer that is not in ZImage. Returns true if the point z-passes. 
	public boolean zPass(int x, int y, double zBuffer) {
		int adjustedX = x - leftBound;
		int adjustedY = y - upBound;

		return (adjustedX >= 0 && adjustedX < cols && adjustedY >= 0 && adjustedY < rows
			&& zBuffer > this.zBuffer.get(adjustedY, adjustedX));
	}
	
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
		zBuffer = new DoubleArray2D(rows, cols);
		polygonBuffer = (ArrayList<ZInt>[]) new ArrayList[cols];
		for (int i = 0; i < cols; i++) {
			polygonBuffer[i] = new ArrayList<>();
		}
	}
	
	public ZBuffer(int leftBound, int rightBound, int upBound, int downBound) {
		this.leftBound = leftBound;
		this.rightBound = rightBound;
		this.upBound = upBound;
		this.downBound = downBound;
		rows = downBound - upBound + 1; 
		cols = rightBound - leftBound + 1;
		initialize();
	}
	
	public Image getZBufferImage(ShadeHandling shadeHandling, RealFunction sigmoid) {
		Image image = new Image(leftBound, rightBound, upBound, downBound);
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (zBuffer.get(i, j) != 0)  {
				image.setShade(j+leftBound, i+upBound, shadeHandling.determineShade(sigmoid.f(zBuffer.get(i, j))));			
				}
			}
		}
		
		return image;
	}
	
	public void clear() {
		Arrays.fill(zBuffer.getArray(), 0.0);
	}

	public void draw(ZPixel pixel) {
		draw(pixel.getX(), pixel.getY(), pixel.getZBuffer());
	}

	public void draw(int x, int y, double zBuffer) {
		int adjustedX = x - leftBound;
		int adjustedY = y - upBound;

		if (adjustedX >= 0 && adjustedX < cols && adjustedY >= 0
			&& adjustedY < rows && zBuffer > this.zBuffer.get(adjustedY, adjustedX)) {

			this.zBuffer.set(zBuffer, adjustedY, adjustedX);
		}
	}

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

				lWHRWriteToPolygonBuffer(new ZPixel(leftBound, d2 + (int) (ratio * (d1 - d2)),
					p2.getShade(), z2 + ratio * (z1 - z2), p2.getRenderInfo()), p1);
			}
		}
	}
	

	private void verticalLine(ZInt start, ZInt end, int right, double zStep) {
		if (upBound > start.position) {
			start.incrementZ(zStep * (upBound - start.position));
			start.position = upBound;
		}

		int visibleEnd = Math.min(end.position, downBound - 1);
		double currZ = start.zBuffer;
		for (int i = start.position; i < visibleEnd; i++) {
				draw(right, i, currZ);
			currZ += zStep;
		}
	}

	public void polygon(List<ZPixel> points) {
		int length = points.size();

		if (length < 3) {
			return;
		}
		
		double slope = 0;

		{
			double r1 = points.get(0).getX();
			double r2 = points.get(1).getX();
			double r3 = points.get(2).getX();

			double d1 = points.get(0).getY();
			double d2 = points.get(1).getY();
			double d3 = points.get(2).getY();

			double z1 = points.get(0).getZBuffer();
			double z2 = points.get(1).getZBuffer();
			double z3 = points.get(2).getZBuffer();

			slope = ((z2 - z1) * (r3 - r1) - (z3 - z1) * (r2 - r1)) / 
					((d2 - d1) * (r3 - r1) - (d3 - d1) * (r2 - r1));
		}
		

		int rightMost = points.get(0).getX();
		int leftMost = points.get(0).getX();

		{
			ZPixel curr;
			int currRight;
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
		
		if (rightMost >= rightBound) {
			rightMost = rightBound - 1;
		}
		
		if (leftMost < leftBound) {
			leftMost = leftBound;
		}

		ArrayList<ZInt> currList;
		
		for (int i = leftMost; i <= rightMost; i++) {
			currList = polygonBuffer[i - leftBound];

			for (int j = 1; j < currList.size(); j += 2) {
				verticalLine(currList.get(j - 1), currList.get(j), i, slope);
			}
			currList.clear();
		}

	}
}