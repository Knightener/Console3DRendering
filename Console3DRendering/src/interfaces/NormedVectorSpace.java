package interfaces;

import other.Constants;

public interface NormedVectorSpace<T> extends VectorSpace<T> {

	public double chebyshev();

	public double euclidian();

	public double taxicab();

	public default double chebyshev(T point) {
		return chebyshev(difference(point));
	}

	public default double euclidian(T point) {
		return euclidian(difference(point));
	}

	public default double taxicab(T point) {
		return taxicab(difference(point));
	}

	public default void normalize() {
		
		double length = euclidian();
		
		if (length == 0) {
			throw new ArithmeticException("Vector cannot be 0");
		}
		
		scale(1 / length);
	}
	
	public default boolean nearlyEquals(T point) {
		return chebyshev(point) < Constants.EPSILON;
	}
}
