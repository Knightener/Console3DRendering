package rendering3D;

import java.util.HashMap;

public abstract class ObserverDependant {

	/*
	 * This class provides a convenient way of working with objects that depend on
	 * some observer.
	 */
	
	/*
	 * Unique ID associated to each instance observerDependant. First n bits
	 * dedicated to observer, where n is 32 - componentBits. Last bit is 1 if polygon
	 * is textured and 0 otherwise. 
	 */
	private int ID;

	private static int currentGreatestID = 1;

	// Number of bits of ID dedicated to the component.
	private static int componentBits = 24;

	// Returns the component associated with the given ID.
	private static HashMap<Integer, ObserverDependant> IDMap = new HashMap<Integer, ObserverDependant>();

	public ObserverDependant(Observer observer) {
		ID = 2*currentGreatestID++ + (observer.getID() << componentBits);
		IDMap.put(ID, this);
	}
	
	public ObserverDependant(Observer observer, boolean isTextured) {
		ID = 2*currentGreatestID++ + (observer.getID() << componentBits) + (isTextured ? 1 : 0);
		IDMap.put(ID, this);
	}
	
	public @SuppressWarnings("unchecked") static <T extends ObserverDependant> T get(int ID) {
		return (T)IDMap.get(ID);
	}
	
	public int getID() {
		return ID;
	}
	
	public Observer getObserver() {
		return Observer.get(ID >> componentBits);
	}
	
}
