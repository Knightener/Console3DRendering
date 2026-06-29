package classes3D;

import classes2D.R2Point;
import interfaces.NormedVectorSpace;
import other.Constants;
import zBuffered2DRendering.ZPixel;


public class R3Point implements NormedVectorSpace<R3Point>{

	/*
	 * This project uses points and vectors interchangeably. The difference is
	 * subtle, but irrelevant for what this project aims to deal with.
	 */
	private double x;
	private double y;
	private double z;
	
	public R3Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public R3Point() {
		x = 0;
		y = 0;
		z = 0;
	}
	public R2Point project(double fov) {
		if (z > 0) {
			
			double ratio = fov / z;
			
			R2Point point = new R2Point(x,y);
			point.scale(ratio);
			
			return point;
		}
		return null;
	}
	
	public ZPixel project(double fov, int shade) {
		if (z > 0) {
			
			double ratio = fov / z;
			
			R2Point point = new R2Point(x,y);
			
			point.scale(ratio);
			
			ZPixel pixel = new ZPixel(point.truncate(shade), ratio);
			
			return pixel;
		}
		return null;
	}
	
	public ZPixel project(double fov, int shade, int polygonID) {
		ZPixel pixel = project(fov, shade);
		pixel.setRenderInfo(polygonID);
		return pixel;
	}

	public void incrementX(double delta) {
		x += delta;
	}
	public void incrementY(double delta) {
		y += delta;
	}
	public void incrementZ(double delta) {
		z += delta;
	}
	public R3Point sum(R3Point factor) {
		return new R3Point(x + factor.x, y + factor.y, z + factor.z);
	}
	
	public R3Point difference(R3Point factor) {
		return new R3Point(x - factor.x, y - factor.y, z - factor.z);
	}
	
	public void scale(double factor) {
		x *= factor;
		y *= factor;
		z *= factor;
	}
	
	public void translate(R3Point vector) {
		x += vector.x;
		y += vector.y;
		z += vector.z;
	}
	
	public void translate(double right, double down, double forward) {
		this.x += right;
		this.y += down;
		this.z += forward;
	}

	public R3Point cross(R3Point vector) {
		return new R3Point(y * vector.z - z * vector.y,
			z * vector.x - x * vector.z, x * vector.y - y * vector.x);
	}

	public double dot(R3Point vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}
	
	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ")";
	}

	/*
	 * Intersects the the line formed by this and point with the near plane. This is
	 * a commonly used expression in 3D rendering.
	 */
	public R3Point nearPlaneIntersection(R3Point point) {

		double ratio = (z - Constants.NEAR_EPSILON) / (z - point.z);

		/*
		 * This is very slightly off the actual intersection point, however, the
		 * difference is negligible in most cases
		 */
		return new R3Point(x + ratio * (point.x - x), y + ratio * (point.y - y),
				Constants.NEAR_EPSILON);

	}

	/*
	 * Returns the linear combination s*v1 + t*v2. This is one of those methods that
	 * can be generalized much further with relative ease, however, I don't see
	 * myself needing much more than a linear combination of two 3D vectors for this
	 * project.
	 */
	public static R3Point linearCombination(double s, double t, R3Point v1, R3Point v2) {
		return new R3Point(s * v1.x + t * v2.x, s * v1.y + t * v2.y, s * v1.z + t * v2.z);
	}
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void set(R3Point point) {
		x = point.x;
		y = point.y;
		z = point.z;
	}

	public R3Point(R3Point point) {
		x = point.x;
		y = point.y;
		z = point.z;
	}

	public double chebyshev() {
		return Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
	}


	public double taxicab() {
		return Math.abs(x) + Math.abs(y) + Math.abs(z);
	}

	public double lSquared() {
		// a little bit more efficient.
		return Math.fma(x, x, Math.fma(y, y, z * z));
	}

	// Returns a new point that represent this scaled by multiplier from origin.
	public R3Point extendFrom(R3Point origin, double multiplier) {
		return new R3Point(multiplier * (x - origin.x) + origin.x,
			multiplier * (y - origin.y) + origin.y,
			multiplier * (z - origin.z) + origin.z);
	}

}


