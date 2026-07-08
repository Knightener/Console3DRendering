package building3D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import classes3D.R3Point;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;

public class Graphing3D {

	/*
	 * Returns a mesh representing the graph of the function in a rectangle around
	 * the specified start sampling horizontalSteps * forwardSteps points with the
	 * specified spacing.
	 */
	public static Mesh getGraph(Observer observer, BiFunction<Double, Double, Double> function,
		double xStart, double xEnd, double zStart, double zEnd, int xDivisions, int yDivisions) {
		BiFunction<Double, Double, R3Point> parametricFunction = (s, t) -> new R3Point(s,
			-function.apply(s, t), t);
		return getParametricGraph(observer,parametricFunction, xStart, xEnd, zStart, zEnd, xDivisions, yDivisions);
	}
	
	/*
	 * Returns the graph of the specified parametric function ([sStart,sEnd] X
	 * [tStart, tEnd]-> R3).
	 */
	public static Mesh getParametricGraph(Observer observer,
		BiFunction<Double, Double, R3Point> function, double sStart, double sEnd, double tStart,
		double tEnd, int sDivisions, int tDivisions) {
		if (sStart > sEnd || tStart > tEnd || sDivisions <= 0 || tDivisions <= 0) {
			throw new IllegalArgumentException();
		}

		double sStep = (sEnd - sStart) / sDivisions;
		double tStep = (tEnd - tStart) / tDivisions;

		Texture texture = new Texture(TexturePresets.WHITE, 1);
		List<R3Point> vertices = new ArrayList<>();

		for (int i = 0; i <= sDivisions; i++) {
			for (int j = 0; j <= tDivisions; j++) {
				vertices.add(function.apply(sStart + i * sStep, tStart + j * tStep));
			}
		}
		Mesh graph = new Mesh(observer, vertices);

		int currIndex;
		for (int i = 0; i < sDivisions; i++) {
			for (int j = 0; j < tDivisions; j++) {
				currIndex = (tDivisions + 1) * i + j;
				graph.createFace(texture, currIndex, currIndex + 1, currIndex + tDivisions + 1);
				graph.createFace(texture, currIndex + tDivisions + 2, currIndex + 1,
					currIndex + tDivisions + 1);
			}
		}

		return graph;

	}

}
