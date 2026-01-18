package texturing;

import other.ArrayFunctions;

public enum TexturePresets {

	CHECKERBOARD(new double[][] { 
		{ 1, 0 }, 
		{ 0, 1 } });
	
	

	private double[][] texture;

	private TexturePresets(double[][] texture) {
		this.texture = texture;
	}
	
	public double[][] getTexture() {
		return ArrayFunctions.copy(texture);
	}
}
