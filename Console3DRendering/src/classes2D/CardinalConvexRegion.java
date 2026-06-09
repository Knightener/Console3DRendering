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

public class CardinalConvexRegion {

	/*
	 * This class provides a more efficient method of storing information about a
	 * shaded region that is either horizontally convex or vertically convex. (as
	 * opposed to a list of pixels or a subregion of a square)
	 * 
	 * A horizontally [resp. vertically] convex region of ZxZ is one such that every
	 * horizontal [resp. vertical] line only enters or exits the region at most two
	 * times. Every convex region is both horizontally and vertically convex.
	 */

	// If true, the region is vertically convex. Else, it is horizontally convex.
	private boolean verticallyConvex;

	/*
	 * Rows are vertical lines if verticallyConvex is true. Else, they are
	 * horizontal lines.
	 */
	private ArrayList<int[]> region;

	/*
	 * Vertical offset of each row of region if verticallyConvex is true. Else,
	 * horizontal offset. Can contain null elements, which signifies that the 
	 * row is empty. 
	 */
	private ArrayList<Integer> rowOffsets;

	/*
	 * Horizontal offset of entire region if verticallyConvex is true. Else,
	 * vertical offset.
	 */
	private int regionOffset;
	
	public CardinalConvexRegion(int regionOffset, boolean verticallyConvex) {
		this.regionOffset = regionOffset;
		this.verticallyConvex = verticallyConvex;
		region = new ArrayList<int[]>();
		rowOffsets = new ArrayList<Integer>();
	}
	
	/*
	 * Adds a new vertical line from a to b (inclusive) to the right of region if
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

	// Reflects the region along x = y.
	public void reflect() {
		verticallyConvex = !verticallyConvex;
	}
	
	// VerticallyConvex assumed to be true.
	private int getShadeAux(int right, int down) {

		try {
			int a = right - regionOffset;
			int b = down - rowOffsets.get(a);
			return region.get(a)[b];
		} catch (Exception e) {
			throw new IllegalArgumentException("Coordinates must be within region");
		}
	}

	// VerticallyConvex assumed to be true.
	private void setShadeAux(int right, int down, int shade) {
		try {
			int a = right - regionOffset;
			int b = down - rowOffsets.get(a);
			region.get(a)[b] = shade;
		} catch (Exception e) {
			throw new IllegalArgumentException("Coordinates must be within region");
		}
	}

	// Returns the shade at the specified coordinates of the region.
	public int getShade(int right, int down) {
		if (verticallyConvex) {
			return getShadeAux(right, down);
		} else
			return getShadeAux(down, right);
	}

	// Sets the shade at the specified coordinates to a new shade.
	public void setShade(int right, int down, int shade) {
		if (verticallyConvex) {
			setShadeAux(right, down, shade);
		} else
			setShadeAux(down, right, shade);
	}

	// Returns a figure representing the region.
	public Figure convertToFigure() {

		int count = 0;
		Figure figure = new Figure();

		for (int i = 0; i < region.size(); i++) {
			
			int[] curr = region.get(i);
			for (int j = 0; j < curr.length; j++) {
				if (verticallyConvex) {
					figure.add(new Pixel(i + regionOffset, j + rowOffsets.get(i), curr[j]));
				} else {
					figure.add(new Pixel(j + rowOffsets.get(i), i + regionOffset, curr[j]));
				}
			}
		}

		return figure;
	}

	// Sets every pixel of the region to shade.
	public void shade(int shade) {
		shade((x, y) -> shade);
	}

	// Shades pixels according to their position.
	public void shade(BiFunction<Integer, Integer, Integer> shadeFunction) {
		for (int i = 0; i < region.size(); i++) {
			int[] curr = region.get(i);
			for (int j = 0; j < curr.length; j++) {
				if (verticallyConvex) {
					curr[j] = shadeFunction.apply(i + regionOffset, j + rowOffsets.get(i));
				} else {
					curr[j] = shadeFunction.apply(j + rowOffsets.get(i), i + regionOffset);
				}
			}
		}
	}
	
	// Shades pixels according to their current shade.
	public void shade(Function<Integer,Integer> shadeFunction) {
		for (int row[] : region) {
			for (int i = 0; i < row.length ; i++) {
				row[i] = shadeFunction.apply(row[i]);
			}
		}
	}

	/*
	 * Returns an integer array representing the horizontal component of every point
	 * on the line. The line is fully drawn, including both p1 and p2. p1 assumed to
	 * be further left.
	 */
	public static int[] lineWithoutHorizontalRepetition(IntPoint p1, IntPoint p2) {

		// difference
		int rightDist = p2.getRight() - p1.getRight();
		int downDif = p2.getDown() - p1.getDown();

		if (rightDist == 0) {
			return new int[0];
		}
		
		int[] line = new int[rightDist + 1];

		// direction
		int downDir = MiscFunctions.sign(downDif);

		int minDownStep = downDif / rightDist;

		// remaining down distance that will need to be distributed across iterations.
		int excess = Math.abs(downDif) % rightDist;

		int currMod = excess;
		for (int i = 0; i < rightDist; i++) {

			line[i + 1] = line[i] + minDownStep;

			currMod += excess;
			if (currMod >= rightDist) {
				currMod -= rightDist;
				line[i + 1] += downDir;
			}
		}
		
		for (int i = 0; i <= rightDist; i++) {
			line[i] += p1.getDown();
		}
		return line;
	}

	/*
	 * Draws a polygon, expressed as a vertically convex region. If the polygon is
	 * not vertically convex, draws the smallest vertically convex region containing
	 * the polygon.
	 * 
	 */
	public static CardinalConvexRegion polygon(List<IntPoint> vertices) {

		int numberVertices = vertices.size();

		if (numberVertices < 3) {
			return new CardinalConvexRegion(0, true);
		}

		// Index of the left/right most points in the list of points
		int leftMost = Integer.MAX_VALUE;
		int rightMost = Integer.MIN_VALUE;

		// Finds leftMostIndex/rightMostIndex
		for (int i = 0; i < numberVertices; i++) {

			int curr = vertices.get(i).getRight();

			if (curr < leftMost) {
				leftMost = curr;
			}

			if (curr > rightMost) {
				rightMost = curr;
			}
		}

		int length = rightMost - leftMost;

		// Top intersection at each x. 
		int[] top = new int[length + 1];

		// Bottom intersection at each x. 
		int[] bottom = new int[length + 1];
		
		Arrays.fill(top, Integer.MAX_VALUE);
		Arrays.fill(bottom, Integer.MIN_VALUE);


		// Loop variables
		int[] currLine;
		IntPoint leftVertex;
		IntPoint rightVertex;
		int left; 
		
		for (int i = 0; i < numberVertices; i++) {

			if (vertices.get(i).getRight() < vertices.get((i + 1) % numberVertices).getRight()) {
				leftVertex = vertices.get(i);
				rightVertex = vertices.get((i + 1) % numberVertices);
			} else {
				rightVertex = vertices.get(i);
				leftVertex = vertices.get((i + 1) % numberVertices);
			}
			
			currLine = lineWithoutHorizontalRepetition(leftVertex, rightVertex);
			left = leftVertex.getRight();
			
			// Adding each line and comparing. 
			for (int j = 0; j < currLine.length; j++) {
				if (top[j + left - leftMost] > currLine[j]) {
					top[j + left - leftMost] = currLine[j];
				}
				if (bottom[j + left - leftMost] < currLine[j]) {
					bottom[j + left - leftMost] = currLine[j];
				}
			}
			
		}

		CardinalConvexRegion polygon = new CardinalConvexRegion(leftMost, true);

		
		for (int i = 0; i <= length; i++) {
			polygon.addLine(bottom[i], top[i]);
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

		for (int i = 0; i < rowOffsets.size(); i++) {;
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
