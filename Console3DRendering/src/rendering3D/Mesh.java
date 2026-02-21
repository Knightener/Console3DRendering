package rendering3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import classes2D.IntPoint;
import classes3D.R3Point;
import other.MiscFunctions;
import texturing.Texture;

public class Mesh extends ObserverDependant {

	/*
	 * This class differs from form in that it can only store faces, and all faces
	 * must be adjacent to only 1 or 2 faces (unless it only consists of one face,
	 * in which case it can have 0 neighbors). Unlike form, it eliminates a lot of
	 * redundant calculations that are found from adjacent faces.
	 */

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
	private List<IntPoint> associatedFace;

	/*
	 * Edges (as indices of the vertices list) that are only adjacent to one
	 * singular polygon.
	 */
	private List<IntPoint> border;

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
		super(observer);
		this.vertices = vertices;

		relativeVertices = new ArrayList<R3Point>();
		faceIndices = new ArrayList<int[]>();
		faces = new ArrayList<MeshPolygon>();
		associatedFace = new ArrayList<IntPoint>();
		border = new ArrayList<IntPoint>();

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

		MeshPolygon face = new MeshPolygon(getObserver(), texture, faceVertices);

		ArrayList<R3Point> perceivedFaceVertices = new ArrayList<R3Point>();

		for (int i : indices) {
			perceivedFaceVertices.add(relativeVertices.get(i));
		}

		face.setPerceivedPoints(perceivedFaceVertices);

		faces.add(face);

		for (int i = 0; i < indices.length; i++) {
			if (associatedFace.get(indices[i]) == null) {
				associatedFace.set(indices[i], new IntPoint(faceIndices.size() - 1, i));
			}
		}
	}

	private void addToBorder(int... indices) {
		for (int k = 0; k < indices.length; k++) {
			MiscFunctions.xorAdd(border, new IntPoint(indices[k], indices[(k + 1) % indices.length]));
		}
	}

	// Returns true if face was successfully added and false otherwise.
	private boolean createFaceAux(Texture texture, int... indices) {

		int length = indices.length;
		IntPoint curr;
		for (int i = 0; i < border.size(); i++) {
			curr = border.get(i);
			for (int j = 0; j < length; j++) {
				// Overlapping edge found
				if (curr.getRight() == indices[j] && curr.getDown() == indices[(j + 1) % length]) {
					unrestrictedCreateFace(texture, indices);
					addToBorder(indices);
					return true;
				}
			}
		}
		return false;
	}

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

		throw new IllegalArgumentException("Face is not adjacent to any polygon.");
	}

	public void updatePerspective() {

		for (MeshPolygon face : faces) {
			face.updatePlaneVectors();
			face.findUVVariables();
		}

		IntPoint index;

		for (int i = 0; i < vertices.size(); i++) {
			index = associatedFace.get(i);
			if (index != null) {
				relativeVertices.get(i).set(faces.get(index.getRight()).viewedVertex(index.getDown()));
			}
		}
	}
	
	
	
	
	
}
