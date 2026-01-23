package rendering3D;

import java.util.HashMap;

import other.MiscFunctions;
import zBuffered2DRendering.ZFigure;

public abstract class RelativeSimplex implements Comparable<RelativeSimplex>{
	/*
	 * A simplex is the smallest convex shape that holds n+1 points in n dimensions.
	 * 
	 * For the purposes of this project, the only simplexes that will be considered 
	 * are lines, triangles, and possibly points. The point of this class
	 * is to assist in rendering something which has both lines and triangles.
	 */

	// Unique integer assigned to each simplex. The first 12 integers store the associated observer's ID.
	private int ID;
	private static int currentGreatestID = 1;
	
	// Number of integers of ID dedicated to the component.
	private static int componentLength = 20;
	
	// Returns the simplex associated with the given ID.
	private static HashMap<Integer, RelativeSimplex> IDMap = new HashMap<Integer, RelativeSimplex>();

	
	double leastForward;
	double mostForward;
	
	public RelativeSimplex(Observer observer) {
		ID = currentGreatestID++ + (observer.getID() << componentLength);
		IDMap.put(ID, this);
	}
	
	public Observer getObserver() {
		return Observer.get(ID >> componentLength);
	}
	
	public @SuppressWarnings("unchecked") static <T extends RelativeSimplex> T get(int ID) {
		return (T)IDMap.get(ID);
	}
	
	public int getID() {
		return ID;
	}
	
	/* This function determines the order in which the simplexes will be rendered.
	 * 
	 * The closer simplex is determined to be "greater". The further simplex is
	 * rendered first, and hence due to overlap, the triangle will appear to be in
	 * front of the other triangle
	 */
	public int compareTo(RelativeSimplex simplex) {

		if (!MiscFunctions.nearlyEquals(leastForward, simplex.leastForward)) {

			if (leastForward < simplex.leastForward) {
				return 1;
			}
			if (leastForward > simplex.leastForward) {
				return -1;
			}
		}
		
		// tiebreaker
		if (mostForward < simplex.mostForward) {
			return 1;
		}
		if (mostForward > simplex.mostForward) {
			return -1;
		}

		return 0;

	}

	// Updates the simplex to be relative to the observer's perspective 
	abstract void updatePerspective();

	/*
	 * Since leastForward and mostForward aren't independent of the observer, they
	 * will not be set in the constructors of any subclass.
	 */
	abstract void determineMostAndLeastForward();

	abstract ZFigure viewed();

}
