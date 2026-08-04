package classes2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import other.MiscFunctions;
import rendering2D.Figure;
import rendering2D.Image;
import rendering2D.Pixel;

public class VCRegionBuilder {

	/*
	 * This class provides a more efficient method of storing information about a
	 * shaded region that is vertically convex. (as opposed to a list of pixels or a
	 * subregion of a square)
	 * 
	 * A vertically convex region of ZxZ is one such that every vertical line only
	 * enters or exits the region at most two times. Every convex region is
	 * vertically convex.
	 */

	// Rows are vertical lines.
	ArrayList<int[]> region;

	// Offset of each vertical line.
	ArrayList<Integer> rowOffsets;

	// Horizontal offset of entire region.
	int regionOffset;

	public VCRegionBuilder(int regionOffset) {
		this.regionOffset = regionOffset;
		region = new ArrayList<int[]>();
		rowOffsets = new ArrayList<Integer>();
	}

	/*
	 * Adds a new vertical line from a to b (inclusive) to the x of region if
	 * verticallyConvex is true. Else, adds a horizontal line from a to b beneath
	 * the region.
	 */
	public void addLine(int a, int b) {
		int start = Math.min(a, b);
		int end = Math.max(a, b);

		region.add(new int[end - start + 1]);
		rowOffsets.add(start);
	}

	// Equivalent to addLine(a,a)
	public void addPoint(int a) {
		region.add(new int[1]);
		rowOffsets.add(a);
	}

	// Adds n blank arrays either behind or after region (depending on sign).
	public void addBlanks(int n) {
		if (n >= 0) {
			for (int i = 0; i < n; i++) {
				region.add(new int[0]);
				rowOffsets.add(null);
			}
		} else {
			for (int i = 0; i < -n; i++) {
				region.addFirst(new int[0]);
				rowOffsets.addFirst(null);
			}
			regionOffset += n;
		}
	}

	// Extends all the rows by n.
	public void extendRows(int n) {
		if (n >= 0) {
			for (int i = 0; i < region.size(); i++) {
				int[] row = region.get(i);
				int[] newRow = new int[row.length + n];
				for (int j = 0; j < row.length; j++) {
					newRow[j] = row[j];
				}
				region.set(i, newRow);
			}
		} else {
			for (int i = 0; i < region.size(); i++) {
				int[] row = region.get(i);
				int[] newRow = new int[row.length - n];
				for (int j = 0; j < row.length; j++) {
					newRow[j - n] = row[j];
				}
				region.set(i, newRow);
				rowOffsets.set(i, rowOffsets.get(i) + n);
			}
		}
	}

	/*
	 * Extends the row at index to include all of the points. New points will have
	 * the shade 0.
	 */
	public void extendRow(int rowIndex, int... points) {

		IntSummaryStatistics stat = Arrays.stream(points).summaryStatistics();

		if (rowOffsets.get(rowIndex) != null) {
			int[] row = region.get(rowIndex);

			int start = rowOffsets.get(rowIndex);
			int end = start + row.length - 1;

			// If both are 0, the row is unchanged.
			int startDiff = Math.max(start - stat.getMin(), 0);
			int endDiff = Math.max(stat.getMax() - end, 0);

			int[] newRow = new int[row.length + startDiff + endDiff];
			for (int i = 0; i < row.length; i++) {
				newRow[i + startDiff] = row[i];
			}

			region.set(rowIndex, newRow);
			rowOffsets.set(rowIndex, start - startDiff);
		} else {
			int start = stat.getMin();
			int end = stat.getMax();

			region.set(rowIndex, new int[end - start + 1]);
			rowOffsets.set(rowIndex, start);
		}
	}

	/*
	 * Extends all columns by n. Columns aren't well defined in this class, this is
	 * just to highlight the fact that it extends the shape in the direction
	 * orthogonal to extendRows. Note that in some cases, it might include points
	 * that are not found by extending a point in the region by n
	 * horizontally/vertically (whichever is orthogonal to verticallyConvex) that
	 * are added to maintain convexity.
	 */
	public void extendColumns(int n) {

		int absN = Math.abs(n);

		// Starts and ends with n nulls.
		Integer[] allStarts = new Integer[region.size() + (absN << 1)];
		Integer[] allEnds = new Integer[region.size() + (absN << 1)];

		for (int i = 0; i < region.size(); i++) {
			allStarts[i + absN] = rowOffsets.get(i);
			if (rowOffsets.get(i) != null) {
				allEnds[i + absN] = rowOffsets.get(i) + region.get(i).length - 1;
			}
		}

		addBlanks(n);

		if (n > 0) {
			for (int i = 0; i < region.size(); i++) {

				int start = Integer.MAX_VALUE;
				int end = Integer.MIN_VALUE;

				/*
				 * Finds the minimum start and maximum ends in the window from i to n behind i
				 * (note that because allStarts and allEnds start with n nulls, the window i - n
				 * to i becomes i to i + n).
				 */
				for (int j = i; j <= i + n; j++) {
					if (allStarts[j] != null) {
						start = (start < allStarts[j]) ? start : allStarts[j];
						end = (end > allEnds[j]) ? end : allEnds[j];
					}
				}

				// This only happens if all starts and ends in the window were null.
				if (start != Integer.MAX_VALUE) {
					extendRow(i, start, end);
				}
			}
		}

		if (n < 0) {
			for (int i = region.size() - 1; i >= 0; i--) {

				int start = Integer.MAX_VALUE;
				int end = Integer.MIN_VALUE;

				/*
				 * Finds the minimum start and maximum ends in the window from i to n in front
				 * of i
				 */
				for (int j = i; j <= i - n; j++) {
					if (allStarts[j] != null) {
						start = (start < allStarts[j]) ? start : allStarts[j];
						end = (end > allEnds[j]) ? end : allEnds[j];
					}
				}

				if (start != Integer.MAX_VALUE) {
					extendRow(i, start, end);
				}
			}
		}
	}

	// VerticallyConvex assumed to be true.
	public int getShade(int right, int down) {
		try {
			int a = right - regionOffset;
			int b = down - rowOffsets.get(a);
			return region.get(a)[b];
		} catch (Exception e) {
			throw new IllegalArgumentException("Coordinates must be within region");
		}
	}

	// VerticallyConvex assumed to be true.
	public void setShadeAux(int right, int down, int shade) {
		try {
			int a = right - regionOffset;
			int b = down - rowOffsets.get(a);
			region.get(a)[b] = shade;
		} catch (Exception e) {
			throw new IllegalArgumentException("Coordinates must be within region");
		}
	}

	// Returns a figure representing the region.
	public Figure convertToFigure() {
		Figure figure = new Figure();

		for (int i = 0; i < region.size(); i++) {

			int[] curr = region.get(i);
			for (int j = 0; j < curr.length; j++) {
				figure.add(new Pixel(i + regionOffset, j + rowOffsets.get(i), curr[j]));

			}
		}

		return figure;
	}

	// Sets every pixel of the region to shade.
	public void shade(int shade) {
		shade((x, y) -> shade);
	}

	// Shades pixels according to their lightSource.
	public void shade(BiFunction<Integer, Integer, Integer> shadeFunction) {
		for (int i = 0; i < region.size(); i++) {
			int[] curr = region.get(i);
			for (int j = 0; j < curr.length; j++) {
				curr[j] = shadeFunction.apply(i + regionOffset, j + rowOffsets.get(i));

			}
		}
	}

	// Shades pixels according to their current shade.
	public void shade(Function<Integer, Integer> shadeFunction) {
		for (int row[] : region) {
			for (int i = 0; i < row.length; i++) {
				row[i] = shadeFunction.apply(row[i]);
			}
		}
	}

	/*
	 * Draws a polygon, expressed as a vertically convex region. If the polygon is
	 * not vertically convex, draws the smallest vertically convex region containing
	 * the polygon.
	 * 
	 * Not the most efficient polygon algorithm, designed to account for non 
	 * integer points and give a generous (larger than the actual polygon) approximation. 
	 */
	public static VCRegionBuilder polygon(List<R2Point> vertices) {

		int numberVertices = vertices.size();

		if (numberVertices < 3) {
			return new VCRegionBuilder(0);
		}

		// Index of the left/x most points in the list of points
		int xMinIdx = 0;
		int xMaxIdx = 0;

		// Finds leftMostIndex/rightMostIndex
		for (int i = 1; i < numberVertices; i++) {

			if (vertices.get(i).getX() < vertices.get(xMinIdx).getX()) {
				xMinIdx = i;
			}

			if (vertices.get(i).getX() > vertices.get(xMaxIdx).getX()) {
				xMaxIdx = i;
			}
		}

		int polygonStart = (int) Math.floor(vertices.get(xMinIdx).getX());
		int polygonEnd = (int) Math.ceil(vertices.get(xMaxIdx).getX());

		int length = polygonEnd - polygonStart;

		// Top intersection at each x.
		double[] top = new double[length + 1];

		// Bottom intersection at each x.
		double[] bottom = new double[length + 1];

		Arrays.fill(top, Double.NEGATIVE_INFINITY);
		Arrays.fill(bottom, Double.POSITIVE_INFINITY);

		R2Point leftVertex;
		R2Point rightVertex;

		int currStart;
		int currEnd;

		double currSlope;
		double currY;

		double currLeft;
		double currRight;

		for (int i = 0; i < numberVertices; i++) {

			if (vertices.get(i).getX() < vertices.get((i + 1) % numberVertices).getX()) {
				leftVertex = vertices.get(i);
				rightVertex = vertices.get((i + 1) % numberVertices);
			} else {
				rightVertex = vertices.get(i);
				leftVertex = vertices.get((i + 1) % numberVertices);
			}

			currLeft = leftVertex.getX();
			currRight = rightVertex.getX();

			currStart = (int) Math.floor(currLeft);
			currEnd = (int) Math.ceil(currRight);
			
			// Vertical line
			if (MiscFunctions.nearlyEquals(currLeft, currRight)) {
				double maxY = Math.max(currLeft, currRight);
				double minY = Math.min(currLeft, currRight);
				
				// It is worth checking both
				int leftIdx = currStart - polygonStart;
				int rightIdx = currEnd - polygonStart;

				if (maxY > top[leftIdx]) {
					top[leftIdx] = maxY;
				}
				if (minY < bottom[leftIdx]) {
					bottom[leftIdx] = minY;
				}
				if (maxY > top[rightIdx]) {
					top[rightIdx] = maxY;
				}
				if (minY < bottom[rightIdx]) {
					bottom[rightIdx] = minY;
				}
				continue;
			}

			currSlope = leftVertex.slope(rightVertex);

			currY = currSlope * (currStart - currLeft) + leftVertex.getY();

			// Adding the line to the top/bottom
			for (int j = currStart - polygonStart; j <= currEnd - polygonStart; j++) {
				if (currY > top[j]) {
					top[j] = currY;
				}
				if (currY < bottom[j]) {
					bottom[j] = currY;
				}
				currY += currSlope;
			}
		}

		VCRegionBuilder polygon = new VCRegionBuilder(polygonStart);

		for (int i = 0; i <= length; i++) {

			polygon.addLine((int) Math.floor(bottom[i]), (int) Math.ceil(top[i]));
		}


		return polygon;
	}

	/*
	 * Prints the entire region with coordinates. Not implemented as efficiently as
	 * possible. This is fine, however, as I only intend to use this for debugging.
	 */
	public void print() {
		int minRow = Integer.MAX_VALUE;
		int maxRow = Integer.MIN_VALUE;

		for (int i = 0; i < rowOffsets.size(); i++) {
			;
			if (minRow > rowOffsets.get(i)) {
				minRow = rowOffsets.get(i);
			}
			if (maxRow < rowOffsets.get(i) + region.get(i).length - 1) {
				maxRow = rowOffsets.get(i) + region.get(i).length - 1;
			}
		}

		Image image = new Image(regionOffset, regionOffset + rowOffsets.size(), minRow, maxRow);

		image.draw(convertToFigure());

		image.displayCoordinates();
	}

}
