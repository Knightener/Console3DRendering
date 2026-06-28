package building3D;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import classes2D.R2Point;
import classes3D.R3Point;
import rendering3D.Mesh;
import rendering3D.Observer;
import texturing.Texture;
import texturing.TexturePresets;

public class Graphing3D {

	/*
	 * Returns a mesh representing the graph of the function in a rectangle around
	 * the specified start sampling horizontalSteps * forwardSteps
	 * points with the specified spacing.
	 */
	public static Mesh getGraph(Observer observer, BiFunction<Double, Double, Double> function,
		double spacing, R2Point start, int horizontalSteps, int forwardSteps) {

		Texture texture = new Texture(TexturePresets.WHITE, 1);
		List<R3Point> vertices = new ArrayList<>();

		double currRight;
		double currForward;
		for (int i = 0; i < horizontalSteps; i++) {
			for (int j = 0; j < forwardSteps; j++) {
				// Although start is in xy coordinates, we are assuming it is in xz.
				currRight = start.getRight() + i * spacing;
				currForward = start.getDown() + j * spacing;

				vertices.add(new R3Point(currRight, -function.apply(currRight, currForward), currForward));

			}
		}

		Mesh graph = new Mesh(observer, vertices);

		int currIndex;
		for (int i = 0; i < horizontalSteps - 1; i++) {
			for (int j = 0; j < forwardSteps - 1; j++) {
				currIndex = forwardSteps * i + j;
				graph.createFace(texture, currIndex, currIndex + 1, currIndex + forwardSteps);
				graph.createFace(texture, currIndex + forwardSteps + 1, currIndex + 1,
					currIndex + forwardSteps);
			}
		}
		
		return graph; 
	}

	// Returns the graph of the function centered on center
	public static Mesh getCenteredGraph(Observer observer,
		BiFunction<Double, Double, Double> function, double spacing, R2Point center,
		int horizontalSteps, int forwardSteps) {

		R2Point start = new R2Point(center);
		start.translate(-horizontalSteps * spacing / 2, -forwardSteps * spacing / 2.0);

		return getGraph(observer, function, spacing, start, horizontalSteps, forwardSteps);
	}

}
