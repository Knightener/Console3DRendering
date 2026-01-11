package rendering3D;
import java.util.*;

public class Form {

	ArrayList<RelativeSimplex> components;

	public Form(ArrayList<RelativeSimplex> components) {
		this.components = components;
	}

	/*
	 * The further forward simplexes are rendered first, so the closer simplexes
	 * overlap them.
	 * 
	 * This method will return an inaccurate result if any two components intersect
	 * (besides the edges).
	 */
	public void determineRenderingOrder() {
		Collections.sort(components);
	}

	public void addNormals() {

		int size = components.size();

		for (int i = 0; i < size; i++) {

			RelativeSimplex curr = components.get(i);

			if (curr instanceof RelativePolygon) {
				components.add(((RelativePolygon) curr).getUnitNormal());
			}
		}
	}

}
