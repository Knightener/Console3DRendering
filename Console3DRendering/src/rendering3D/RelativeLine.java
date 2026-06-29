package rendering3D;

import classes3D.R3Point;
import rendering2D.ShadeHandling;

public class RelativeLine extends RelativeComponent {

	/*
	 * Lines require a second integer to specify the shade of it's border when
	 * rendered. This is to make them clearly visible amongst triangles
	 */
	int shade;
	private int borderShade;

	private R3Point pointA;
	private R3Point pointB;
	
	private R3Point perceivedA;
	private R3Point perceivedB;

	public RelativeLine(R3Point pointA, R3Point pointB, int shade, int borderShade, Observer observer) {

		super(observer);
		
		this.pointA = pointA;
		this.pointB = pointB;
		
		this.shade = shade;
		this.borderShade = borderShade;
		
		updatePerspective();
	}

	public RelativeLine(R3Point pointA, R3Point pointB, Observer observer) {
		this(pointA, pointB, ShadeHandling.MAX_SHADE, 0, observer);
	}

	public void render() {
		observer.lineDefault(perceivedA, perceivedB, shade, borderShade);
	}

	public void determineMostAndLeastForward() {
		
		leastForward = Math.min(perceivedA.getZ(),perceivedB.getZ());
		mostForward = Math.max(perceivedA.getZ(),perceivedB.getZ());
	}

	public void updatePerspective() {
		perceivedA = observer.perspective(pointA);
		perceivedB = observer.perspective(pointB);
		
		determineMostAndLeastForward();
	}
}
