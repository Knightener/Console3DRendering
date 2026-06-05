package array2D;

public class BooleanArray2D {
	private final int rows, cols;
	private final boolean[] array2D;

	public BooleanArray2D(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.array2D = new boolean[rows * cols];
	}

	public boolean get(int row, int col) {
		return array2D[row * cols + col];
	}

	public void set(boolean element, int row, int col) {
		array2D[row * cols + col] = element;
	}

	public boolean[] getArray() {
		return array2D;
	}
}
