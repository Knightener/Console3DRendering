package functionalInterfaces;

@FunctionalInterface
public interface RealFunction {
	double f(double x);

	public static RealFunction sigmoid(double steepness) {

		RealFunction f = x -> {

			double adjusted = x * steepness;

			return adjusted / (1 + Math.abs(adjusted));
			
		};

		return f;

	}
}
