package other;

public class MiscFunctions {
	public static int nthDigit(int num, int n) {
		num = Math.abs(num);
		for(int i = 0; i < n; i++) {
			num /= 10;
		}
		return num % 10;
	}
	
	public static int numberOfDigits(int num) {
		int curr = 0;
		num = Math.abs(num);
		while (num != 0) {
			num /= 10;
			curr++;
		}
		return curr;
	}
	
	public static String spacedNumber(int num, int figures) {
		String spacedNumber = "";

		for(int i = 0; i < figures; i++) {
			if (numberOfDigits(num) > i) {
				spacedNumber = nthDigit(num,i) + " " + spacedNumber;
			} else if (numberOfDigits(num) == i && num < 0) {
				spacedNumber = "- " + spacedNumber;
			} else if (num == 0 && i == 0){
				spacedNumber = "0 " + spacedNumber;
			} else {
				spacedNumber = "  " + spacedNumber;
			}
		}
		
		return spacedNumber;
	}

	public static String xAxis(int start, int end, int figures) {
		
		String xAxis = "";
		
		int currDigit;
		int currNumberDigits;
		for (int i = figures; i > 0; i--) {
			for (int k = 0; k < figures; k++) {
				xAxis += "  ";
			}
			for (int j = start; j < end; j++) {
				currDigit = nthDigit(j, i - 1);
				currNumberDigits = numberOfDigits(j);
				if (i == currNumberDigits + 1 && j < 0) {
					xAxis += "-|";
				} else if (i <= currNumberDigits) {
					xAxis += currDigit + "|";
				} else if (j == 0 && i == 1) {
					xAxis += "0|";
				} else {
					xAxis += "  ";
				}
			}
			xAxis += "\n";

		}
		
		return xAxis;
	}

	//Java's default modulus returns a negative number when a is negative for some reason.
	public static double mod(double a, double n) {
		if (a >= 0) {
			return a % n;
		} else {
			return n + a % n;
		}

	}
	
	public static int sign(int n) {
		if (n > 0) {
			return 1;
		} else if (n < 0) {
			return -1;
		} else {
			return 0;
		}
	}
	
	// Returns true if x is between a and b
	public static boolean between(double x, double a, double b) {
		return (a <= x && x <= b) || (a >= x && x >= b);
	}

	public static boolean nearlyEquals(double a, double b) {
		return Math.abs(a - b) < Constants.EPSILON;
	}
	
}