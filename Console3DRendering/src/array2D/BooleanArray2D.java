package array2D;

import java.util.BitSet;

public class BooleanArray2D {
	private final int rows, cols, size;
	
	private final BitSet array2D;

	public BooleanArray2D(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		size = rows * cols;
		array2D = new BitSet(size);
	}

	public boolean get(int row, int col) {
		return array2D.get(row * cols + col);
	}

	public void set(boolean element, int row, int col) {
		array2D.set(row * cols + col, element);
	}

	public void flip(int row, int col) {
		array2D.flip(row * cols + col);
	}
		
	public void clear() {
		array2D.clear();
	}

	public void flip() {
		array2D.flip(0, size);
	}

	/*
	 * Returns false if most bits are false, true if most bits are true. If there is
	 * a tie, returns false.
	 */
	public boolean majority() {
		return array2D.cardinality() > size / 2;
	}
}
