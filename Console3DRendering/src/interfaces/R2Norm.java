package interfaces;
import classes2D.*;
@FunctionalInterface
public interface R2Norm {
	double n(R2Point point);
	
	public static final R2Norm CHEBYSHEV = point -> Math.max(Math.abs(point.getRight()), 
		Math.abs(point.getDown()));

}
