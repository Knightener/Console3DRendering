package control;

import java.util.ArrayList;
import java.util.List;

import rendering3D.LightSource;
import rendering3D.Mesh;
import rendering3D.RelativePoint;
import rendering3D.Renderable;

public class World {
	/*
	 * Class consisting of renderable objects that will be viewed by the user.
	 */
	private static List<Renderable> world = new ArrayList<Renderable>(); 
	
	private static List<Mesh> shadowVolumes = new ArrayList<Mesh>();
	
	// Only one light source allowed for now. 
	static LightSource lightSource;
	
	// For rendering purposes. 
	private static RelativePoint lightSourcePoint;

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
				shadowVolumes.add(object.getCappedShadowVolume(lightSource, shadowExtendMultiplier));
		} else {
			throw new IllegalArgumentException("Object must be associated with current user observer.");
		}
	}
	
	// Adds a light source to the world.
	public static void setLightSource(LightSource lightSource) {
		World.lightSource = lightSource;
		lightSourcePoint = new RelativePoint(lightSource.getPosition(), User.getUser());
	}

	// Renders the world to the user. 
	public static void render() {
		if (lightSource != null) {
			lightSourcePoint.updatePerspective();
			lightSourcePoint.render();
		}
		for (Renderable object : world) {
			object.updatePerspective();
			object.render();
		}
	}
	
	// Debug function. Renders the shadows. 
	public static void renderShadows() {
		for (Mesh shadowVolume : shadowVolumes) {
			shadowVolume.updatePerspective();
			shadowVolume.render();
		}
	}
	
	
	// Shades the world. 
	public static void shade() {
		for (Mesh shadowVolume : shadowVolumes) {
			shadowVolume.updatePerspective();
			shadowVolume.writeToStencil();
		}
	}
}
