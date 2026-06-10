package array2D;

import java.util.ArrayList;
import java.util.Arrays;

public class JaggedIntArray2D {

	/*
	 * Flattened 2D jagged array of ints for less overhead. Not optimized for
	 * resizing.
	 */
	int[] jaggedArray;
	int[] rowStarts;
	
	// Initializes an empty jagged array.
	public JaggedIntArray2D() {
		jaggedArray = new int[0];
		rowStarts = new int[1];
	}

	// Flattens an array.
	public JaggedIntArray2D(int[][] array) {
		int numRows = array.length;
		rowStarts = new int[numRows + 1];
		for (int i = 1; i <= numRows; i++) {
			rowStarts[i] = rowStarts[i - 1] + array[i - 1].length;
		}
		// Last row start is the length of the jagged array
		jaggedArray = new int[rowStarts[numRows]];
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < array[i].length; j++) {
				jaggedArray[j + rowStarts[i]] = array[i][j];
			}
		}
	}

	public JaggedIntArray2D(ArrayList<int[]> array) {
		this(array.toArray(new int[0][]));
	}
	
	// Adds a row to the end of the jagged array.
	public void addRow(int[] row) {
		jaggedArray = Arrays.copyOf(jaggedArray, jaggedArray.length + row.length);
		int rowStartIdx = rowStarts.length - 1;
		int arrayStartIdx = rowStarts[rowStartIdx];
		rowStarts = Arrays.copyOf(rowStarts, rowStarts.length + 1);
		rowStarts[rowStartIdx + 1] = arrayStartIdx + row.length;
		for (int i = arrayStartIdx; i < rowStarts[rowStartIdx + 1]; i++) {
			jaggedArray[i] = row[i - arrayStartIdx];
		}
	}

	// Returns the array as an int[][]
	public int[][] getArray() {
		int[][] array = new int[rowStarts.length - 1][];
		for (int i = 0; i < rowStarts.length - 1; i++) {
			array[i] = Arrays.copyOfRange(jaggedArray, rowStarts[i], rowStarts[i+1]);
		}
		return array;
	}

	// Returns the element at row i "column" j.
	public int get(int i, int j) {
		return jaggedArray[rowStarts[i] + j];
	}

}
