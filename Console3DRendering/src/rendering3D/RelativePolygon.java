package rendering3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes2D.R2Point;
import classes3D.R3Point;
import other.Constants;
import texturing.PolygonTexture;
import texturing.Texture;

public class RelativePolygon extends RelativeComponent {

	PolygonTexture texture;

	/*
	 * Stores the points using two variables u and v representing their position on
	 * the plane. Rotation and translation invariant
	 */
	protected List<R2Point> uVPoints;

	// Stores the vertices of the polygon as viewed by the observer.
	protected List<R3Point> perceivedPoints;

	// Represents the orientation of the actual polygon.
	protected R3Point orientation;

	// Represents the offset of the plane the polygon lies on from the origin
	protected R3Point offset;

	// Vectors that specify the directions of the plane. Forms an orthogonal basis with orientation.
	protected R3Point vectorA;
	protected R3Point vectorB;

	protected R3Point perceivedOffset;
	protected R3Point perceivedVectorA;
	protected R3Point perceivedVectorB;

	/*
	 * If false, rendering will not be affected by orientation. If true, polygons
	 * facing away from a light source will be rendered all black.
	 */
	boolean shadeOrientation = false;
	
	/*
	 * vA/BFis perceivedVectorA/B's forward component multiplied by the FOV of whatever
	 * observer is associated with this polygon. 
	 * 
	 * vABDotOffset is (-percievedVectorA dot perceivedOffset,-percievedVectorB dot perceivedOffset).
	 * 
	 * These variables are stored to cut y operations for the findUV method.
	 */
	private double vAF;
	private double vBF;
	private R2Point vABDotOffset;

	/*
	 * Polygon assumed to lie entirely on some plane. Doesn't necessarily need to be
	 * convex. In fact, the polygon can even intersect itself, but I would not
	 * recommend doing this as it would likely not work very well with shading. Set
	 * texture to null to obtain an untextured polygon.
	 */
	public RelativePolygon(Observer observer, Texture texture, List<R3Point> points) {
		super(observer, texture != null); 
		
		if (points.size() < 3) {
			throw new IllegalArgumentException("Polygon must have at least 3 points");
		}
		
		this.perceivedPoints = new ArrayList<R3Point>(points);
		
		offset = points.get(0);
		
		vectorA = points.get(1).difference(offset);
		
		// (A-B)x(B-C)
		orientation = points.get(0).difference(points.get(1)).cross(points.get(1).difference(points.get(2)));

		try {
			orientation.normalize();
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException("Polygon cannot have edge overlap");
		}

		try {
			vectorA = orientation.cross(new R3Point(0, -1, 0));
			vectorA.normalize();
			vectorB = vectorA.cross(orientation);
			
		} catch (ArithmeticException e) {
			// Code only gets here if the polygon is orthogonal to the vertical axis
			
			vectorA = new R3Point(1,0,0);
			vectorB = new R3Point(0,0,1);
		}

		uVPoints = new ArrayList<R2Point>();

		for (int i = 0; i < points.size(); i++) {

			R3Point adjusted = points.get(i).difference(offset);

			// Checks if all points lie on the same plane
			if (adjusted.dot(orientation) > Constants.EPSILON) {
				throw new IllegalArgumentException("All points must lie in one plane");
			}
			uVPoints.add(new R2Point(adjusted.dot(vectorA), adjusted.dot(vectorB)));

		}
		
		if ((getID() & 1) == 1) {
			this.texture = new PolygonTexture(uVPoints, texture);
		}
		
		updatePerspective();


	}

	public RelativePolygon(Observer observer, Texture texture, R3Point... points) {
		this(observer, texture, Arrays.asList(points));
	}

	public void findUVVariables() {

		vAF = perceivedVectorA.getZ() * observer.getFov();
		vBF = perceivedVectorB.getZ() * observer.getFov();

		vABDotOffset = new R2Point(-perceivedVectorA.dot(perceivedOffset),
			-perceivedVectorB.dot(perceivedOffset));

	}

	public void determineMostAndLeastForward() {

		mostForward = perceivedPoints.get(0).getZ();
		leastForward = mostForward;

		double currForward;

		for (int i = 1; i < perceivedPoints.size(); i++) {

			currForward = perceivedPoints.get(i).getZ();

			if (currForward > mostForward) {
				mostForward = currForward;
			} else if (currForward < leastForward) {
				leastForward = currForward;
			}
		}
	}

	// Updates the 3 vectors that determine the plane.
	public void updatePlaneVectors() {		
		perceivedVectorA = observer.rotate(vectorA);
		perceivedVectorB = observer.rotate(vectorB);
		perceivedOffset = observer.perspective(offset);
	}
	
	public void updatePerspective() {

		updatePlaneVectors();

		R2Point curr;

		for (int i = 0; i < uVPoints.size(); i++) {
			curr = uVPoints.get(i);

			perceivedPoints.set(i,
				R3Point.linearCombination(curr.getX(), curr.getY(), perceivedVectorA, perceivedVectorB));
		}

		for (R3Point point : perceivedPoints) {
			point.translate(perceivedOffset);
		}

		if  ((getID() & 1) == 1) {
			findUVVariables();
		}
	}

	/*
	 * Given a pixel that is assumed to be projected by a given observer and comes
	 * from this polygon, finds (an approximation of) the coordinates of this point
	 * in u-v space.
	 */
	public R2Point findUV(int x, int y, double zBuffer) {
		checkTextured();
		R2Point uvPoint = new R2Point(
			perceivedVectorA.getX() * x + perceivedVectorA.getY() * y + vAF,
			perceivedVectorB.getX() * x + perceivedVectorB.getY() * y + vBF);

		uvPoint.scale(1 / zBuffer);

		uvPoint.translate(vABDotOffset);

		return uvPoint;
	}

	// Avoids object creation overhead. Equivalent to texture.determineShadeAt(findUV)
	public int determineShade(int x, int y, double zBuffer) {
		return texture.determineShadeAt(
			(perceivedVectorA.getX() * x + perceivedVectorA.getY() * y + vAF)
				/ zBuffer + vABDotOffset.getX(),
			(perceivedVectorB.getX() * x + perceivedVectorB.getY() * y + vBF)
				/ zBuffer + vABDotOffset.getY());
	}

	/*
	 * Finds the texture of the given point and calculates the new shade taking into
	 * account the given light source. Fall off proportional to 1/distance
	 */
	public int determineShade(int x, int y, double zBuffer, LightSource lightSource) {		
		double dot = lightSource.dot(this);
		
		if (shadeOrientation && dot < 0) {
			return 0;
		}
		
		// findUV.getX
		double u = (perceivedVectorA.getX() * x + perceivedVectorA.getY() * y
			+ vAF) / zBuffer + vABDotOffset.getX();

		// findUV.getY
		double v = (perceivedVectorB.getX() * x + perceivedVectorB.getY() * y
			+ vBF) / zBuffer + vABDotOffset.getY();
		
		/*
		 * Coordinates of the vector from the traced back point (in 3d) to the light
		 * source. Equivalent to (lightSource - u*vectorA - v*vectorB).
		 */
		double diffX = lightSource.lightSource.getX() - u * vectorA.getX()
			- v * vectorB.getX() - offset.getX();
		double diffY = lightSource.lightSource.getY() - u * vectorA.getY()
			- v * vectorB.getY() - offset.getY();
		double diffZ = lightSource.lightSource.getZ() - u * vectorA.getZ()
			- v * vectorB.getZ() - offset.getZ();

		/*
		 * If a is the difference vector from the traced back point to the light source,
		 * then the brightness is equivalent to intensity * (a DOT orientation) / (a DOT
		 * a), clamped to be between 0 and 1. Math.fma used for a little extra
		 * efficiency.
		 */
		double brightness = Math.min(Math.abs(lightSource.intensity * dot / (Math
			.fma(diffX, diffX, Math.fma(diffY, diffY, diffZ * diffZ)))), 1);

		return (int) (texture.determineShadeAt(u, v) * brightness);
	}

	public void render() {
		observer.polygon(perceivedPoints, 2, ID, false, false);
	}
	
	public void writeToStencil() {
		observer.polygon(perceivedPoints, 2, ID, true, isFacingObserver());
	}

	// Returns the outward pointing unit normal vector of the triangle.
	public RelativeLine getUnitNormal() {

		// Arithmetic center of the polygon
		R2Point uVVectorTail = new R2Point();

		for (R2Point point : uVPoints) {
			uVVectorTail.translate(point);
		}

		uVVectorTail.scale(1 / (double) uVPoints.size());

		R3Point vectorTail = R3Point.linearCombination(
			uVVectorTail.getX(), uVVectorTail.getY(), vectorA, vectorB);
		
		vectorTail.translate(offset);
		
		R3Point vectorTip = new R3Point(vectorTail);

		vectorTip.translate(orientation);

		return new RelativeLine(vectorTail, vectorTip, observer);
	}
	
	// Returns true if the polygon is facing the point.
	public boolean isFacing(R3Point point) {
		return differenceDotNormal(point) > Constants.EPSILON;
	}

	/*
	 * (point - any point of the polygon) DOT (orientation). Independent of choice.
	 * Written out directly to avoid object creation overhead. 
	 */
	public double differenceDotNormal(R3Point point) {
		return (point.getX() - offset.getX()) * orientation.getX()
			+ (point.getY() - offset.getY()) * orientation.getY()
			+ (point.getZ() - offset.getZ()) * orientation.getZ();
	}
	
	
	public boolean isFacingObserver() {
		return isFacing(observer.position);
	}
	
	private void checkTextured() {
		if  ((getID() & 1) == 0) {
			throw new IllegalStateException("Polygon is untextured");
		}
	}
	
	// Reverses the orientation of the polygon. 
	public void invertOrientation() {
		
		// vectorB is fixed because of the double negative
		orientation.scale(-1);
		
		uVPoints = uVPoints.reversed();
		perceivedPoints = perceivedPoints.reversed();
	}
	
	public void toggleShadeOrientation() {
		shadeOrientation = !shadeOrientation;
	}
}
