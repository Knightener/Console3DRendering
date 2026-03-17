package other;

import java.util.HashMap;
import java.util.Map;

public class Graph {
	
	Map<Integer,Boolean> graph;
	
	public Graph() {
		graph = new HashMap<>();
	}

	public boolean isConnected(int a, int b) {
		return graph.getOrDefault((a << 16) | b, false);
	}

	public boolean connect(int a, int b) {
		return graph.put((a << 16) | b, true);
	}
	
	public boolean disconnect(int a, int b) {
		return graph.remove((a << 16) | b);
	}
	
	
}
