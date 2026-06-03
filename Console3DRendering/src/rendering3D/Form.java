package rendering3D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import classes3D.R3Point;

public class Form {

	ArrayList<RelativeComponent> components;

	public Form(ArrayList<RelativeComponent> components) {
		this.components = components;
	}

	public Form() {
		this.components = new ArrayList<RelativeComponent>();
	}

	// Constructs a form by connecting the points in a loop. 
	public static Form connect(List<R3Point> points, Observer observer) {
		Form connected = new Form();

		for (int i = 0; i < points.size(); i++) {
			connected.add(new RelativeLine(points.get(i), points.get((i + 1) % points.size()), observer));
		}
		
		return connected; 
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
	
	public void add(RelativeComponent component) {
		components.add(component);
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
	
	public void render() {
		for (RelativeComponent component : components) {
			component.render();
		}
	}
	public int getSize() {
		return components.size();
	}

}
