package demos;

import java.util.ArrayList;

import classes3D.R3Point;
import rendering3D.Form;
import rendering3D.Observer;
import rendering3D.RelativePolygon;
import rendering3D.RelativeSimplex;
import texturing.Texture;
import texturing.TexturePresets;
import zBuffered2DRendering.ZImage;

public class TexturedShape {

	/*
	 * A demo showing the observer rotating around a triangular prism with a
	 * checkerboard pattern.
	 */
	public static void main(String args[]) {

		// Dimensions of the printed image.
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
		Texture texture = new Texture(TexturePresets.CHECKERBOARD, 0.6);
	
		// Observer's position.
		R3Point position = new R3Point(0, 0, 0);
		
		// Observer's horizontal angle,
		double theta = 0;
		
		Observer observer = new Observer(position, theta, 0, image, 70);

		// Faces of the shape.
		RelativePolygon t1 = new RelativePolygon(observer,texture,p1,p2,p3);
		RelativePolygon t2 = new RelativePolygon(observer,texture,q1,q2,q3);
		RelativePolygon t3 = new RelativePolygon(observer,texture,p1,p2,q2,q1);
		RelativePolygon t4 = new RelativePolygon(observer,texture,p3,p1,q1,q3);

		ArrayList<RelativeSimplex> f = new ArrayList<RelativeSimplex>();

		f.add(t1);
		f.add(t2);
		f.add(t3);
		f.add(t4);

		Form form = new Form(f);
		
		while (true) {

			theta += 0.05;

			position = new R3Point(4 * Math.sin(theta), 0, -4 * Math.cos(theta));

			observer.setPosition(position);
			observer.setOrientation(theta, 0);

			form.updatePerspective();
			observer.renderDirectly(form);
			image.texturize();
			image.display();
			image.clear();
			
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}

		}
	}
}
