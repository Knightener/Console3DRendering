package graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IndexGraph implements Iterable<IndexEdge>{

	/*
	 * Implementation of a directed graph where the points are only named by their
	 * index.
	 */

	// The index at i is a list of points connected to point i.
	private List<List<Integer>> graph;
	private int numPoints;

	public IndexGraph() {
		graph = new ArrayList<>();
	}

	public IndexGraph(int numPoints) {
		graph = new ArrayList<>();
		for (int i = 0; i < numPoints; i++) {
			graph.add(new ArrayList<>());
		}
		this.numPoints = numPoints;
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
		} else {
			throw new IndexOutOfBoundsException("Both points must be in range.");
		}
	}
	
	// Finds the first point of graph which leads to at least one other point.
	public int findConnectedPoint() {
		for (int i = 0; i < numPoints; i++) {
			if (!graph.get(i).isEmpty()) {
				return i;
			}
		}
		throw new IllegalStateException("Graph has no connections.");
	}

	// Disconnects a and b.
	public void disconnect(int a, Integer b) {
		try {
			if (graph.get(a).remove(b)) {
				return;
			}
		} catch (Exception e) {

		}
		throw new IllegalArgumentException("No connection between " + a + " and " + b + ".");
	}

	// Extends the number of points if newPoints > numPoints. Else, does nothing.
	public void extend(int newPoints) {
		for (int i = numPoints; i < newPoints; i++) {
			graph.add(new ArrayList<>());
		}
	}
	
	// Adds a, b if and only if b,a is not present in graph. Else, removes a,b.
	public void addNonOpposite(int a, int b) {
		if (graph.get(b).contains(a)) {
			graph.get(b).remove(new Integer(a));

			// Same direction.
		} else if (graph.get(a).contains(b)) {

		} else {
			graph.get(a).add(b);
		}
	}
	
	// Adds a point to the graph. 
	public void addPoint() {
		numPoints++;
		graph.add(new ArrayList<>());
	}
	
	public int getNumPoints() {
		return numPoints;
	}
	
	List<Integer> getConnections(int i) {
		return graph.get(i);
	}
	
	/*
	 * Adds all edges of other to this but removes overlapping ones that point in
	 * opposite directions. Overlapping edges that point the same direction are
	 * kept.
	 */
	public void mergeNonOpposite(IndexGraph other) {
		extend(other.numPoints);
		List<Integer> curr;
		for (int i = 0; i < other.numPoints; i++) {
			curr = other.graph.get(i);
			for (int j = 0; j < curr.size(); j++) {
				addNonOpposite(i, curr.get(j));
			}
		}
	}

	// Same as the above method, but with a cycle represented by an array.
	public void mergeNonOpposite(int[] cycle) {
		for (int i = 0; i < cycle.length; i++) {
			addNonOpposite(cycle[i], cycle[(i + 1) % cycle.length]);
		}
	}

	/*
	 * Finds a cycle containing the point start. Note that this algorithm does not
	 * necessarily always find a cycle even if there is one containing start.
	 * However, an important class of graphs for which a cycle will always be found
	 * are graphs without dead ends. I have optimized this algorithm for this
	 * scenario as the main use case I have for this is to convert cyclic graphs
	 * into arrays.
	 */
	public int[] findCycleFromStart(int start) {
		ArrayList<Integer> cycle = new ArrayList<>();

		int currentPoint = start;
		int index = -1;

		while (!graph.get(currentPoint).isEmpty()) {
			
			if (index == -1) {
				cycle.add(currentPoint);
			} else {
				int[] cycleArr = new int[cycle.size() - index];
				for (int i = 0; i < cycleArr.length; i++) {
					cycleArr[i] = cycle.get(i + index);
				}
				return cycleArr;
			}

			currentPoint = graph.get(currentPoint).get(0);
			index = cycle.indexOf(currentPoint);
		}

		throw new IllegalStateException("Graph has a dead end from start");
	}

	// Iterator for all the edges of the graph. 
    @Override
    public Iterator<IndexEdge> iterator() {
    	Iterator<IndexEdge> it = new Iterator<>() {
    		
    		// Index of current point. 
    		public int pointIndex = 0;
    		
    		// Index of current edge. 
			public int edgeIndex = 0;

			// This is for if the first point has no edges. 
			{
				if (graph.get(0).size() == 0) {
					findNextEdge();
				}
			}

			@Override
			public boolean hasNext() {
				return pointIndex < numPoints;
			}

			@Override
			public IndexEdge next() {
				IndexEdge currEdge = new IndexEdge(pointIndex, graph.get(pointIndex).get(edgeIndex));
				findNextEdge();
				return currEdge;
			}
           
			// Finds the indices of the next valid edge. 
			private void findNextEdge() {
				if (edgeIndex < graph.get(pointIndex).size() - 1) {
					edgeIndex++;
				} else {
					pointIndex++;
					// If multiple points in a row are empty. 
					while (pointIndex < numPoints) {
						if (!graph.get(pointIndex).isEmpty()) {
							break;
						}
						pointIndex++;
					}
					edgeIndex = 0;
				}
			}
    	};
    	return it;
    }
    
    public IndexGraph getTranspose() {
    	IndexGraph graph = new IndexGraph(numPoints);
    	for (IndexEdge edge : this) {
    		graph.connect(edge.to(), edge.from());
    	}
    	return graph;
    }
    
    @Override
    public String toString() {
    	StringBuilder graphString = new StringBuilder();

		for (int i = 0; i < numPoints; i++) {
			List<Integer> curr = graph.get(i);
			graphString.append(i + ": ");
			for (int element : curr) {
				graphString.append(element + " ");
			}
			graphString.append("\n");
		}
		
		return graphString.toString();
    }

}
