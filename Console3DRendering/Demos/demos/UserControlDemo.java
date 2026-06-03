package demos;

import classes3D.R3Point;
import control.User;
import control.UserControl;
import control.World;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;
import zBuffered2DRendering.ZImage;

public class UserControlDemo {

	/*
	 * Demo for user movement. 
	 */
	public static void main(String args[]) {

		ZImage image = new ZImage(-60, 60, -50, 50);
		
		// Front face vertices.
		R3Point p1 = new R3Point(0, -1, 1);
		R3Point p2 = new R3Point(-1, 1, 1);
		R3Point p3 = new R3Point(1, 1, 1);

		// Back face vertices.
		R3Point q1 = new R3Point(0, -1, -1);
		R3Point q2 = new R3Point(-1, 1, -1);
		R3Point q3 = new R3Point(1, 1, -1);
		
		// Texture of the shape.
		Texture texture = new Texture(TexturePresets.CHECKERBOARD, 0.5);
	
		// Observer's position.
		R3Point position = new R3Point(0, 0, 0);

		Observer observer = new Observer(position, 0, 0, image, 70);

		Mesh plane = new Mesh(observer, new R3Point(-10, 2, 10), new R3Point(10, 2, 10),
			new R3Point(10, 2, -10), new R3Point(-10, 2, -10));

		plane.createFace(new Texture(TexturePresets.BRICKS, 0.2), 0, 1, 2, 3);

		// The shape.
		Mesh shape = new Mesh(observer,p1,p2,p3,q1,q2,q3);
		
		// Adding faces.
		shape.createFace(texture, 0,1,2);
		shape.createFace(texture, 0,1,4,3);
		shape.createFace(texture, 3,4,5);
		shape.createFace(texture, 2,0,3,5);
		
		User.setUser(observer);
		User.setRotationSpeed(0.05);
		User.setMovementSpeed(0.1);
		
		World.addObject(shape);
		World.addObject(plane);
		
		new UserControl();
	}
}
