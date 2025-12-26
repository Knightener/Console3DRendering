package functionalInterfaces;

import classes2D.R2Point;

@FunctionalInterface
public interface R2Metric {
	double d(R2Point p1, R2Point p2);
	
	public static final R2Metric TAXICAB = (p1,p2) -> Math.abs(p1.getRight() - p2.getRight()) 
		+ Math.abs(p1.getDown() - p2.getDown());
	
	public static final R2Metric EUCLIDIAN = (p1,p2) -> Math.hypot(p1.getRight() - p2.getRight(), 
		p1.getDown() - p2.getDown());
		
	public static final R2Metric CHEBYSHEV = (p1,p2) -> Math.max(Math.abs(p1.getRight() - p2.getRight()), 
			Math.abs(p1.getDown() - p2.getDown()));
}
