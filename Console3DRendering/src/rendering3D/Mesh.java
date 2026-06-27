package rendering3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import classes3D.R3Point;
import graph.IndexEdge;
import graph.IndexGraph;
import graph.UnorderedEdge;
import other.Constants;
import texturing.Texture;
import texturing.TexturePresets;

public class Mesh implements Renderable {

	/*
	 * This class differs from form in that it can only store faces, all edges must
	 * be shared by only 1 or 2 faces, and all faces are connected (in the sense
	 * that it is possible to traverse from any face to any other face). Unlike
	 * form, it eliminates a lot of redundant calculations that are found from
	 * adjacent faces.
	 */

	private Observer observer;
	
	// Vertices of the mesh.
	private List<R3Point> vertices;

	// Vertices of the mesh relative to the observer.
	private List<R3Point> relativeVertices;
	
	// Vertices of the faces as indices of the vertices list.
	private List<int[]> faceIndices;

	// Faces of the mesh.
	List<MeshPolygon> faces;

	/*
	 * For 0 <= i < vertices.size(), associatedFace(i) is some (n,m) such that
	 * faceIndices.get(n)[m] = i. If no such n,m exists, associatedFace(i) is null.
	 * In other words, this gives a face that is adjacent to a given vertex, as an
	 * index of the list vertices.
	 */
	private List<IndexEdge> associatedFace;

	/*
	 * Edges (as indices of the vertices list) that are only adjacent to one
	 * singular polygon.
	 */
	private IndexGraph border;

	/*
	 * Private nested class that allows for unrestricted manipulation of some of the
	 * RelativePolygon instance fields.
	 */
	private class MeshPolygon extends RelativePolygon {

		MeshPolygon(Observer observer, Texture texture, R3Point... points) {
			super(observer, texture, points);
		}

		// Returns the vertex at index i as viewed by the observer. 
		R3Point viewedVertex(int index) {
			return R3Point.linearCombination(uVPoints.get(index).getRight(), 
					uVPoints.get(index).getDown(), perceivedVectorA, perceivedVectorB)
					.sum(perceivedOffset);
		}

		void setPerceivedPoints(ArrayList<R3Point> perceivedPoints) {
			this.perceivedPoints = perceivedPoints;
		}
	}

	public Mesh(Observer observer, List<R3Point> vertices) {
		this.observer = observer;
		this.vertices = vertices;

		relativeVertices = new ArrayList<R3Point>();
		faceIndices = new ArrayList<int[]>();
		faces = new ArrayList<MeshPolygon>();
		associatedFace = new ArrayList<IndexEdge>();
		border = new IndexGraph(vertices.size());

		for (int i = 0; i < vertices.size(); i++) {
			associatedFace.add(null);
			relativeVertices.add(observer.perspective(vertices.get(i)));
		}

	}

	public Mesh(Observer observer, R3Point... vertices) {
		this(observer, Arrays.asList(vertices));
	}

	// Creates a face of the given indices of the vertices list. 
	private void unrestrictedCreateFace(Texture texture, int... indices) {

		faceIndices.add(indices);
		
		R3Point[] faceVertices = new R3Point[indices.length];
		Arrays.setAll(faceVertices, i -> vertices.get(indices[i]));

		MeshPolygon face = new MeshPolygon(observer, texture, faceVertices);

		ArrayList<R3Point> perceivedFaceVertices = new ArrayList<R3Point>();

		for (int i : indices) {
			perceivedFaceVertices.add(relativeVertices.get(i));
		}

		face.setPerceivedPoints(perceivedFaceVertices);

		faces.add(face);

		for (int i = 0; i < indices.length; i++) {
			if (associatedFace.get(indices[i]) == null) {
				associatedFace.set(indices[i], new IndexEdge(faceIndices.size() - 1, i));
			}
		}
	}

	private void addToBorder(int... indices) {
		border.mergeNonOpposite(indices);
	}

	// Returns true if face was successfully added and false otherwise.
	private boolean createFaceAux(Texture texture, int... indices) {

		int length = indices.length;
		for (int i = 0; i < length; i++) {
			// Overlapping edge found
			if (border.isConnected(indices[(i + 1) % length], indices[i])) {
				unrestrictedCreateFace(texture, indices);
				addToBorder(indices);
				return true;
			}
		}
		return false;
	}

	/*
	 * Adds a face to the mesh if and only if it is adjacent to the border. In other
	 * words, it must be adjacent to an edge that only has a singular polygon. This
	 * is to ensure consistent winding, and hence normals. The initial face will
	 * determine which side of the mesh is the interior and which side is the
	 * exterior.
	 */
	public void createFace(Texture texture, int... indices) {

		// Initial face if mesh has none.
		if (faceIndices.isEmpty()) {
			unrestrictedCreateFace(texture, indices);
			addToBorder(indices);
			return;
		}
		
		if (createFaceAux(texture, indices)) {
			return;
		}

		int length = indices.length;
		int[] reversedIndices = new int[length];

		for (int i = 0; i < length; i++) {
			reversedIndices[i] = indices[length - i - 1];
		}

		if (createFaceAux(texture, reversedIndices)) {
			return;
		}

		throw new IllegalArgumentException("Face is not adjacent to border.");
	}

	public void updatePerspective() {

		for (MeshPolygon face : faces) {
			face.updatePlaneVectors();
			face.findUVVariables();
		}

		IndexEdge index;

		for (int i = 0; i < vertices.size(); i++) {
			index = associatedFace.get(i);
			if (index != null) {
				relativeVertices.get(i).set(faces.get(index.from()).viewedVertex(index.to()));
			}
		}
	}
	
	// Returns a form with all normals.
	public Form getAllNormals() {
		Form normals = new Form(observer);
		for (RelativePolygon face : faces) {
			normals.add(face.getUnitNormal());
		}
		return normals;
	}

	// Returns the border of the visible faces from the light source.
	public List<R3Point> getVisibleBorder(LightSource lightSource) {

		IndexGraph visibleBorderGraph = new IndexGraph(vertices.size());

		for (int i = 0; i < faces.size(); i++) {
			if (lightSource.isFacing(faces.get(i))) {
				visibleBorderGraph.mergeNonOpposite(faceIndices.get(i));
			}
		}
		
		ArrayList<R3Point> visibleBorder = new ArrayList<>();

		for (int n : visibleBorderGraph.findCycleFromStart(visibleBorderGraph.findConnectedPoint())) {
			visibleBorder.add(vertices.get(n));
		}
		
		return visibleBorder;
	}

	// Returns a wire frame representing the mesh.
	public Form getWireFrame() {
		Set<UnorderedEdge> wireFrameEdges = new HashSet<>();

		for (int[] faceIndex : faceIndices) {
			for (int i = 0; i < faceIndex.length; i++) {
				wireFrameEdges.add(new UnorderedEdge(faceIndex[i], faceIndex[(i + 1) % faceIndex.length]));
			}
		}
		
		Form wireFrame = new Form(observer);

		for (UnorderedEdge edge : wireFrameEdges) {
			wireFrame.add(new RelativeLine(vertices.get(edge.a()), vertices.get(edge.b()), observer));
		}

		return wireFrame;
	}
	
	public Mesh getShadowVolume(LightSource lightSource, double extendMultiplier) {
		List<R3Point> border = getVisibleBorder(lightSource);

		int halfSize = border.size();

		for (int i = 0; i < halfSize; i++) {
			border.add(border.get(i).extendFrom(lightSource.lightSource, extendMultiplier));
		}

		Mesh shadowVolume = new Mesh(observer, border);
		for (int i = 0; i < halfSize; i++) {
			shadowVolume.createFace(null, i, (i + 1) % halfSize, (i + 1) % halfSize + halfSize, i + halfSize);
		}
		return shadowVolume;
	}
	
	public Mesh getCappedShadowVolume(LightSource lightSource, double extendFactor) {
		// These will be used to construct the mesh. 
		
		// Map of the index of a vertex in the current mesh to its index in the shadow mesh
		HashMap<Integer, Integer> frontFaceVertices = new HashMap<>(); 
		
		// List of indices of the current mesh that will be in the shadow. Determines an order. 
		List<Integer> vertexOrder = new ArrayList<>();
		
		// Indices of faceIndices that will be in the shadow. 
		List<Integer> frontFaceIndices = new ArrayList<>(); 
		
		// Average distance of the front face vertices from the light sources.
		double avgDistance = 0;
		
		int index = 0;
		for (int i = 0; i < faces.size(); i++) {
			if (lightSource.isFacing(faces.get(i))) {
				frontFaceIndices.add(i);
				for (int vertex : faceIndices.get(i)) {
					if (!frontFaceVertices.containsKey(vertex)) {
						frontFaceVertices.put(vertex, index++);
						vertexOrder.add(vertex);
						avgDistance += vertices.get(vertex).euclidian(lightSource.lightSource);
					}
				}
			}
		}
		
		avgDistance /= vertexOrder.size();
		
		// This helps mitigate Z-fighting
		double nearExtendFactor = 1 + Constants.EPSILON / avgDistance;
		double farExtendFactor = 1 + extendFactor / avgDistance;
		List<R3Point> shadowVertices = new ArrayList<>();
		
		/*
		 * Adding vertices of the shadow. If i is the index of a vertex of a the front
		 * cap, i + 1 represents the extended vertex of the back cap and vice versa.
		 */
		for (int n : vertexOrder) {
			shadowVertices.add(vertices.get(n).extendFrom(lightSource.lightSource, nearExtendFactor));
			shadowVertices.add(vertices.get(n).extendFrom(lightSource.lightSource, farExtendFactor));
		}

		Mesh shadowVolume = new Mesh(observer, shadowVertices);

		// Graph of the visible border as indices of the shadow volume.
		IndexGraph visibleBorderGraph = new IndexGraph(2 * vertexOrder.size());

		int[] currFace;
		int currLength; 
		for (int n : frontFaceIndices) {
			currFace = faceIndices.get(n);
			currLength = currFace.length;
			int[] mappedFace = new int[currLength];

			// Front face.
			for (int i = 0; i < currLength; i++) {
				mappedFace[i] = 2 * frontFaceVertices.get(currFace[i]);
			}

			// Finding visible border. 
			visibleBorderGraph.mergeNonOpposite(mappedFace);

			shadowVolume.unrestrictedCreateFace(null, mappedFace);

			// Extending current face.
			for (int i = 0; i < currLength; i++) {
				mappedFace[i]++;
			}
			
			int temp; 
			// Inverting face to maintain normals.
			for (int i = 0; i < currLength / 2; i++) {
				temp = mappedFace[i];
				mappedFace[i] = mappedFace[currLength - i - 1];
				mappedFace[currLength - i - 1] = temp;
			}

			// Back face.
			shadowVolume.unrestrictedCreateFace(null, mappedFace);
		}
		
		int[] visibleBorderCycle = visibleBorderGraph.findCycleFromStart(visibleBorderGraph.findConnectedPoint());
		
		int cycleLength = visibleBorderCycle.length;
		
		// Adding the side faces. 
		for (int i = 0; i < cycleLength; i++) {
			shadowVolume.unrestrictedCreateFace(null, 
				visibleBorderCycle[i], 
				visibleBorderCycle[i] + 1,
				visibleBorderCycle[(i + 1) % cycleLength] + 1,
				visibleBorderCycle[(i + 1) % cycleLength]);
		}
		return shadowVolume;

	}
	
	public void writeToStencil() {
		for (RelativePolygon face : faces) {
			face.writeToStencil();
		}
	}

	@Override
	public void render() {
		for (RelativePolygon face : faces) {
			face.render();
		}
	}
	
	@Override
	public Observer getObserver() {
		return observer;
	}

	public void invertOrientation() {
		for (RelativePolygon face : faces) {
			face.invertOrientation();
		}
		border = border.getTranspose();
		int temp;
		for (int[] face : faceIndices) {
			for (int i = 0; i < face.length / 2; i++) {
				temp = face[i];
				face[i] = face[face.length - i - 1];
				face[face.length - i - 1] = temp;
			}
		}
	}

}
