package texturing;

import other.ArrayFunctions;

public class TextureBuilder {

	/*
	 * Intermediate class before Texture. Stores shades as doubles between 0 and 1
	 * (where 0 is no shade and 1 is full shade). Will extend capabilities and add
	 * methods for editing textures.
	 */
	private double[][] texture;

	private int rows;
	private int cols;

	public TextureBuilder(TexturePresets texturePreset) {
		texture = texturePreset.getTexture();

		rows = texture.length;
		cols = texture[0].length;
	}

	public double[][] getTexture() {
		return ArrayFunctions.copy(texture);
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}
	
}
