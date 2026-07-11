package texturing;

import java.util.List;

import classes2D.IntPoint;
import classes2D.R2Point;
import classes2D.VCRegionBuilder;
import classes2D.VerticallyConvexRegion;

public class PolygonTexture {

	/*
	 * This class provides a more efficient way of storing information about a
	 * textured convex polygon.
	 */

	private VerticallyConvexRegion polygonTexture;

	private double scaleFactor;

	public PolygonTexture(List<R2Point> vertices, Texture texture) {

		scaleFactor = texture.getScaleFactor();

		List<IntPoint> flooredScaledVertices = vertices.stream().map(x -> x.furthestRound(scaleFactor))
			.toList();

		VCRegionBuilder polygonBuilder = VCRegionBuilder.polygon(flooredScaledVertices);

		// Adds a margin to account for errors that come from rounding.
		polygonBuilder.extendRows(-1);
		polygonBuilder.extendRows(1);
		polygonBuilder.extendColumns(-1);
		polygonBuilder.extendColumns(1);

		polygonBuilder.shade((x, y) -> texture.determineShadeAt(x, y));

		polygonTexture = new VerticallyConvexRegion(polygonBuilder);

	}

	public int determineShadeAt(int right, int down) {
		try {
			return polygonTexture.getShade(right, down);
		} catch (Exception e) {
			return -1;
		}
	}

	public int determineShadeAt(double right, double down) {
		try {
			return polygonTexture.getShade((int) Math.floor(scaleFactor * right),
				(int) Math.floor(scaleFactor * down));
		} catch (Exception e) {
			return -1;
		}
	}

	public int determineShadeAt(R2Point point) {
		return determineShadeAt(point.getX(), point.getY());
	}

}
