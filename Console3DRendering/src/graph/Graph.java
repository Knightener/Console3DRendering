package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Graph<T> implements Iterable<Edge<T>> {

	IndexGraph graph;
	
	// Which indices correspond to which elements. 
	List<T> idxToElement;
	
	// What element correspond to what index. Inverse of assignments. 
	Map<T, Integer> elementToIdx;
	
	public Graph() {
		idxToElement = new ArrayList<>();
		elementToIdx = new HashMap<>();
		graph = new IndexGraph();
	}
	
	@SafeVarargs
	public Graph(T...points) {
		this();
		addPoints(points);
		
	}
	public boolean isConnected(T a, T b) {
		try {
			return graph.isConnected(elementToIdx.get(a), elementToIdx.get(b));
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Element not present in graph.");
		}
	}

	public void connect(T a, T b) {
		try {
			graph.connect(elementToIdx.get(a), elementToIdx.get(b));
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Element not present in graph.");
		}
	}
	
	public void disconnect(T a, T b) {
		try {
			graph.disconnect(elementToIdx.get(a), elementToIdx.get(b));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("No connection between " + a + " and " + b + ".");
		}
	}
	
	public void addPoint(T point) {
		int currIdx = graph.getNumPoints();
		graph.addPoint();
		idxToElement.add(point);
		elementToIdx.put(point, currIdx);
	}

	@SafeVarargs
	public final void addPoints(T... points) {
		for (T element : points) {
			addPoint(element);
		}
	}
	
    @Override
    public Iterator<Edge<T>> iterator() {
    	Iterator<Edge<T>> it = new Iterator<Edge<T>>() {
    		Iterator<IndexEdge> iterator = graph.iterator();
    		
    		@Override
    		public boolean hasNext() {
    			return iterator.hasNext();
    		}
    		
    		@Override
    		public Edge<T> next() {
    			IndexEdge currEdge = iterator.next();
    			return new Edge<>(idxToElement.get(currEdge.to()), idxToElement.get(currEdge.from()));
    		}	
    	};
    	return it;
    }
	
    @Override
    public String toString() {
    	StringBuilder graphString = new StringBuilder();

		for (int i = 0; i < graph.getNumPoints(); i++) {
			List<Integer> curr = graph.getConnections(i);
			graphString.append(idxToElement.get(i) + ": ");
			for (int element : curr) {
				graphString.append(idxToElement.get(element) + " ");
			}
			graphString.append("\n");
		}
		
		return graphString.toString();
    }

}
