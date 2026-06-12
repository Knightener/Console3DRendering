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
	private double right;
	private double down;
	private double forward;
	
	public R3Point(double right, double down, double forward) {
		this.right = right;
		this.down = down;
		this.forward = forward;
	}

	public R3Point() {
		right = 0;
		down = 0;
		forward = 0;
	}
	public R2Point project(double fov) {
		if (forward > 0) {
			
			double ratio = fov / forward;
			
			R2Point point = new R2Point(right,down);
			point.scale(ratio);
			
			return point;
		}
		return null;
	}
	
	public ZPixel project(double fov, int shade) {
		if (forward > 0) {
			
			double ratio = fov / forward;
			
			R2Point point = new R2Point(right,down);
			
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

	public void incrementRight(double delta) {
		right += delta;
	}
	public void incrementDown(double delta) {
		down += delta;
	}
	public void incrementForward(double delta) {
		forward += delta;
	}
	public R3Point sum(R3Point factor) {
		return new R3Point(right + factor.right, down + factor.down, forward + factor.forward);
	}
	
	public R3Point difference(R3Point factor) {
		return new R3Point(right - factor.right, down - factor.down, forward - factor.forward);
	}
	
	public void scale(double factor) {
		right *= factor;
		down *= factor;
		forward *= factor;
	}
	
	public void translate(R3Point vector) {
		right += vector.right;
		down += vector.down;
		forward += vector.forward;
	}
	
	public void translate(double right, double down, double forward) {
		this.right += right;
		this.down += down;
		this.forward += forward;
	}

	public R3Point cross(R3Point vector) {
		return new R3Point(down * vector.forward - forward * vector.down,
			forward * vector.right - right * vector.forward, right * vector.down - down * vector.right);
	}

	public double dot(R3Point vector) {
		return right * vector.right + down * vector.down + forward * vector.forward;
	}
	
	@Override
	public String toString() {
		return "(" + right + "," + down + "," + forward + ")";
	}

	/*
	 * Intersects the the line formed by this and point with the near plane. This is
	 * a commonly used expression in 3D rendering.
	 */
	public R3Point forward0Intersection(R3Point point) {

		double ratio = (forward - Constants.NEAR_EPSILON) / (forward - point.forward);

		/*
		 * This is very slightly off the actual intersection point, however, the
		 * difference is negligible in most cases
		 */
		return new R3Point(right + ratio * (point.right - right), down + ratio * (point.down - down),
				Constants.NEAR_EPSILON);

	}

	/*
	 * Returns the linear combination s*v1 + t*v2. This is one of those methods that
	 * can be generalized much further with relative ease, however, I don't see
	 * myself needing much more than a linear combination of two 3D vectors for this
	 * project.
	 */
	public static R3Point linearCombination(double s, double t, R3Point v1, R3Point v2) {
		return new R3Point(s * v1.right + t * v2.right, s * v1.down + t * v2.down, s * v1.forward + t * v2.forward);
	}
	
	public double getRight() {
		return right;
	}

	public double getDown() {
		return down;
	}

	public double getForward() {
		return forward;
	}

	public void setRight(double right) {
		this.right = right;
	}

	public void setDown(double down) {
		this.down = down;
	}

	public void setForward(double forward) {
		this.forward = forward;
	}

	public void set(R3Point point) {
		right = point.right;
		down = point.down;
		forward = point.forward;
	}

	public R3Point(R3Point point) {
		right = point.right;
		down = point.down;
		forward = point.forward;
	}

	public double chebyshev() {
		return Math.max(Math.max(Math.abs(right), Math.abs(down)), Math.abs(forward));
	}

	public double euclidian() {
		return Math.sqrt(dot(this));
	}

	public double taxicab() {
		return Math.abs(right) + Math.abs(down) + Math.abs(forward);
	}

	// Returns a new point that represent this scaled by multiplier from origin.
	public R3Point extendFrom(R3Point origin, double multiplier) {
		return new R3Point(
				multiplier * (right - origin.right) + origin.right,
				multiplier * (down - origin.down) + origin.down,
				multiplier * (forward - origin.forward) + origin.forward);
	}
}
