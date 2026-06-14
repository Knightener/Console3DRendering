package shading;

import java.util.Map;

import classes3D.R3Point;
import rendering3D.RelativePolygon;

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
}
