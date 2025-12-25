package rendering3D;

import classes2D.*;
import classes3D.*;
import rendering2D.*;

public class Observer {

	// What the observer is seeing.
	DrawingMethods2D view;

	double fov;

	R3Point position;
	
	// Stored for convenience
	R3Point negativePosition;

	/*
	 * The rotation matrix that will be applied to points when they are observed.
	 * 
	 * The columns of the inverse of this matrix corresponds to the observer's
	 * orientation in space. This is because rotating your head to the left looks
	 * like keeping your head fixed and the world around you rotating to the right.
	 * 
	 * Since rotation matrices are orthogonal, its inverse is equal to its
	 * transpose, hence the rows of the matrix correspond to the observer's
	 * orientation.
	 */
	R3Matrix rotation;

	/*
	 * Theta rotates the observer to the left and phi rotates the observer up. Phi
	 * is restricted to the interval -pi/2 , pi/2 to avoid flipping the observer
	 * upside down.
	 * 
	 * The formula for the rotation matrix was derived by multiplying the rotation
	 * matrices for theta alone and phi alone, which can be easily written down.
	 */
	Observer(R3Point position, double theta, double phi, DrawingMethods2D view, double fov) {

		if (-Math.PI / 2 > phi || phi > Math.PI / 2) {
			throw new IllegalArgumentException();
		}
		this.view = view;
		this.position = position;
		this.negativePosition = new R3Point(position);
		negativePosition.scale(-1);
		this.fov = fov;

		double sinT = Math.sin(theta);
		double cosT = Math.cos(theta);
		double sinP = Math.sin(phi);
		double cosP = Math.cos(phi);

		rotation = new R3Matrix(cosT, -sinT * sinP, sinT * cosP, 0, cosP, sinP, -sinT, 
			-cosT * sinP, cosT * cosP);

	}

	public R2Point lookAt(R3Point point) {
		return perspective(point).project(fov);

	}

	public R2Point lookAtAlt(R3Point point) {
		return perspective(point).projectAlt(fov);

	}

	// Returns the position of the point from the observer's perspective
	public R3Point perspective(R3Point point) {
		return rotation.transform(point.difference(position));
	}

	// Updates the point to be relative from the observer
	public void updatePerspective(R3Point point) {
		point.translate(negativePosition);
		rotation.updateTransform(point);
	}

	public DrawingMethods2D getView() {
		return view;
	}

	public void updatePerspective(RelativeTriangle triangle) {
		
		triangle.perceivedPointA = perspective(triangle.pointA);
		triangle.perceivedPointB = perspective(triangle.pointB);
		triangle.perceivedPointC = perspective(triangle.pointC);
		
		triangle.determineMostAndLeastForward();
	}
	
	public void updatePerspective(Form form) {
		
		for (RelativeTriangle face : form.faces) {
			updatePerspective(face);
		}
		
		form.determineRenderingOrder();
	}
	
	
	
}
