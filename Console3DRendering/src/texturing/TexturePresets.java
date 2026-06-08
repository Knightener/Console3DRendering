package texturing;

import other.ArrayFunctions;

public enum TexturePresets {

	// Some default textures to use as a base. Will expand soon.

	CHECKERBOARD(new double[][] { 
		{ 1, 0 }, 
		{ 0, 1 } }),

	BRICKS(new double[][] { 
		{ 0, 0, 0.0, 0.0 },
		{ 0, 0.5, 0.5, 0.5 }, 
		{ 0, 0.5, 0.5, 0.5 }, 
		{ 0, 0, 0, 0 }, 
		{ 0.5, 0.5, 0, 0.5 },
		{ 0.5, 0.5, 0, 0.5 } }),

	GRADIENT(new double[][] { { 0, 0.25, 0.5, 0.75, 1, 0.75, 0.5, 0.25 } }),

	WHITE(new double[][] {
		{1}});

	private double[][] texture;

	private TexturePresets(double[][] texture) {
		this.texture = texture;
	}
	
	public double[][] getTexture() {
		return ArrayFunctions.copy(texture);
	}
}
