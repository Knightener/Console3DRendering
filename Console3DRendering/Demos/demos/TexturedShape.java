package demos;

import classes3D.R3Point;
import control.User;
import control.World;
import control.RotationDirection;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;
import zBuffered2DRendering.ZImage;

public class TexturedShape {

	/*
	 * A demo showing the observer rotating around a triangular prism with a
	 * checkerboard pattern.
	 */
	public static void main(String args[]) {

		/*
		 * Dimensions of the printed image. You may adjust this to fit your screen. The
		 * first two coordinates are the horizontal span, and the last two coordinates
		 * are the vertical span (note y axis is flipped).
		 */
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
		
		// Observer's horizontal angle.
		double theta = 0;
		
		Observer observer = new Observer(position, theta, 0, image, 70);

		// The shape.
		Mesh shape = new Mesh(observer,p1,p2,p3,q1,q2,q3);
		
		// Adding faces.
		shape.createFace(texture, 0,1,2);
		shape.createFace(texture, 0,1,4,3);
		shape.createFace(texture, 3,4,5);
		shape.createFace(texture, 2,0,3,5);
		
		User.setUser(observer);
		User.setRotationSpeed(0.05);
		
		World.addObject(shape);
		
		while (true) {

			theta += 0.05;

			// The observer is moving in a circle around the shape. 
			position = new R3Point(4 * Math.sin(theta), 0, -4 * Math.cos(theta));

			User.setPosition(position);
			User.turn(RotationDirection.LEFT);

			shape.updatePerspective();
			shape.render();
			
			User.printView();

			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}

		}
	}
}
