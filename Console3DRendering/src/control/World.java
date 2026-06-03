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
		world.add(object);
	}
}
