package control;

import java.util.ArrayList;
import java.util.List;

import classes3D.R3Point;
import rendering3D.Mesh;
import rendering3D.Renderable;

public class World {
	/*
	 * Class consisting of renderable objects that will be viewed by the user.
	 */
	private static List<Renderable> world = new ArrayList<Renderable>(); 
	
	private static List<Mesh> shadowVolumes = new ArrayList<Mesh>();
	
	private static List<R3Point> lightSources = new ArrayList<R3Point>();

	private static int shadowExtendMultiplier = 300; 
	
	// Adds an object to the world. 
	public static void addObject(Renderable object) {
		if (object.getObserver() == User.getUser()) {
			world.add(object);
		} else {
			throw new IllegalArgumentException("Object must be associated with current user observer.");
		}
	}

	// Adds the shadows of the mesh to the world. 
	public static void addShadow(Mesh object) {
		if (object.getObserver() == User.getUser()) {
			for (R3Point lightSource : lightSources) {
				shadowVolumes.add(object.getShadowVolume(lightSource, shadowExtendMultiplier));
			}
		} else {
			throw new IllegalArgumentException("Object must be associated with current user observer.");
		}
	}
	
	// Adds a light source to the world. 
	public static void addLightSource(R3Point lightSource) {
		lightSources.add(lightSource);
	}
	
	// Renders the world to the user. 
	public static void render() {
		for (Renderable object : world) {
			object.updatePerspective();
			object.render();
		}
	}
	
	// Shades the world. 
	public static void shade() {
		for (Mesh shadowVolume : shadowVolumes) {
			shadowVolume.writeToStencil();
		}
		User.getUser().getView().addStencil();
	}
}
