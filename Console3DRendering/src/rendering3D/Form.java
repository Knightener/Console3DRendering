package rendering3D;
import java.util.*;

public class Form {

	ArrayList<RelativeSimplex> components;

	public Form(ArrayList<RelativeSimplex> components) {
		this.components = components;
	}

	/*
	 * The further forward triangles are rendered first, so the closer triangles
	 * overlap them.
	 */
	public void determineRenderingOrder() {
		Collections.sort(components);
	}
	

}
