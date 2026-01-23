package rendering3D;
import java.util.*;

public class Form {

	ArrayList<RelativeComponent> components;

	public Form(ArrayList<RelativeComponent> components) {
		this.components = components;
	}

	public void updatePerspective() {
		for (RelativeComponent component : components) {
			component.updatePerspective();
		}
	}

	/*
	 * The further forward simplexes are rendered first, so the closer simplexes
	 * overlap them.
	 * 
	 * This method of rendering is currently unused, however, I plan to eventually
	 * use a hybrid method of both zBuffering and depth sorting to optimize.
	 */
	public void determineRenderingOrder() {
		Collections.sort(components);
	}

	public void addNormals() {

		int size = components.size();

		for (int i = 0; i < size; i++) {

			RelativeComponent curr = components.get(i);

			if (curr instanceof RelativePolygon) {
				components.add(((RelativePolygon) curr).getUnitNormal());
			}
		}
	}

}
