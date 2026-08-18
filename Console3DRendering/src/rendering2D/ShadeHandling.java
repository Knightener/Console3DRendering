package rendering2D;


public class ShadeHandling {

	// This class is for determining a mapping from [0,1] to shading characters.
	

	// Easier to spot: you will rarely see pure white and black next to each other. 
	public static final String NO_SHADE = " █";
	
	public static final String[] BLOCK_SHADES = getShadeArray(" ░▒▓█");
	
	// ASCII characters ordered by brightness. 
	public static final String[] ASCII_SHADES =getShadeArray(
		" `.-':_,^=;><+!rc*/z?sLTv)J7(|Fi{C}fI31tlu[neoZ5Yxjya]2ESwqkP6h9d4VpOGbUAKXHm8RD#$Bg0MNWQ%&@");
	
	static String[] shades = BLOCK_SHADES;
	
	// Very commonly used expression, stored as an instance variable for convenience.
	public static final int MAX_SHADE = shades.length - 1;

	public static int determineShade(double x, int maxShade) {
		return Math.min(maxShade, determineShade(x));
	}

	public static int determineShade(double x) {
		// Negative shade correspond to no shade
		if (x < 0) {
			return -1;
		}
		
		return Math.min(MAX_SHADE, (int) (x * MAX_SHADE));
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
	
	public static String[] getShadeArray(String shades) {
		return getShadeArray(shades.toCharArray());
	}

	public static int darken(int shade, int darkeningFactor) {
		// temp implementation for testing
		if (darkeningFactor > 0) {
			return shade / 3;
		} else {
			return shade;
		}
	}

}
