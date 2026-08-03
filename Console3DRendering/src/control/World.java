package control;

import java.util.ArrayList;
import java.util.List;

import classes3D.R3Point;
import rendering3D.Mesh;
import rendering3D.Renderable;
import rendering3D.Spotlight;
import zBuffered2DRendering.ZBuffer;

public class World {
	/*
	 * Class consisting of renderable objects that will be viewed by the user.
	 */
	private static List<Renderable> world = new ArrayList<Renderable>();

	static Spotlight spotlight = new Spotlight(new R3Point(-4, -6, 6), Math.PI, 0,
		new ZBuffer(-240, 240, -160, 160), 100);

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
		spotlight.render(object);
	}

	// Adds a light source to the world.
	public static void setSpotlight(Spotlight spotlight) {
		World.spotlight = spotlight;
	}

	// Renders the world to the user.
	public static void render() {
		for (Renderable object : world) {
			object.updatePerspective();
			object.render();
		}
	}
}
