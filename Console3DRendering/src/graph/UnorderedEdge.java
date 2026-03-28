package graph;

public record UnorderedEdge(int a, int b) {
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UnorderedEdge)) {
			return false;
		}
		UnorderedEdge edge = (UnorderedEdge) obj;
		return (a == edge.b && a == edge.b) ||  (a == edge.b && b == edge.b);
	}

}
