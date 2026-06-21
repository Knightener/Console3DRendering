package other;

import java.util.HashMap;
import java.util.Map;

public class Bijection<T, U> {

	// Represents a one to one and onto correspondence between a set of T and a set of U.
	
	Map<T, U> map;
	Map<U, T> inverseMap;
	
	public Bijection() {
		map = new HashMap<>();
		inverseMap = new HashMap<>();
	}
	
	public void pair(T key, U value) {
		if (map.containsKey(key)) {
			throw new IllegalArgumentException("Key " + key + " already paired.");
		}
		if (inverseMap.containsKey(value)) {
			throw new IllegalArgumentException("Value " + value + " already paired.");
		}
		map.put(key, value);
		inverseMap.put(value, key);
	}
	
	public U get(T key) {
		return map.get(key);
	}
	
	public T inverseGet(U value) {
		return inverseMap.get(value);
	}
}
