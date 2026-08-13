package control;

import java.util.ArrayList;
import java.util.List;

import rendering3D.LightSource;
import rendering3D.Mesh;
import rendering3D.Renderable;
import rendering3D.Spotlight;
import zBuffered2DRendering.ZBuffer;

public class World {
	/*
	 * Class consisting of renderable objects that will be viewed by the user.
	 */
	private static List<Renderable> world = new ArrayList<Renderable>();
	
	static List<Spotlight> spotlights = new ArrayList<Spotlight>();
	
	// Adds an object to the world.
	public static void addObject(Renderable object) {
		if (object.getObserver() == User.getUser()) {
			world.add(object);
		} else {
			throw new IllegalArgumentException(
				"Object must be associated with current user observer.");
		}
	}

	// Adds the shadows of the mesh to the world.
	public static void addShadow(Mesh object) {
		for (Spotlight spotlight : spotlights) {
			spotlight.render(object);
		}
	}

	public static void addSpotlight(Spotlight spotlight) {
		spotlights.add(spotlight);
	}

	// Renders the world to the user.
	public static void render() {
		for (Renderable object : world) {
			object.updatePerspective();
			object.render();
		}
	}
}
