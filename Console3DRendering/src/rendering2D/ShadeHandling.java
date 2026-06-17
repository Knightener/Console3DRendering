package rendering2D;

import functionalInterfaces.RealFunction;

public class ShadeHandling {

	// This class is for determining a mapping from [0,1] to shading characters.
	
	// Shade cutoffs.
	private double[] shadePartition;

	private static final String[] DEFAULT_SHADES = { "  ", " ░", "░░", "░▒", "▒▒", "▒▓", "▓▓", "▓█", "██" };
	
	// ASCII characters ordered by brightness. 
	private static final String[] ASCII_SHADES =getShadeArray(
		" `.-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@".toCharArray());

	static String[] shades = DEFAULT_SHADES;
	
	// Very commonly used expression, stored as an instance variable for convenience.
	public static final int MAX_SHADE = shades.length - 1;

	public ShadeHandling() {
		shadePartition = new double[MAX_SHADE + 1];
		for (int i = 0; i <= MAX_SHADE; i++) {
			shadePartition[i] = (i + 1) / (double) (MAX_SHADE + 1);
		}
	}

	public int determineShade(double x, int maxShade) {
		return Math.min(maxShade, determineShade(x));
	}

	public int determineShade(double x) {
		if (x < 0 || x > 1) {
			throw new IllegalArgumentException();
		}

		int numPointsPassed = 0;

		while (shadePartition[numPointsPassed] < x) {
			numPointsPassed++;
		}

		return numPointsPassed;
	}

	/*
	 * Turns an array of chars into a shade array by adding half tones.
	 */
	public static String[] getShadeArray(char[] shades) {
		String[] shadeArray = new String[2 * shades.length - 1];

		for (int i = 0; i < shadeArray.length; i++) {
			shadeArray[i] = "" + shades[i / 2] + shades[(i + 1) / 2];
		}
		return shadeArray;
	}

	public static int darken(int shade, int darkeningFactor) {
		// temp implementation for testing
		if (darkeningFactor > 0) {
			return shade / 3;
		} else {
			return shade;
		}
	}

	/*
	 * Adjusts the shade partition using an increasing function with domain and
	 * range [0,1]. Bringing down the cutoffs will make shades appear stronger, and
	 * bringing them up will make them appear lighter.
	 */
	public void adjustShades(RealFunction adjustment) {
		for (int i = 0; i < shadePartition.length; i++) {
			shadePartition[i] = adjustment.f(shadePartition[i]);
		}
	}
}
