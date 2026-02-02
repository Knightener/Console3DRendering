package classes2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

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
	public void constantShade(int shade) {
		shade((x, y) -> shade);
	}

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
	
	 

}
