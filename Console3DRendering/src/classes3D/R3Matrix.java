package classes3D;

public class R3Matrix {

	/*
	 * Written down explicitly for convenience. Doesn't change much as opposed to
	 * writing it as a 2 dimensional array
	 */
	private double a11;
	private double a12;
	private double a13;

	private double a21;
	private double a22;
	private double a23;

	private double a31;
	private double a32;
	private double a33;

	public R3Matrix(R3Point v1, R3Point v2, R3Point v3) {

		a11 = v1.getRight();
		a21 = v1.getDown();
		a31 = v1.getForward();

		a12 = v2.getRight();
		a22 = v2.getDown();
		a32 = v2.getForward();
		
		a13 = v3.getRight();
		a23 = v3.getDown();
		a33 = v3.getForward();
	}
	

	public R3Point transform(R3Point point) {
		double x = point.getRight();
		double y = point.getDown();
		double z = point.getForward();

		return new R3Point(
			x * a11 + y * a12 + z * a13, 
			x * a21 + y * a22 + z * a23, 
			x * a31 + y * a32 + z * a33);
	}
	
	public void updateTransform(R3Point point) {
		double x = point.getRight();
		double y = point.getDown();
		double z = point.getForward();

		point.setRight(x * a11 + y * a12 + z * a13);
		point.setDown(x * a21 + y * a22 + z * a23);
		point.setForward(x * a31 + y * a32 + z * a33);
	}

	@Override
	public String toString() {
		return a11 + " " + a12 + " " + a13 + "\n" + a21 + " " + a22 + " " + a23 + "\n" + a31 + " " + a32 + " " + a33;
	}


	public R3Matrix(double a11, double a12, double a13, double a21, double a22, double a23, double a31, double a32,
			double a33) {
		
		this.a11 = a11;
		this.a12 = a12;
		this.a13 = a13;
		this.a21 = a21;
		this.a22 = a22;
		this.a23 = a23;
		this.a31 = a31;
		this.a32 = a32;
		this.a33 = a33;
	}
	
	
}
