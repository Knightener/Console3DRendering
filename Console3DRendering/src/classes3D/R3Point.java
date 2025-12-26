package classes3D;

import classes2D.*;
import functionalInterfaces.*;
import other.Constants;

public class R3Point {

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

	public R2Point project(double fov) {
		if (forward > 0) {
			
			double ratio = fov / forward;
			
			R2Point point = new R2Point(right,down);
			point.scale(ratio);
			
			return point;
		}
		return null;
	}

	/*
	 * If forward is zero, a direction vector pointing towards this is returned. This
	 * helps with drawing lines.
	 */
	public R2Point projectAlt(double fov) {
		if (forward > 0) {
			double ratio = fov / forward;
			
			R2Point point = new R2Point(right,down);
			point.scale(ratio);
			
			return point;
		}
		if ((right != 0 || down != 0) && -Constants.EPSILON < forward && forward < Constants.EPSILON) {
			R2Point point = new R2Point(right, down);
			
			point.normalize(R2Norm.CHEBYSHEV);

			return point;
		} else {
			return null;
		}
	}

	public R3Point difference(R3Point point) {
		return new R3Point(right - point.right, down - point.down, forward - point.forward);
	}
	
	public void scale(double r) {
		right *= r;
		down *= r;
		forward *= r;
	}
	
	public void translate(R3Point point) {
		right += point.right;
		down += point.down;
		forward += point.forward;
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
	
	public String toString() {
		return "(" + right + "," + down + "," + forward + ")";
	}

	public void normalize(R3Norm norm) {
		scale(1 / norm.n(this));
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
	

	public R3Point(R3Point point) {
		right = point.right;
		down = point.down;
		forward = point.forward;
	}
}
