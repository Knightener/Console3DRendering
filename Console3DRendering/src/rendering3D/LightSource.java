package rendering3D;

import java.util.Map;

import classes3D.R3Point;

public class LightSource {
	R3Point lightSource; 
	
	/*
	 * (Polygon normal) DOT (vector from any point of polygon to lightSource)
	 * Independent of the chosen point of the polygon. Stored for more efficient
	 * lighting calculations.
	 */
	Map<RelativePolygon, Double> dot;

	public void addPolygon(RelativePolygon polygon) {
		dot.put(polygon, polygon.differenceDotNormal(lightSource));
	}
	
	public double dot(RelativePolygon polygon) {
		try {
			return dot.get(polygon);
		} catch (NullPointerException e) {
			addPolygon(polygon);
			return dot.get(polygon);
		}
	}
}
