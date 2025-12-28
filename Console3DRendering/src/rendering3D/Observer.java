package rendering3D;

import classes2D.*;
import classes3D.*;
import other.Constants;
import rendering2D.*;

public class Observer {

	// What the observer is seeing.
	Image view;

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
	public Observer(R3Point position, double theta, double phi, Image view, double fov) {

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

	public Image getView() {
		return view;
	}

	// Added because it feels more natural to have the observer "act on" the simplex.
	public void updatePerspective(RelativeSimplex simplex) {
		simplex.updatePerspective(this);
	}
	
	public void updatePerspective(Form form) {
		
		for (RelativeSimplex face : form.components) {
			updatePerspective(face);
		}
		
		form.determineRenderingOrder();
	}

	// Everything past this point is drawing methods
	
	public Figure lineDefault(R3Point p1, R3Point p2, int maxShade) {
	
		double f1 = p1.getForward();
		double f2 = p2.getForward();
	
		if (f1 > Constants.EPSILON && f2 > Constants.EPSILON) {
			return view.line(p1.project(fov), p2.project(fov), maxShade);
		}
		if (f1 > Constants.EPSILON) {
			R3Point direction = p2.difference(p1);
			direction.scale(-f1 / direction.getForward());
			direction.translate(p1);
			return view.ray(p1.project(fov), direction.projectAlt(fov), maxShade);
	
		}
		if (f2 > Constants.EPSILON) {
			R3Point difference = p1.difference(p2);
			difference.scale(-f2 / difference.getForward());
			difference.translate(p2);
			return view.ray(p2.project(fov), difference.projectAlt(fov), maxShade);
		}
		return new Figure();
	}

	public Figure line(R3Point p1, R3Point p2) {
	
		return lineDefault(perspective(p1), perspective(p2), ShadeHandling.getMaxPossibleShade());
	}

	private Figure triangleAux(R3Point p1, R3Point p2, R3Point p3, int maxShade, boolean isSmooth) {
		
	
		// Initialized for convenience.
		double r1 = p1.getRight();
		double r2 = p2.getRight();
		double r3 = p3.getRight();
	
		double d1 = p1.getDown();
		double d2 = p2.getDown();
		double d3 = p3.getDown();
	
		double f1 = p1.getForward();
		double f2 = p2.getForward();
		double f3 = p3.getForward();
				
		if (f1 > Constants.EPSILON) {
			return view.triangle(p1.project(fov), p2.project(fov), p3.project(fov), maxShade, isSmooth);
		}
		
		if (f2 > Constants.EPSILON) {
			Figure triangle = new Figure();
	
			/*
			 * The idea is to find a far enough point that acts as a start for the line from
			 * p1 to p2 and p1 to p3, since the line can't be projected directly due to
			 * having points behind the observer.
			 * 
			 * The exact derivation is just a bunch of algebra, but the idea is to intersect
			 * the line from p1 to p2/p3 with the plane forward = 0.
			 */
			
			R3Point start12 = new R3Point(r2 - r1, d2 - d1, 0);
			R3Point start13 = new R3Point(r3 - r1, d3 - d1, 0);
	
			start12.scale(f1 / (f1 - f2));
			start13.scale(f1 / (f1 - f3));
	
			start12.translate(r1, d1, 0);
			start13.translate(r1, d1, 0);
			
			R2Point proj12 = start12.projectAlt(fov);
			R2Point proj13 = start13.projectAlt(fov);
			
			/*
			 * Squaring is arbitrary here. It's just to (try to) make sure that the whole
			 * screen is covered when the triangle is very flat. Of course, the triangle can
			 * still be flat enough to where the screen doesn't get covered, but I doubt
			 * that'll end up being serious issue. If it does, I'll try to make a scaling
			 * factor that adjusts for the triangle's flatness.
			 * 
			 * The nice part about the triangle method is that going far beyond the bounds
			 * of the image doesn't increase the processing time by that much.
			 */
			double scaleFactor = view.getFurthestOut() * view.getFurthestOut();
			
			proj12.scale(scaleFactor);
			proj13.scale(scaleFactor);
	
			R2Point proj2 = p2.project(fov);
			R2Point proj3 = p3.project(fov);
	
			proj12.translate(proj2);
			proj13.translate(proj3);
	
			/*
			 * I'm not exactly sure why this specific order of points works, but it 
			 * returned a full triangle when tested.
			 */
			triangle.add(view.jaggedTriangle(proj12, proj13, proj3, maxShade));
	
			triangle.add(view.jaggedTriangle(proj12, proj3, proj2, maxShade));
	
			// A little bit more efficient than directly calling the triangle method.
			if (isSmooth) {
				triangle.add(view.line(proj12, proj2));
				triangle.add(view.line(proj13, proj3));
				triangle.add(view.line(proj2, proj3));
			}
			return triangle;
		}
	
		if (f3 > Constants.EPSILON) {
	
			/*
			 * Similar concept to the previous case, but actually simpler since the
			 * projected triangle will have 3 sides instead of 4.
			 */
			R3Point start23 = new R3Point(r2 - r3, d2 - d3, 0);
			R3Point start13 = new R3Point(r3 - r1, d3 - d1, 0);
	
			start23.scale(f3 / (f3 - f2));
			start13.scale(f1 / (f1 - f3));
	
			start23.translate(r3, d3, 0);
			start13.translate(r1, d1, 0);
			
			R2Point proj23 = start23.projectAlt(fov);
			R2Point proj13 = start13.projectAlt(fov);
			
			double scaleFactor = view.getFurthestOut() * view.getFurthestOut();
			
			proj23.scale(scaleFactor);
			proj13.scale(scaleFactor);
			
			R2Point proj3 = p3.project(fov);
	
			proj23.translate(proj3);
			proj13.translate(proj3);
	
			return view.triangle(proj13, proj23, proj3, maxShade, isSmooth);
		}
		
		return new Figure();
	
	}

	public Figure triangleDefault(R3Point p1, R3Point p2, R3Point p3, int maxShade, boolean isSmooth) {
		
		boolean f2f3 = p2.getForward() <= p3.getForward();
		boolean f1f3 = p1.getForward() <= p3.getForward();
		boolean f1f2 = p1.getForward() <= p2.getForward();
	
	
		// Same justification as the 2d version of this method
		if (f1f2) {
			if (f2f3) {
				return triangleAux(p1, p2, p3, maxShade, isSmooth);
			}
			if (f1f3) {
				return triangleAux(p1, p3, p2, maxShade, isSmooth);
			}
			return triangleAux(p3, p1, p2, maxShade, isSmooth);
		}
		if (f1f3) {
			return triangleAux(p2, p1, p3, maxShade, isSmooth);
		}
		if (f2f3) {
			return triangleAux(p2, p3, p1, maxShade, isSmooth);
		}
		return triangleAux(p3, p2, p1, maxShade, isSmooth);
	}

	public Figure triangle(R3Point p1, R3Point p2, R3Point p3, int maxShade, boolean isSmooth) {
	
		return triangleDefault(perspective(p1), perspective(p2), perspective(p3), maxShade, isSmooth);
		
	}

	public void renderDirectly(RelativeSimplex simplex) {
		view.replace(simplex.viewedBy(this));
	}

	public void renderDirectly(Form form) {
		for (RelativeSimplex face : form.components) {
			renderDirectly(face);
		}
	}
	
	
	
}
