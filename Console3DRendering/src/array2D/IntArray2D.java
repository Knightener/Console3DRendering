package array2D;

import java.util.function.IntUnaryOperator;

public class IntArray2D {
	// Performance over using int[][]
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

	public void set(int element, int row, int col) {
		array2D[row * cols + col] = element;
	}
	
	// Applies the function at row, col. 
	public void map(IntUnaryOperator map, int row, int col) {
		int index = row * cols + col;
		array2D[index] = map.applyAsInt(array2D[index]);
	}
	
	public int[] getArray() {
		return array2D;
	}
}
