package other;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class IndexGraph{

	/*
	 * Implementation of a directed graph where the points are only named by their
	 * index.
	 */

	// The index at i is a list of points connected to point i.
	List<List<Integer>> graph;
	int numPoints;
	int numEdges;

	public IndexGraph() {
		graph = new ArrayList<>();
		numPoints = 0;
	}

	public IndexGraph(int numPoints) {
		graph = new ArrayList<>();
		this.numPoints = numPoints;
	}

	// Initializes a graph such that adjacent elements of base are connected left-> right
	public IndexGraph(int[] base) {
		this(Arrays.stream(base).max().getAsInt());
		for (int i = 0; i < base.length; i++) {
			connect(base[i], base[(i + 1) % base.length]);
		}
	}

	// Returns true if there is a connection between a and b. 
	public boolean isConnected(int a, int b) {
		if (a >= numPoints) { 
			return false; 
		}
		return graph.get(a).contains(b);
	}

	// Connects a and b. 
	public void connect(int a, int b) {
		if (a < numPoints && b < numPoints) {
			graph.get(a).add(b);
			numEdges++;
		} else {
			throw new IndexOutOfBoundsException("Both points must be in range.");
		}
	}

	public void disconnect(int a, Integer b) {
		try {
			if (graph.get(a).remove(b)) {
				numEdges--;
				return;
			}
		} catch (Exception e) {

		}
		throw new IllegalArgumentException("No connection between " + a + " and " + b + ".");
	}
	
	public void addPoint() {
		numPoints++;
	}
	


}
