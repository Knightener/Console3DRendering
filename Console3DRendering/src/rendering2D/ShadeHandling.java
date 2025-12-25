package rendering2D;
import interfaces.*;
public class ShadeHandling {

	private double[] shadePartition;

	public static final String[] DEFAULT_SHADES = { "  ", " ░", "░░", "░▒", "▒▒", "▒▓", "▓▓", "▓█", "██" };

	static String[] shades = DEFAULT_SHADES;
	
	// Very commonly used expression, stored as an instance variable for convenience.
	static int maxPossibleShade = shades.length - 1;

	public ShadeHandling() {
		shadePartition = new double[maxPossibleShade + 1];
		for (int i = 0; i <= maxPossibleShade; i++) {
			shadePartition[i] = (i + 1) / (double) (maxPossibleShade + 1);
		}
	}

	public int determineShade(double x, int maxShade) {

		if (x < 0 || x > 1) {
			throw new IllegalArgumentException();
		}

		int numPointsPassed = 0;

		while (shadePartition[numPointsPassed] < x) {
			numPointsPassed++;
		}

		return Math.min(maxShade, numPointsPassed);
	}

	public int determineShade(double x) {

		return determineShade(x, maxPossibleShade);
	}

	public void adjustShades(RealFunction gauge) {
		for (int i = 0; i < shadePartition.length; i++) {
			shadePartition[i] = gauge.f(shadePartition[i]);
		}
	}

	public static int getMaxPossibleShade() {
		return maxPossibleShade;
	}
	
	
	
	
}
