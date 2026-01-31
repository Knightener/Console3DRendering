package classes2D;

import java.util.ArrayList;

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
	public boolean verticallyConvex;

	/*
	 * Rows are vertical lines if verticallyConvex is true. Else, they are
	 * horizontal lines.
	 * 
	 */
	public ArrayList<int[]> region;

	/*
	 * Vertical offset of each row of region if verticallyConvex is true. Else,
	 * horizontal offset.
	 */
	public ArrayList<Integer> rowOffsets;

	/*
	 * Horizontal offset of entire region if verticallyConvex is true. Else,
	 * vertical offset.
	 */
	public int regionOffset;

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

	/*
	 * Returns the shade at the specified coordinates of the region.
	 * VerticallyConvex assumed to be true.
	 */
	private int shadeAtAux(int right, int down) {
		int a = right - regionOffset;
		int b = down - rowOffsets.get(a);
		try {
			return region.get(a)[b];
		} catch (Exception e) {
			throw new IllegalArgumentException("Coordinates must be within region");
		}
	}

	public int shadeAt(int right, int down) {
		if (verticallyConvex) {
			return shadeAtAux(right, down);
		} else
			return shadeAtAux(down, right);
	}

}
