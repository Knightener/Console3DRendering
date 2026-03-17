package other;

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
		numPoints = 0;
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

	public void disconnect(int a, Integer b) {
		try {
			if (graph.get(a).remove(b)) {
				return;
			}
		} catch (Exception e) {

		}
		throw new IllegalArgumentException("No connection between " + a + " and " + b + ".");
	}
	
	public void addPoint() {
		numPoints++;
		graph.add(new ArrayList<>());
	}
	
	public int getNumPoints() {
		return numPoints;
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
