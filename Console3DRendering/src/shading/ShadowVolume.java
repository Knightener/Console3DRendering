package shading;

import rendering3D.LightSource;
import rendering3D.Mesh;

public class ShadowVolume {

	private Mesh shadowVolume;
	private static final double EXTEND_MULTIPLIER = 1000;

	public ShadowVolume(Mesh mesh, LightSource lightSource) {
		shadowVolume = mesh.getShadowVolume(lightSource, EXTEND_MULTIPLIER);
	}


}
