package classes2D;
import java.util.ArrayList;

import functionalInterfaces.RealFunction;
import interfaces.NormedVectorSpace;
import other.MiscFunctions;
import rendering2D.Pixel;
import rendering2D.ShadeHandling;


public class R2Point implements NormedVectorSpace<R2Point> {

		private double x;
		private double y;
		
		private static ShadeHandling shadeHandling = new ShadeHandling();
		
		public R2Point(double x, double y) {

			this.x = x;
			this.y = y;
		}
		
		public R2Point() {

			x = 0;
			y = 0;
		}
		
		public R2Point(R2Point point) {

			x = point.x;
			y = point.y;
		}
		
		public double getX() {
			return x;
		}
		
		public double getY() {
			return y;
		}
		
		public void set(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public void set(R2Point point) {
			x = point.x;
			y = point.y;
		}
		
		public void translate(double x, double y) {

			this.x += x;
			this.y += y;
		}
		
		public static void adjustShades(RealFunction gauge) {
			shadeHandling.adjustShades(gauge);
		}

		public void translate(R2Point vector) {
			x += vector.x;
			y += vector.y;
		}
		
		public void scale(double factor) {
			x *= factor;
			y *= factor;
		}
		
		public R2Point sum(R2Point vector) {
			return new R2Point(x + vector.x, y + vector.y);
		}
		
		public R2Point difference(R2Point vector) {

			return new R2Point(x - vector.x, y - vector.y);
		}

		public void round() {

			x = Math.round(x);
			y = Math.round(y);
		}

		// Returns the area of the rectangle with corners this, point.
		public double areaRectangle(R2Point point) {
			return Math.abs((x - point.x) * (y - point.y));
		}
		
		private int shadeArea(R2Point point, int maxShade) {
			return shadeHandling.determineShade(areaRectangle(point), maxShade);
		}

		public Pixel truncate(int shade) {
			return new Pixel((int) x, (int) y, shade);
		}

		public IntPoint floor() {
			return new IntPoint((int) Math.floor((x)), (int) Math.floor((y)));
		}

		public IntPoint floor(double scale) {
			return new IntPoint((int) Math.floor((scale * x)), (int) Math.floor((scale * y)));
		}
		
		public IntPoint furthestRound(double scale) {
			return new IntPoint(MiscFunctions.furthestRound(scale * x), MiscFunctions.furthestRound(scale * y));
		}

		public Pixel[] approximate() {
			return view(ShadeHandling.MAX_SHADE);
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

		// Returns the slope between this and point
		public double slope(R2Point point) {
			return (y - point.y)/(x - point.x);
		}
		
		public static void add(ArrayList<Pixel> points, R2Point point, int maxShade) {
			for (int j = 0; j < 4; j++) {
				points.add(point.view(maxShade)[j]);
			}
		}
		public String toString() {
			return "(" + x + "," + y + ")";
		}

		public double dot(R2Point vector) {
			return vector.x * x + vector.y * y;
		}

		public void setRight(double right) {
			this.x = right;
		}

		public void setDown(double down) {
			this.y = down;
		}
		
		public double chebyshev() {
			return Math.max(Math.abs(x), Math.abs(y));
		}
		
		public double taxicab() {
			return Math.abs(x) + Math.abs(y);
		}

		public double lSquared() {
			// a little bit more efficient
			return Math.fma(x, x, y * y);
		}

	}
