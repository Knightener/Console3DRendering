package shading;

import java.util.ArrayList;
import java.util.List;

import classes3D.R3Point;
import rendering3D.Mesh;

public class ShadowVolume {

	private Mesh shadowVolume;
	private static final double EXTEND_MULTIPLIER = 10;

	public ShadowVolume(Mesh mesh, R3Point lightSource) {
		List<R3Point> border = mesh.getVisibleBorder(lightSource);

		int halfSize = border.size();

		for (int i = 0; i < halfSize; i++) {
			border.add(border.get(i).extendFrom(lightSource, EXTEND_MULTIPLIER));
		}

		shadowVolume = new Mesh(mesh.getObserver(), border);
		for (int i = 0; i < halfSize; i++) {
			shadowVolume.createFace(null, i, (i + 1) % halfSize, (i + 1) % halfSize + halfSize, i + halfSize);
		}
	}
	

}
