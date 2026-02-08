package texturing;

import classes2D.R2Point;
import other.MiscFunctions;
import rendering2D.ShadeHandling;

public class Texture {

	private static ShadeHandling shadeHandling = new ShadeHandling();

	// Array of shades.
	private int[][] texture;

	private int rows;
	private int cols;

	// 1/scaleFactor is how much the texture is scaled up.
	private double scaleFactor;

	public Texture(TextureBuilder textureBuilder, double scale) {

		int rows = textureBuilder.getRows();
		int cols = textureBuilder.getCols();

		double[][] doubleTexture = textureBuilder.getTexture();

		texture = new int[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				texture[i][j] = shadeHandling.determineShade(doubleTexture[i][j]);
			}
		}

		this.rows = rows;
		this.cols = cols;

		scaleFactor = 1 / scale;

	}

	public Texture(TexturePresets texturePreset, double scale) {
		this(new TextureBuilder(texturePreset), scale);
	}

	public double getScaleFactor() {
		return scaleFactor;
	}
	
	// Extends the texture to all of ZxZ by tessellation. 
	public int determineShadeAt(int right, int down) {
		return texture[MiscFunctions.mod(down, rows)][MiscFunctions.mod(right, cols)];
	}

	public int determineShadeAt(R2Point point) {
		return determineShadeAt((int) Math.floor(scaleFactor * point.getRight()),
				(int) Math.floor(scaleFactor * point.getDown()));
	}
}
