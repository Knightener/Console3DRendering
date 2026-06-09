package array2D;

import java.util.Arrays;

public class JaggedIntArray2D {

	/*
	 * Flattened 2D jagged array of ints. For more efficiency. 
	 */
	
	int[] jaggedArray; 
	int[] rowStarts;
	
	// Initializes an empty jagged array.
	public JaggedIntArray2D() {
		jaggedArray = new int[0];
		rowStarts = new int[1];
	}

	// Adds a row to the end of the jagged array.
	public void addRow(int[] row) {
		jaggedArray = Arrays.copyOf(jaggedArray, jaggedArray.length + row.length);
		int rowStartIdx = rowStarts.length - 1;
		int arrayStartIdx = rowStarts[rowStartIdx];
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

}
