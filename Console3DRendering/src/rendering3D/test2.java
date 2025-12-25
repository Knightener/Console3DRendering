package rendering3D;

import classes2D.*;
import classes3D.*;
import rendering2D.*;

import java.util.*;

public class test2 {

	public static void main(String args[]) {
		DrawingMethods2D image2 = new DrawingMethods2D(-60, 60, -60, 60);

		R3Point position = new R3Point(0, 0, 0);
		R2Point.adjustShades(x -> x * x);
		DrawingMethods3D observer = new DrawingMethods3D(position, 1, 0, image2, 20);

		R3Point p1 = new R3Point(0, -1, 1);
		R3Point p2 = new R3Point(-1, 1, 1);
		R3Point p3 = new R3Point(1, 1, 1);


		Triangle triangle = new Triangle(new R3Point(0, -1, 1),new R3Point(-1, 1, 1),new R3Point(1, 1, 1));
		
		RelativeTriangle t = new RelativeTriangle(triangle,4);

		double theta = 0;
		double phi = 0;
		while (true) {
			observer = new DrawingMethods3D(position, theta, phi, image2, 20);
			observer.updatePerspective(t);
			observer.renderDirectly(t);
			
			image2.draw(observer.line(p2, p3));
			image2.draw(observer.line(p1, p3));
			image2.draw(observer.line(p1, p2));
			long startTime = System.nanoTime();
			image2.display();
			long endTime = System.nanoTime();
			image2.clear();

			theta +=0.05;
			System.out.println(phi);
			System.out.println((endTime - startTime) / 1000000);
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}

		}
	}
}
