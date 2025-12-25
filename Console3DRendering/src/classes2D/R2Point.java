package classes2D;
import interfaces.*;
import rendering2D.Pixel;
import rendering2D.ShadeHandling;

import java.util.*;


public class R2Point {

		private double right;
		private double down;
		
		private static ShadeHandling shadeHandling = new ShadeHandling();
		
		public R2Point(double right, double down) {

			this.right = right;
			this.down = down;
		}
		
		public R2Point() {

			right = 0;
			down = 0;
		}
		
		public R2Point(R2Point point) {

			right = point.right;
			down = point.down;
		}
		
		public double getRight() {
			return right;
		}
		
		public void assign(double right, double down) {
			this.right = right;
			this.down = down;
		}

		public void assign(R2Point point) {
			right = point.right;
			down = point.down;
		}
		
		public double getDown() {
			return down;
		}
		
		public void translate(double x, double y) {

			right += x;
			down += y;
		}
		
		public static void adjustShades(RealFunction gauge) {
			shadeHandling.adjustShades(gauge);
		}

		public void translate(R2Point vector) {

			right += vector.right;
			down += vector.down;
		}
		
		public void scale(double r) {

			right *= r;
			down *= r;
		}
		

		public R2Point difference(R2Point point) {

			return new R2Point(right - point.right, down - point.down);
		}

		public void round() {

			right = Math.round(right);
			down = Math.round(down);
		}

		// Returns the area of the rectangle with corners this, point.
		public double areaRectangle(R2Point point) {
			return Math.abs((right - point.right) * (down - point.down));
		}
		
		private int shadeArea(R2Point point, int maxShade) {
			return shadeHandling.determineShade(areaRectangle(point), maxShade);
		}
		
		public Pixel truncate(int shade) {
			return new Pixel((int)right,(int)down,shade);
		}
		
		public Pixel[] approximate() {
			return view(ShadeHandling.getMaxPossibleShade());
		}
		
		public Pixel[] view(int maxShade) {
			Pixel[] points = new Pixel[4];
			
			R2Point copy = new R2Point(this);  
			R2Point fixedQuarter = new R2Point(this);
			
			fixedQuarter.round();
			
			R2Point currentQuarter = new R2Point(fixedQuarter);
			
			copy.translate(0.5,0.5);
			
			points[0] = currentQuarter.truncate(fixedQuarter.shadeArea(copy, maxShade));
			copy.translate(-1,0);
			currentQuarter.translate(-1,0);
			points[1] = currentQuarter.truncate(fixedQuarter.shadeArea(copy, maxShade));
			copy.translate(0,-1);
			currentQuarter.translate(0,-1);
			points[2] = currentQuarter.truncate(fixedQuarter.shadeArea(copy, maxShade));
			copy.translate(1,0);
			currentQuarter.translate(1,0);
			points[3] = currentQuarter.truncate(fixedQuarter.shadeArea(copy, maxShade));
			
			return points;
		}
		
		public static void add(ArrayList<Pixel> points, R2Point point, int maxShade) {
			for (int j = 0; j < 4; j++) {
				points.add(point.view(maxShade)[j]);
			}
		}
		public String toString() {
			return "(" + right + "," + down + ")";
		}

		public double dot(R2Point vector) {
			return vector.right * right + vector.down * down;
		}
		
		public void normalize(R2Norm norm) {
			scale(1 / norm.n(this));
		}
		
		public R2Point add(R2Point point) {
			return new R2Point(right + point.right, down + point.down);
		}

		public void setRight(double right) {
			this.right = right;
		}

		public void setDown(double down) {
			this.down = down;
		}
		
		
	}
