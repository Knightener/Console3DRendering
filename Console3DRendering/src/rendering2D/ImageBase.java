package rendering2D;

import classes2D.R2Point;
import other.ArrayFunctions;
import other.MiscFunctions;

public abstract class ImageBase {

	/*
	 * This class was pulled from the Image class. It was created so ZImage could
	 * extend it instead of Image since Image comes with a lot of baggage.
	 * 
	 * Furthermore, most of the drawing methods in Image have to be rewritten to
	 * account for the Z buffer. Although the changes to the methods are minor, they
	 * cannot be implemented using only the output of the original drawing methods.
	 */
	protected int[][] image;
	protected int imageRows;
	protected int imageCols;

	/*
	 * When the image is displayed, the points that will be visible are those with
	 * horizontal [resp. vertical] component greater than left bound [resp. upBound]
	 * (inclusive) and lesser than rightBound [resp. downBound] (not inclusive).
	 */
	protected int leftBound, rightBound, upBound, downBound;

	/*
	 * Stored as an instance variable for convenience. This represents the furthest
	 * point from the origin that is visible in terms of the Chebyshev metric.
	 */
	private int furthestOut;

	/*
	 * These are stored as instance variables so that the displayCoordinates method
	 * doesn't have to recalculate them every time its called.
	 */
	private String xAxis;
	private String[] yAxis;

	/*
	 * Creates a new image out of the given int array, shifted according to the
	 * given left, up parameters.
	 */
	protected ImageBase(int[][] arr, int left, int up) {
		if (!ArrayFunctions.passesCheck(arr, n -> n >= 0 && n <= ShadeHandling.maxPossibleShade)) {
			throw new IllegalArgumentException();
		}

		image = ArrayFunctions.rectangulize(arr);

		imageRows = image.length;
		imageCols = image[0].length;

		leftBound = -left;
		rightBound = imageCols - left;
		upBound = -up;
		downBound = imageRows - up;

		furthestOut = Math.max(Math.max(Math.abs(leftBound), Math.abs(rightBound - 1)),
				Math.max(Math.abs(upBound), Math.abs(downBound - 1)));

		xAxis = MiscFunctions.xAxis(leftBound, rightBound, 4);
		yAxis = new String[imageRows];

		for (int i = upBound; i < downBound; i++) {
			yAxis[i + up] = MiscFunctions.spacedNumber(i, 4);
		}
	}

	protected ImageBase(int leftEnd, int rightEnd, int upEnd, int downEnd) {
		this(new int[downEnd - upEnd + 1][rightEnd - leftEnd + 1], -leftEnd, -upEnd);
	}

	protected ImageBase(ImageBase image) {
		this.image = ArrayFunctions.copy(image.image);
		
		imageRows = image.imageRows;
		imageCols = image.imageCols;
		
		leftBound = image.leftBound;
		rightBound = image.rightBound;
		upBound = image.upBound;
		downBound = image.downBound;
		
		xAxis = image.xAxis;
		yAxis = image.yAxis;
		
		furthestOut = image.furthestOut;
		
	}
	
	public void clear() {
		image = new int[imageRows][imageCols];
	}

	public String[][] convert() {

		String[][] shadeArr = new String[imageRows][imageCols];

		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				shadeArr[i][j] = ShadeHandling.shades[image[i][j]];
			}
		}
		return shadeArr;
	}

	public void display() {
		String[][] shadeArr = convert();

		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				System.out.print(shadeArr[i][j]);
			}
			System.out.println();
		}
	}

	public void displayCoordinates() {
		String[][] shadeArr = convert();

		System.out.print(xAxis);
		for (int i = 0; i < imageRows; i++) {
			System.out.print(yAxis[i]);
			for (int j = 0; j < imageCols; j++) {
				System.out.print(shadeArr[i][j]);
			}
			System.out.println();
		}
	}

	public void setShade(int right, int down, int shade) {
		
		if (!(shade >= 0 && shade <= ShadeHandling.maxPossibleShade)) {
			throw new IllegalArgumentException();
		}
		
		image[down][right] = shade;
	}
	
	public int getShade(int right, int down) {
		return image[down][right];
	}

	public void invert() {
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageCols; j++) {
				image[i][j] = ShadeHandling.maxPossibleShade - image[i][j];
			}
		}
	}
	
	public boolean isVisible(R2Point point) {

		if (point == null) {
			return false;
		}
		double right = point.getRight();
		double down = point.getDown();

		/*
		 * The +-2s are arbitrary. I only added them to have a comfortable margin of
		 * error as part of the point through the .approximate method might be visible
		 */
		return (leftBound - 2 <= right && right < rightBound + 2 && upBound - 2 <= down && down < downBound + 2);
	}
	
	public int getLeftBound() {
		return leftBound;
	}

	public int getRightBound() {
		return rightBound;
	}

	public int getUpBound() {
		return upBound;
	}

	public int getDownBound() {
		return downBound;
	}
	
	public int getFurthestOut() {
		return furthestOut;
	}


}
