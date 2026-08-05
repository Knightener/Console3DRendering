
package rendering3D;

import java.util.ArrayList;
import java.util.List;

import classes3D.R3Matrix;
import classes3D.R3Point;
import other.Constants;
import zBuffered2DRendering.ZBuffer;
import zBuffered2DRendering.ZPixel;

public class Spotlight {

	// Stripped down observer class for shade mapping purposes.
	
	ZBuffer zBuffer;
	double fov;
	
	// Contains lighting and position info
	LightSource lightSource;
	
	R3Matrix rotation;

	public Spotlight(LightSource lightSource, double theta, double phi, ZBuffer zBuffer, double fov) {
		this.zBuffer = zBuffer;
		this.lightSource = lightSource;
		this.fov = fov;

		setOrientation(theta, phi);
	}
	
	public boolean isLit(double x, double y, double z, int polygonID) {
		
		double xDiff = x - lightSource.getX();
		double yDiff = y - lightSource.getY();
		double zDiff = z - lightSource.getZ();

		/*
		 * Written out explicitly to avoid object creation overhead (this function will
		 * be called a lot). Equivalent to the coordinates of perspective(new
		 * R3Point(x,y,z))
		 */
		double zPer = rotation.getA31() * xDiff + rotation.getA32() * yDiff
			+ rotation.getA33() * zDiff;
		
		if (zPer < Constants.NEAR_EPSILON) {
			return false;
		}
		
		double xPer = rotation.getA11() * xDiff + rotation.getA12() * yDiff
			+ rotation.getA13() * zDiff;
		double yPer = rotation.getA21() * xDiff + rotation.getA22() * yDiff
			+ rotation.getA23() * zDiff;
		
		double ratio = fov/zPer;
		
		return zBuffer.zPass((int) (xPer * ratio), (int) (yPer * ratio), ratio, polygonID);
	}

	public R3Point perspective(R3Point point) {
		return rotation.transform(point.difference(lightSource.getPosition()));
	}

	public R3Point getPosition() {
		return lightSource.getPosition();
	}
	
	public void setOrientation(double theta, double phi) {
		if (-Math.PI / 2 > phi || phi > Math.PI / 2) {
			throw new IllegalArgumentException("phi must be within [-pi/2 , pi/2]");
		}

		double sinT = Math.sin(theta);
		double cosT = Math.cos(theta);
		double sinP = Math.sin(phi);
		double cosP = Math.cos(phi);

		rotation = new R3Matrix(cosT, 0, sinT, -sinT * sinP, cosP, cosT * sinP, -sinT * cosP, -sinP,
			cosT * cosP);
		
	}
	
	public double getFov() {
		return fov;
	}
	

	/*
	 * Unlike observer, polygons will not have their coordinates pre adjusted to be
	 * relative to the observer. Hence, the method adjusts them.
	 * 
	 * Optimization is not very important as the spotlight, unlike the observer, will
	 * not be moving much.
	 */
	public void polygon(List<R3Point> points, int polygonID) {

		ArrayList<ZPixel> viewedPolygon = new ArrayList<ZPixel>();

		int length = points.size();

		for (int i = 0; i < length; i++) {

			R3Point curr = perspective(points.get(i));
			R3Point next = perspective(points.get((i + 1) % length));

			double currZ = curr.getZ();
			double nextZ = next.getZ();

			// 0 is a placeholder as there is no writing to shade
			if (currZ > Constants.NEAR_EPSILON) {
				viewedPolygon.add(curr.project(fov, 0, polygonID));
			}

			if (currZ > Constants.NEAR_EPSILON ^ nextZ > Constants.NEAR_EPSILON) {
				viewedPolygon.add(curr.nearPlaneIntersection(next).project(fov, 0, polygonID));
			}
		}

		zBuffer.polygon(viewedPolygon);
	}
	
	public void render(RelativePolygon polygon) {
		polygon(polygon.getVertexList(), polygon.getID());
	}
	
	public void render(Mesh mesh) {
		for (RelativePolygon face : mesh.faces) {
			render(face);
		}
	}
	
	public double dot(RelativePolygon polygon) {
		return lightSource.dot(polygon);
	}
	
	public double getIntensity() {
		return lightSource.getIntensity();
	}
	
	public double getX() {
		return lightSource.getX();
	}
	
	public double getY() {
		return lightSource.getY();
	}
	
	public double getZ() {
		return lightSource.getZ();
	}
	
}
	