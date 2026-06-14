package rendering3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes2D.R2Point;
import classes3D.R3Point;
import other.Constants;
import rendering2D.ShadeHandling;
import texturing.PolygonTexture;
import texturing.Texture;

public class RelativePolygon extends RelativeComponent {

	PolygonTexture texture;

	/*
	 * Stores the points using two variables u and v representing their position on
	 * the plane. Rotation and translation invariant
	 */
	protected ArrayList<R2Point> uVPoints;

	// Stores the vertices of the polygon as viewed by the observer.
	protected ArrayList<R3Point> perceivedPoints;

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
	 * vA/BFis perceivedVectorA/B's forward component multiplied by the FOV of whatever
	 * observer is associated with this polygon. 
	 * 
	 * vABDotOffset is (-percievedVectorA dot perceivedOffset,-percievedVectorB dot perceivedOffset).
	 * 
	 * These variables are stored to cut down operations for the findUV method.
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

		vAF = perceivedVectorA.getForward() * observer.getFov();
		vBF = perceivedVectorB.getForward() * observer.getFov();

		vABDotOffset = new R2Point(-perceivedVectorA.dot(perceivedOffset),
			-perceivedVectorB.dot(perceivedOffset));

	}

	public void determineMostAndLeastForward() {

		mostForward = perceivedPoints.get(0).getForward();
		leastForward = mostForward;

		double currForward;

		for (int i = 1; i < perceivedPoints.size(); i++) {

			currForward = perceivedPoints.get(i).getForward();

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
				R3Point.linearCombination(curr.getRight(), curr.getDown(), perceivedVectorA, perceivedVectorB));
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
	public R2Point findUV(int right, int down, double zBuffer) {
		checkTextured();
		R2Point uvPoint = new R2Point(
			perceivedVectorA.getRight() * right + perceivedVectorA.getDown() * down + vAF,
			perceivedVectorB.getRight() * right + perceivedVectorB.getDown() * down + vBF);

		uvPoint.scale(1 / zBuffer);

		uvPoint.translate(vABDotOffset);

		return uvPoint;
	}

	// Avoids object creation overhead. Equivalent to texture.determineShadeAt(findUV)
	public int determineShade(int right, int down, double zBuffer) {
		return texture.determineShadeAt(
			(perceivedVectorA.getRight() * right + perceivedVectorA.getDown() * down + vAF)
				/ zBuffer + vABDotOffset.getRight(),
			(perceivedVectorB.getRight() * right + perceivedVectorB.getDown() * down + vBF)
				/ zBuffer + vABDotOffset.getDown());
	}

	/*
	 * Finds the texture of the given point and calculates the new shade taking into
	 * account the given light source. Fall off proportional to 1/distance
	 */
	public int determineShade(int right, int down, double zBuffer, LightSource lightSource) {
		// findUV.right
		double u = (perceivedVectorA.getRight() * right + perceivedVectorA.getDown() * down
			+ vAF) / zBuffer + vABDotOffset.getRight();

		// findUV.down
		double v = (perceivedVectorB.getRight() * right + perceivedVectorB.getDown() * down
			+ vBF) / zBuffer + vABDotOffset.getDown();
		
		/*
		 * Coordinates of the vector from the traced back point (in 3d) to the light
		 * source. Equivalent to (lightSource - u*vectorA - v*vectorB).
		 */
		double diffRight = lightSource.lightSource.getRight() - u * vectorA.getRight()
			- v * vectorB.getRight();
		double diffDown = lightSource.lightSource.getDown() - u * vectorA.getDown()
			- v * vectorB.getDown();
		double diffForward = lightSource.lightSource.getForward() - u * vectorA.getForward()
			- v * vectorB.getForward();

		/*
		 * If a is the difference vector from the traced back point to the light source,
		 * then the brightness is equivalent to intensity * (a DOT orientation) / (a DOT
		 * a), clamped to be between 0 and 1. Math.fma used for a little extra
		 * efficiency.
		 */
		double brightness = Math.clamp(lightSource.intensity * lightSource.dot(this) / (Math
			.fma(diffRight, diffRight, Math.fma(diffDown, diffDown, diffForward * diffForward))), 0,
			1);

		return (int) (texture.determineShadeAt(u, v) * brightness);
	}

	public void render() {
		observer.polygon(perceivedPoints, 2, ID, false);
	}
	
	public void writeToStencil() {
		observer.polygon(perceivedPoints, 2, ID, true);
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
			uVVectorTail.getRight(), uVVectorTail.getDown(), vectorA, vectorB);
		
		vectorTail.translate(offset);
		
		R3Point vectorTip = new R3Point(vectorTail);

		vectorTip.translate(orientation);

		return new RelativeLine(vectorTail, vectorTip, observer);
	}
	
	// Returns true if the polygon is facing the point.
	public boolean isFacing(R3Point point) {
		return differenceDotNormal(point) > Constants.EPSILON;
	}

	// (point - any point of the polygon) DOT (orientation). Independent of choice.
	public double differenceDotNormal(R3Point point) {
		return point.difference(offset).dot(orientation);
	}
	
	private void checkTextured() {
		if  ((getID() & 1) == 0) {
			throw new IllegalStateException("Polygon is untextured");
		}
	}
}
