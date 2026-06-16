package array2D;

import java.util.BitSet;

public class BooleanArray2D {
	private final int rows, cols;
	
	private final BitSet array2D;

	public BooleanArray2D(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		array2D = new BitSet(rows * cols);
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
}
