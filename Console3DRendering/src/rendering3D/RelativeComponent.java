package rendering3D;

import java.util.HashMap;

public abstract class RelativeComponent implements Comparable<RelativeComponent> {
	/*
	 * A component is either a line, a point, or a polygon. These will serve as the
	 * basic building blocks for building scenes.
	 */

	double leastForward;
	double mostForward;
	
	protected Observer observer;

	protected int ID;

	private static int currentGreatestID = 1;

	// Returns the component associated with the given ID.
	private static HashMap<Integer, RelativeComponent> IDMap = new HashMap<Integer, RelativeComponent>();

	public RelativeComponent(Observer observer) {
		this.observer = observer;
		ID = 2 * currentGreatestID++;
		IDMap.put(ID, this);
	}

	public RelativeComponent(Observer observer, boolean isTextured) {
		this.observer = observer;
		ID = 2 * currentGreatestID++ + (isTextured ? 1 : 0);
		IDMap.put(ID, this);
	}
	
	public @SuppressWarnings("unchecked") static <T extends RelativeComponent> T get(int ID) {
		return (T)IDMap.get(ID);
	}
	
	public int getID() {
		return ID;
	}

	/*
	 * This function determines if a component is in front of some other component.
	 * 
	 * Returns 1 if every single point of this is in front of (has a smaller
	 * forward) of every single point of component B, -1 if every point of this is
	 * behind component, 0 otherwise.
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

	abstract void viewed();

}
