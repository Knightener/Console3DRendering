package classes3D;

import functionalInterfaces.R3Norm;
import rendering2D.ShadeHandling;

public class LightSource {
	
	private static ShadeHandling shadeHandling = new ShadeHandling();
	
	public R3Point direction;

	// Creates a light source using the standard parameterization of a sphere.
	public LightSource(double theta, double phi) {

		direction.setRight(Math.sin(theta) * Math.cos(phi));
		direction.setDown(Math.sin(theta) * Math.sin(phi));
		direction.setForward(Math.cos(theta));
	}

	// Creates a light source in the direction of the given vector.
	public LightSource(R3Point vector) {

		direction = new R3Point(vector);
		
		direction.normalize(R3Norm.EUCLIDIAN);
	}

	/*
	 * If the orientation of the polygon is directly facing the light source, 1 is
	 * returned to represent full brightness.
	 * 
	 * If the orientation of the polygon isn't facing the light source (light is
	 * hitting the back of the polygon) 0 is returned to represent no light.
	 * 
	 * If the orientation of the polygon is facing the light source but not directly,
	 * a number somewhere between 0 and 1 is returned.
	 */
	public double alignment(Triangle triangle) {
		
		double alignment = triangle.getOrientation().dot(direction);

		if (alignment > 0) {
			return 0;
		}

		return -alignment;
	}

	public int shade(Triangle triangle) {
		return shadeHandling.determineShade(alignment(triangle));
	}
	

}

