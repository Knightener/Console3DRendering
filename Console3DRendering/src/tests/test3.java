package rendering3D;

import classes3D.*;
import other.Constants;
import rendering2D.DrawingMethods2D;
import java.util.*;

public class test3 {

//	public static void clearScreen() {
//
//		System.out.println("hello");
//		System.out.print("\033[H\033[2J");
//
//	}

	public static void main(String args[]) {

		R3Point position = new R3Point(1, 0, 0);
		double theta = 0;
		DrawingMethods2D image = new DrawingMethods2D(-80, 80, -55, 55);

		LightSource lightsource = new LightSource(new R3Point(2,10,2));
		
		R3Point p1 = new R3Point(0, -1, 1);
		R3Point p2 = new R3Point(-1, 1, 1);
		R3Point p3 = new R3Point(1, 1, 1);

		R3Point q1 = new R3Point(0, -1, -1);
		R3Point q2 = new R3Point(-1, 1, -1);
		R3Point q3 = new R3Point(1, 1, -1);

		Triangle triangle1 = new Triangle(p1, p2, p3);

		Triangle triangle2 = new Triangle(q1, q2, q3);

		RelativeTriangle t1 = new RelativeTriangle(p1, p2, p3, lightsource);
		RelativeTriangle t2 = new RelativeTriangle(q1, q2, q3, lightsource);

		RelativeTriangle t3 = new RelativeTriangle(p1, q2, q1, lightsource);
		RelativeTriangle t4 = new RelativeTriangle(p1, p2, q2, lightsource);

		RelativeTriangle t5 = new RelativeTriangle(p1, q1, q3, lightsource);
		RelativeTriangle t6 = new RelativeTriangle(p3, p1, q3, lightsource);

		ArrayList<RelativeTriangle> f = new ArrayList<RelativeTriangle>();

		f.add(t1);
		f.add(t2);
		f.add(t3);
		f.add(t4);
		f.add(t5);
		f.add(t6);

		Form form = new Form(f);

		while (true) {

		theta += 0.05;

		position.setRight(Math.sin(theta));
		position.setForward(-Math.cos(theta));
		position.scale(4);

		DrawingMethods3D observer = new DrawingMethods3D(position, theta, 0, image, 40);

		observer.updatePerspective(form);
		
		observer.renderDirectly(form);

		long startTime = System.nanoTime();
		image.display();
		long endTime = System.nanoTime();
		System.out.println((endTime - startTime) / 1000000);
		
		image.clear();

		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}



	}}

}
