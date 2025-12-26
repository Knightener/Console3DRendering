package functionalInterfaces;

import classes3D.R3Point;

@FunctionalInterface
public interface R3Norm {

	double n(R3Point point);
	
	public static final R3Norm CHEBYSHEV = point -> Math
		.max(Math.max(Math.abs(point.getRight()), Math.abs(point.getDown())), Math.abs(point.getForward()));

	public static final R3Norm EUCLIDIAN = point -> Math.sqrt(point.dot(point));

}
