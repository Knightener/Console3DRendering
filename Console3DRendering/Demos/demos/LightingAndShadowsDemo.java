package demos;

import classes3D.R3Point;
import control.User;
import control.UserControl;
import control.World;
import rendering3D.LightSource;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;
import zBuffered2DRendering.ZImage;

public class LightingAndShadowsDemo {
	public static void main(String args[]) {
		ZImage image = new ZImage(-60, 60, -40, 40);

		// Floor vertices.
		R3Point[] floor = { new R3Point(2, 0, 2), new R3Point(2, 0, -2), new R3Point(-2, 0, -2),
			new R3Point(-2, 0, 2) };

		// Top vertices.
		R3Point[] top = { new R3Point(2, -4, 2), new R3Point(2, -4, -2), new R3Point(-2, -4, -2),
			new R3Point(-2, -4, 2) };

		// Door cut-out vertices
		R3Point[] door = { new R3Point(2, 0, 0.5), new R3Point(2, -2, 0.5),
			new R3Point(2, -2, -0.5), new R3Point(2, 0, -0.5) };

		// Point of the roof.
		R3Point[] roof = { new R3Point(0, -6, 0) };

		// Various textures that will be used.
		Texture checkerboard = new Texture(TexturePresets.CHECKERBOARD, 1);
		Texture bricks = new Texture(TexturePresets.BRICKS, 0.2);
		Texture gradient = new Texture(TexturePresets.GRADIENT, 0.2);

		Observer observer = new Observer(image, 70);

		// The lightsource. 
		LightSource lightSource = new LightSource(-4, -6, 6, 15);

		
		// Plane everything is on. 
		Mesh plane = new Mesh(observer, new R3Point(-10, 0.3, 10), new R3Point(10, 0.3, 10),
			new R3Point(10, 0.3, -10), new R3Point(-10, 0.3, -10));

		// Adding face 
		plane.createFace(checkerboard, 3, 2, 1, 0);

		// The house
		Mesh house = new Mesh(observer, floor[0], floor[1], floor[2], floor[3], top[0], top[1],
			top[2], top[3], door[0], door[1], door[2], door[3], roof[0]);

		// The floor of the house.
		house.createFace(checkerboard, 0, 1, 2, 3);
		
		// Walls of the house 
		house.createFace(bricks, 1, 2, 6, 5);
		house.createFace(bricks, 2, 3, 7, 6);
		house.createFace(bricks, 3, 0, 4, 7);
		
		// Door of the house
		house.createFace(bricks, 8, 9, 10, 11, 1, 5, 4, 0);

		/*
		 * Roof of the house. Note this is not the most intuitive way of constructing
		 * shapes. Although it works, I plan to make a more intuitive system eventually.
		 */
		house.createFace(gradient, 4, 5, 12);
		house.createFace(gradient, 6, 5, 12);
		house.createFace(gradient, 7, 6, 12);
		house.createFace(gradient, 4, 7, 12);

		User.setUser(observer);

		World.addObject(house);
		World.addObject(plane);

		World.setLightSource(lightSource);

		World.addShadow(house);

		User.toggleFrameRenderSpeed();
		User.toggleAvgRenderSpeed();

		observer.setPosition(new R3Point(6, -1, 4));
		observer.setOrientation(2, 0);
		
		User.printView();
		new UserControl();
	}
}
