package other;
import functionalInterfaces.CheckInt;

public class ArrayFunctions {

	public static int[][] copy(int[][] arr) {

		int length = arr.length;
		int[][] copy = new int[length][];

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				copy[i][j] = arr[i][j];
			}
		}
		return copy;
	}
	
	public static int longest(int[][] arr) {

		int length = arr.length;
		int[] lengthArray = new int[length];

		for (int i = 0; i < length; i++) {
			lengthArray[i] = arr[i].length;
		}

		int maxLength = 0;

		for (int value : lengthArray) {
			if (value > maxLength) {
				maxLength = value;
			}
		}

		return maxLength;
		
	}
	

	public static int[][] rectangulize(int[][] arr) {
		
		int rows = arr.length;
		int cols = longest(arr);
		
		int[][] rectArr = new int[rows][cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				rectArr[i][j] = arr[i][j];
			}
		}
		return rectArr;
		
	}
	
	public static boolean passesCheck(int[][] arr, CheckInt check) {
		
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				if (check.checkInt(arr[i][j])) {
					return true;
				}
			}
		}
		return false;
	}

	public static void printArray(int[][] arr) {
		
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
	}
	
}
