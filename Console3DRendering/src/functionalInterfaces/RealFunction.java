package functionalInterfaces;

@FunctionalInterface
public interface RealFunction {
	double f(double x);

	public static final RealFunction SIGMOID = x -> x / (1 + Math.abs(x));
}
