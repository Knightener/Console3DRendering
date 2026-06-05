package array2D;

public class DoubleArray2D {
	// Performance gains over using a double[][]
	int rows, cols;

	double array2D[];

	public DoubleArray2D(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		array2D = new double[rows * cols];
	}

	public double get(int row, int col) {
		return array2D[row * cols + col];
	}

	public void set(double element, int row, int col) {
		array2D[row * cols + col] = element;
	}
	
	public double[] getArray() {
		return array2D;
	}
}
