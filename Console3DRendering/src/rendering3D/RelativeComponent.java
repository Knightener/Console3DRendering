package rendering3D;

import zBuffered2DRendering.ZFigure;

public abstract class RelativeComponent extends ObserverDependant implements Comparable<RelativeComponent> {
	/*
	 * A component is either a line, a point, or a polygon. These will serve as the
	 * basic building blocks for building scenes.
	 */

	double leastForward;
	double mostForward;
	

	
	public RelativeComponent(Observer observer) {
		super(observer);
	}

	/*
	 * This function determines if a component is in front of some other component.
	 * 
	 * Returns 1 if every single point of this is in front of (has a smaller
	 * forward) of every single point of component B, -1 if every point of
	 * this is behind component, 0 otherwise.
	 * 
	 * This is not a total ordering.
	 */
	public int compareTo(RelativeComponent component) {

		if (mostForward < component.leastForward) {
			return 1;
		}

		if (leastForward > component.mostForward) {
			return -1;
		}

		return 0;

	}

	// Updates the component to be relative to the observer's perspective 
	abstract void updatePerspective();

	/*
	 * Since leastForward and mostForward aren't independent of the observer, they
	 * will not be set in the constructors of any subclass.
	 */
	abstract void determineMostAndLeastForward();

	abstract ZFigure viewed();

}
