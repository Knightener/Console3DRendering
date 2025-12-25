package rendering3D;
import java.util.*;

public class Form {

	ArrayList<RelativeTriangle> faces;

	public Form(ArrayList<RelativeTriangle> faces) {
		this.faces = faces;
	}

	/*
	 * The further forward triangles are rendered first, so the closer triangles
	 * overlap them.
	 */
	public void determineRenderingOrder() {
		Collections.sort(faces);
	}
	
	public void octahedron() {
		
	}

}
