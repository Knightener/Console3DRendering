package rendering3D;

import classes2D.*;
import classes3D.R3Point;
import rendering2D.*;

import java.util.*;

public class test {

	public static void main(String args[]) {

		int[][] shadeArr = new int[101][121];
		


		DrawingMethods2D image = new DrawingMethods2D(-60,60, -50, 50);
		R2Point.adjustShades(x->x*x);
		
		R3Point position = new R3Point(0,0,0);
		
		R3Point p000 = new R3Point(1,1,1);
		R3Point p001 = new R3Point(1, 1, -1);
		R3Point p010 = new R3Point(1,-1,1);
		R3Point p011 = new R3Point(1,-1,-1);
		R3Point p100 = new R3Point(-1,1,1);
		R3Point p101 = new R3Point(-1, 1, -1);
		R3Point p110 = new R3Point(-1,-1,1);
		R3Point p111 = new R3Point(-1,-1,-1);
		
		double scale = 1;
		
		p000.scale(scale);
		p001.scale(scale);
		p010.scale(scale);
		p011.scale(scale);
		p100.scale(scale);
		p101.scale(scale);
		p110.scale(scale);
		p111.scale(scale);
		
		double theta = 1.2;
		while (true) {
		DrawingMethods3D observer = new DrawingMethods3D(position,theta,0,image, 20);
		
		Figure figure = new Figure();
		
		figure.add(observer.line(p000,p001));
		figure.add(observer.line(p010,p011));
		figure.add(observer.line(p100,p101));
		figure.add(observer.line(p110,p111));
		
		figure.add(observer.line(p000,p010));
		figure.add(observer.line(p001,p011));
		figure.add(observer.line(p100,p110));
		figure.add(observer.line(p101,p111));
		
		figure.add(observer.line(p000,p100));
		figure.add(observer.line(p001,p101));
		figure.add(observer.line(p010,p110));
		figure.add(observer.line(p011,p111));
	
		image.draw(figure);
		image.displayCoordinates();

	
		image.clear();
		theta += 0.1;
		try
		{
		    Thread.sleep(100);
		}
		catch (Exception e) {
			
		}
		}

	}
}
