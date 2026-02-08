package texturing;

import java.util.List;

import classes2D.CardinalConvexRegion;
import classes2D.IntPoint;
import classes2D.R2Point;

public class PolygonTexture {

	/*
	 * This class provides a more efficient way of storing information about a
	 * textured convex polygon.
	 */

	private CardinalConvexRegion polygon;
	
	private double scaleFactor; 
	
	public PolygonTexture(List<R2Point> vertices, Texture texture) {
		
		scaleFactor = texture.getScaleFactor();

		List<IntPoint> flooredScaledVertices = vertices.stream().map(x -> x.floor(scaleFactor)).toList();

		polygon = CardinalConvexRegion.polygon(flooredScaledVertices);

		// Adds a margin to account for errors that come from rounding.
		polygon.extendRows(-1);
		polygon.extendRows(1);
		polygon.extendColumns(-1);
		polygon.extendColumns(1);


		polygon.shade((x, y) -> texture.determineShadeAt(x, y));

	}
	
	public int determineShadeAt(int right, int down) {
		try {
		return polygon.getShade(right, down);
		} catch (Exception e) {
			return 2;
		}
	}
	
	public int determineShadeAt(R2Point point) {
		return determineShadeAt((int) Math.floor(scaleFactor*point.getRight()), (int) Math.floor(scaleFactor*point.getDown()));
	}

}
