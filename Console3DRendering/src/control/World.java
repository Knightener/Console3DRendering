package control;

import java.util.ArrayList;
import java.util.List;

import rendering3D.Renderable;

public class World {
	/*
	 * Class consisting of renderable objects that will be viewed by the user.
	 */
	private static List<Renderable> world = new ArrayList<Renderable>(); 
	
	public static void addObject(Renderable object) {
		if (object.getObserver() == User.getUser()) {
			world.add(object);
		} else {
			throw new IllegalArgumentException("Object must be associated with current user observer.");
		}
	}
	
	public static void render() {
		for (Renderable object : world) {
			object.updatePerspective();
			object.render();
		}
	}
}
