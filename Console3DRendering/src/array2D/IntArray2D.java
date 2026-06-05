package array2D;

public class IntArray2D {
	// Performance gains over using int[][]
	int rows, cols;

	int array2D[];

	public IntArray2D(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		array2D = new int[rows * cols];
	}

	public int get(int row, int col) {
		return array2D[row * cols + col];
	}

	public void add(int element, int row, int col) {
		array2D[row * cols + col] = element;
	}
}
