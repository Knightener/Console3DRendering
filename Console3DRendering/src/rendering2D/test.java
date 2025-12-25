package rendering2D;
import interfaces.*;

import java.util.*;

import classes2D.R2Point;
import classes3D.*;
public class test {

	public static void main(String[] args) {
		int[][] shadeArr = new int[100][120];
		R2Point.adjustShades(x -> x*x);

		ArrayList<Integer> test = new ArrayList<Integer>();
		test.add(1);
		test.add(4);
		test.add(3);
		
		System.out.println(test);
		Collections.sort(test);
		System.out.println(test);
		DrawingMethods2D m = new DrawingMethods2D(-20,20,-20,20);
		DrawingMethods2D m2 = new DrawingMethods2D(-60,60,-50,50);
		
		Image image2 = new Image(-10,30,-30,0);
		R2Point p1 = new R2Point(-30,-40);
		R2Point p2 = new R2Point(10,40);
		R2Point p3 = new R2Point(-20,-10);
		
		Pixel pp1 = new Pixel (-30,-40,6);
		Pixel pp2 = new Pixel(20, -20, 6);
		Pixel pp3 = new Pixel(50, 30, 6);

//		Pixel q1 = new Pixel (-30,-40,2);
//		Pixel q2 = new Pixel(20, -20, 2);
//		Pixel q3 = new Pixel(50, 30, 2);
//		ArrayList<Pixel> points = new ArrayList<Pixel>();
//		int maxShade = 8;
//
//		m2.draw(m2.jaggedTriangle(pp2, pp2, pp3));
		
		m2.draw(m2.graph(x->Math.pow(x/20, 1/3)*20, -60, 60, 100));
		m2.displayCoordinates();

		m.highlightVertical(30);
		m.highlightVertical(-10);
		
		

	}

}
