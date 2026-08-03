package classes3D;

public class R3Matrix {

	/*
	 * Written y explicitly for convenience. Doesn't change much as opposed to
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

		a11 = v1.getX();
		a21 = v1.getY();
		a31 = v1.getZ();

		a12 = v2.getX();
		a22 = v2.getY();
		a32 = v2.getZ();
		
		a13 = v3.getX();
		a23 = v3.getY();
		a33 = v3.getZ();
	}
	

	public R3Point transform(R3Point point) {
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();

		return new R3Point(
			x * a11 + y * a12 + z * a13, 
			x * a21 + y * a22 + z * a23, 
			x * a31 + y * a32 + z * a33);
	}
	
	public void updateTransform(R3Point point) {
		double x = point.getX();
		double y = point.getY();
		double z = point.getZ();

		point.setX(x * a11 + y * a12 + z * a13);
		point.setY(x * a21 + y * a22 + z * a23);
		point.setZ(x * a31 + y * a32 + z * a33);
	}

	public void set(double a11, double a12, double a13, double a21, double a22, double a23,
		double a31, double a32, double a33) {

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
	
	public double getA11() {
		return a11;
	}

	public double getA12() {
		return a12;
	}

	public double getA13() {
		return a13;
	}

	public double getA21() {
		return a21;
	}

	public double getA22() {
		return a22;
	}

	public double getA23() {
		return a23;
	}

	public double getA31() {
		return a31;
	}

	public double getA32() {
		return a32;
	}

	public double getA33() {
		return a33;
	}
	
	
}
