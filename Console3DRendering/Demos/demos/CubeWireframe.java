package demos;

import classes3D.R3Point;
import rendering3D.Observer;
import zBuffered2DRendering.ZFigure;
import zBuffered2DRendering.ZImage;

public class CubeWireframe {

	public static void main(String args[]) {

		/*
		 * Simple demo showing the line function. This was one of the first 3D things I
		 * built within this project.
		 */
		ZImage image = new ZImage(-60, 60, -50, 50);

		// Cube vertices

		R3Point p000 = new R3Point(1, 1, 1);
		R3Point p001 = new R3Point(1, 1, -1);
		R3Point p010 = new R3Point(1, -1, 1);
		R3Point p011 = new R3Point(1, -1, -1);
		R3Point p100 = new R3Point(-1, 1, 1);
		R3Point p101 = new R3Point(-1, 1, -1);
		R3Point p110 = new R3Point(-1, -1, 1);
		R3Point p111 = new R3Point(-1, -1, -1);

		double theta = 0;

		Observer observer = new Observer(new R3Point(0, 0, 0), theta, 0, image, 20);

		while (true) {

			observer.setOrientation(theta, 0);

			ZFigure figure = new ZFigure();

			// x axis aligned lines.
			figure.add(observer.line(p000, p100, 8, 3));
			figure.add(observer.line(p001, p101, 8, 3));
			figure.add(observer.line(p010, p110, 8, 3));
			figure.add(observer.line(p011, p111, 8, 3));

			// y axis aligned lines.
			figure.add(observer.line(p000, p010, 8, 3));
			figure.add(observer.line(p001, p011, 8, 3));
			figure.add(observer.line(p100, p110, 8, 3));
			figure.add(observer.line(p101, p111, 8, 3));

			// z axis aligned lines
			figure.add(observer.line(p000, p001, 8, 3));
			figure.add(observer.line(p010, p011, 8, 3));
			figure.add(observer.line(p100, p101, 8, 3));
			figure.add(observer.line(p110, p111, 8, 3));

			image.draw(figure);
			image.display();

			image.clear();
			theta += 0.05;
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}

		}
	}
}
