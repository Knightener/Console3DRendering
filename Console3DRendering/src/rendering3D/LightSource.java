package rendering3D;

import java.util.HashMap;
import java.util.Map;

import classes3D.R3Point;

public class LightSource {
	R3Point lightSource;

	double intensity = 1;
	/*
	 * (Polygon normal) DOT (vector from any point of polygon to lightSource)
	 * Independent of the chosen point of the polygon. Stored for more efficient
	 * lighting calculations.
	 */
	Map<RelativePolygon, Double> dot = new HashMap<RelativePolygon, Double>();

	public LightSource(R3Point lightSource, double intensity) {
		this.lightSource = lightSource;
		this.intensity = intensity;
	}

	public LightSource(double right, double down, double forward, double intensity) {
		this(new R3Point(right, down, forward), intensity);
	}

	public void setIntensity(double intensity) {
		this.intensity = intensity;
	}

	public void addPolygon(RelativePolygon polygon) {
		dot.put(polygon, polygon.differenceDotNormal(lightSource));
	}

	// Returns true if the polygon is facing the light source. 
	public boolean isFacing(RelativePolygon polygon) {
		return dot(polygon) > 0;
	}
 	
	public double dot(RelativePolygon polygon) {
		try {
			return dot.get(polygon);
		} catch (NullPointerException e) {
			addPolygon(polygon);
			return dot.get(polygon);
		}
	}
	
	public R3Point getPosition() {
		return lightSource;
	}
}
