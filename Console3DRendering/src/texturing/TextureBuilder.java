package texturing;

import other.ArrayFunctions;

public class TextureBuilder {

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
