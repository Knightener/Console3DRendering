package shading;

import java.util.List;

import classes3D.R3Point;
import rendering3D.Mesh;

public class ShadowVolume {

	private Mesh shadowVolume;
	private static final double EXTEND_MULTIPLIER = 1000;

	public ShadowVolume(Mesh mesh, R3Point lightSource) {
		shadowVolume = mesh.getShadowVolume(lightSource, EXTEND_MULTIPLIER);
	}


}
