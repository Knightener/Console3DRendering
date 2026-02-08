package classes2D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import other.MiscFunctions;
import rendering2D.Figure;
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
	 * horizontal offset.
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

	// Extends the row at index to include a, b. New points will have the shade 0.
	public void extendRow(int a, int b, int rowIndex) {
		
		int[] row = region.get(rowIndex);
		
		int start = rowOffsets.get(rowIndex);
		int end = start + row.length - 1; 

		// If both are 0, the row is unchanged.
		int startDiff = Math.max(start - Math.min(a, b), 0);
		int endDiff = Math.max(Math.max(a, b) - end, 0);

		int[] newRow = new int[row.length + startDiff + endDiff];
		for (int i = 0; i < row.length; i++) {
			newRow[i + startDiff] = row[i];
		}
		
		region.set(rowIndex, newRow);
		rowOffsets.set(rowIndex, start - startDiff);
		
	}
	
	// Reflects the region along x = y
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
	 * on the line. The line is fully drawn, except p2. p1 assumed to be further
	 * left.
	 */
	public static int[] lineWithoutHorizontalRepetition(IntPoint p1, IntPoint p2) {

		// difference
		int rightDist = p2.getRight() - p1.getRight();
		int downDif = p2.getDown() - p1.getDown();

		if (rightDist == 0) {
			return new int[0];
		}
		
		int[] line = new int[rightDist];

		// direction
		int downDir = MiscFunctions.sign(downDif);

		int minDownStep = downDif / rightDist;

		// remaining down distance that will need to be distributed across iterations.
		int excess = Math.abs(downDif) % rightDist;

		int currMod = excess;
		for (int i = 0; i < rightDist - 1; i++) {

			line[i + 1] = line[i] + minDownStep;

			currMod += excess;
			if (currMod >= rightDist) {
				currMod -= rightDist;
				line[i + 1] += downDir;
			}
		}
		
		for (int i = 0; i < rightDist; i++) {
			line[i] += p1.getDown();
		}
		return line;
	}

	/*
	 * Draws a polygon. 
	 * Polygon must be vertically convex.
	 */
	public static CardinalConvexRegion polygon(List<IntPoint> vertices) {

		int numberVertices = vertices.size();

		if (numberVertices < 3) {
			return new CardinalConvexRegion(0, true);
		}

		// Index of the left/right most points in the list of points
		int leftMostIndex = 0;
		int rightMostIndex = 0;

		// Finds leftMostIndex/rightMostIndex
		for (int i = 1; i < numberVertices; i++) {

			double curr = vertices.get(i).getRight();

			if (curr < vertices.get(leftMostIndex).getRight()) {
				leftMostIndex = i;
			}

			if (curr > vertices.get(rightMostIndex).getRight()) {
				rightMostIndex = i;
			}
		}

		int length = vertices.get(rightMostIndex).getRight() - vertices.get(leftMostIndex).getRight();

		/*
		 * Line formed when winding counter clockwise through polygon from leftMost to
		 * rightMost
		 */
		int[] counterClockwise = new int[length];

		// Line formed when winding clockwise through polygon from leftMost to rightMost
		int[] clockwise = new int[length];

		int currIndex = 0;
		int[] currLine;

		try {
			for (int i = leftMostIndex; i != rightMostIndex; i = (i + 1) % numberVertices) {

				currLine = lineWithoutHorizontalRepetition(vertices.get(i), 
						vertices.get((i + 1) % numberVertices));

				for (int j = 0; j < currLine.length; j++) {
					clockwise[currIndex++] = currLine[j];
				}
			}

			currIndex = 0;

			for (int i = leftMostIndex; i != rightMostIndex; i = MiscFunctions.mod((i - 1), numberVertices)) {

				currLine = lineWithoutHorizontalRepetition(vertices.get(i),
						vertices.get(MiscFunctions.mod((i - 1), numberVertices)));

				for (int j = 0; j < currLine.length; j++) {
					counterClockwise[currIndex++] = currLine[j];
				}
			}

		} catch (NegativeArraySizeException e) {
			// This happens because the line tries to go backwards.
			throw new IllegalArgumentException("Polygon must be vertically convex.");
		}

		CardinalConvexRegion polygon = new CardinalConvexRegion(vertices.get(leftMostIndex).getRight(), true);

		for (int i = 0; i < length; i++) {
			polygon.addLine(clockwise[i], counterClockwise[i]);
		}

		// End point.
		polygon.addPoint(vertices.get(rightMostIndex).getDown());

		return polygon;

	}
	 

}
