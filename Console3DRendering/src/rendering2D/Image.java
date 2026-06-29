package rendering2D;

import java.util.Arrays;

import classes2D.R2Point;
import functionalInterfaces.RealFunction;
import other.MiscFunctions;

public class Image extends ImageBase {

	/*
	 * Note: some the functions of the same name in ZImage may draw completely
	 * different figures.
	 */
	public Image(ImageBase image) {
		super(image);
	}

	public Image(int leftEnd, int rightEnd, int upEnd, int downEnd) {
		super(leftEnd, rightEnd, upEnd, downEnd);
	}

	public Image(int[][] arr, int left, int up) {
		super(arr, left, up);
	}

	public void clear() {
		Arrays.fill(image.getArray(), 0);
	}

	public void draw(Figure figure) {
		for (Pixel pixel : figure.figure) {
			draw(pixel);
		}
	}

	public void draw(int shade, int right, int down) {
		setShade(right, down, shade);
	}
	
	public void draw(Pixel pixel) {
		draw(pixel.getShade(), pixel.getX(), pixel.getY());
	}
	
	
	public int countShade(int shade) {
		int count = 0;

		for (int value : image.getArray()) {
			if (value == shade) {
				count++;
			}
		}

		return count;
	}

	// Everything past this point is drawing methods
	
	public static Figure lineFull(R2Point p1, R2Point p2, int maxShade) {
		
		Figure line = new Figure();

		/*
		 * Chebyshev metric is used here because it minimizes the number of steps while
		 * still ensuring that the sample of points gets close enough to every grid
		 * square that the line passes
		 */
		double numSteps = Math.ceil(p1.chebyshev(p2));
		R2Point stepVector = p2.difference(p1);
		stepVector.scale(1 / numSteps);

		R2Point movingPoint = new R2Point(p1);

		for (int i = 0; i < numSteps; i++) {
			line.add(movingPoint, maxShade);
			movingPoint.translate(stepVector);

		}
		return line;
	}

	public static Figure lineFull(R2Point p1, R2Point p2) {
		return lineFull(p1, p2, ShadeHandling.MAX_SHADE);
	}
	
	/*
	 * Draws a line from p1 to p2, stopping when one of the points within the line
	 * is no longer visible within the image provided as a parameter.
	 */
	private Figure lineP1Visible(R2Point p1, R2Point p2, int maxShade) {
	
		Figure line = new Figure();
	
		double numSteps = Math.ceil(p1.chebyshev(p2));
		R2Point stepVector = p2.difference(p1);
	
		R2Point movingPoint = new R2Point(p1);
	
		stepVector.scale(1 / numSteps);
	
		for (int i = 1; i <= numSteps; i++) {
			if (isVisible(movingPoint)) {
				line.add(movingPoint, maxShade);
				movingPoint.translate(stepVector);
			} else {
				break;
			}
			
	
		}
	
		return line;
	}

	// This method went through a loooot of iterations.
	private Figure lineNeitherVisible(R2Point p1, R2Point p2, int maxShade) {
	
		double r1 = p1.getX();
		double r2 = p2.getX();
	
		double d1 = p1.getY();
		double d2 = p2.getY();
	
		/*
		 * Could be done more efficiently, but this edge case along with the one where
		 * d1 = d2 are rare enough to where it isn't worth the time to optimize.
		 */
		if (MiscFunctions.nearlyEquals(r1, r2)) {
	
			Figure line = new Figure();
	
			R2Point movingPoint = new R2Point(r1, upBound - 1);
	
			for (int i = 0; i < downBound - upBound + 1; i++) {
				line.add(movingPoint, maxShade);
				movingPoint.translate(0, 1);
			}
	
			return line;
	
		}
	
		if (MiscFunctions.nearlyEquals(d1, d2)) {
	
			Figure line = new Figure();
	
			R2Point movingPoint = new R2Point(leftBound - 1, d1);
	
			for (int i = 0; i < rightBound - leftBound + 1; i++) {
				line.add(movingPoint, maxShade);
				movingPoint.translate(1, 0);
			}
	
			return line;
	
		}
	
		double ratio1 = (d2 - d1) / (r2 - r1);
		double ratio2 = (r2 - r1) / (d2 - d1);
	
		R2Point[] borderIntersections = new R2Point[2];
	
		int currIndex = 0;
	
		/*
		 * The next four expressions intersect the line determined by p1 and p2 with the
		 * lines found by extrapolating the edges of the square. If this point lies
		 * outside the square or outside the line, it is not used (the if statement
		 * before each expression determines if the intersection point lies inside the
		 * line, and the if statement inside each expression determines if it lies
		 * inside the square.
		 * 
		 * These expressions can be derived through various manipulations of the
		 * parametric equation p1 + (p2 - p1)t , t in [0,1], which draws the line
		 * between p1 and p2.
		 */
		if (MiscFunctions.between(rightBound, r1, r2)) {
			double intersectionDown = d1 + (rightBound - r1) * ratio1;
	
			if (MiscFunctions.between(intersectionDown, upBound, downBound)) {
				borderIntersections[currIndex] = new R2Point(rightBound, intersectionDown);
				currIndex++;
			}
	
		}
	
		if (MiscFunctions.between(leftBound, r1, r2)) {
			double intersectionDown = d1 + (leftBound - r1) * ratio1;
	
			if (MiscFunctions.between(intersectionDown, upBound, downBound)) {
				borderIntersections[currIndex] = new R2Point(leftBound, intersectionDown);
				currIndex++;
			}
	
		}
	
		if (MiscFunctions.between(upBound, d1, d2)) {
			double intersectionRight = r1 + (upBound - d1) * ratio2;
	
			if (MiscFunctions.between(intersectionRight, leftBound, rightBound)) {
				R2Point intersection = new R2Point(intersectionRight, upBound);
	
				/*
				 * Accounts for a rare edge case where the line crosses a certain corner and no
				 * line gets drawn because the first and second elements borderIntersections are
				 * identical
				 */
				if (!(currIndex == 1 && borderIntersections[0].nearlyEquals(intersection))) {

					borderIntersections[currIndex] = intersection;
					currIndex++;

				}
			}
		}

		if (MiscFunctions.between(downBound, d1, d2)) {
			double intersectionRight = r1 + (downBound - d1) * ratio2;
	
			if (MiscFunctions.between(intersectionRight, leftBound, rightBound)) {
				borderIntersections[currIndex] = new R2Point(intersectionRight, downBound);
				currIndex++;
			}

		}
	
		// Checks if the square was crossed.
		if (borderIntersections[1] != null) {
			return lineFull(borderIntersections[0], borderIntersections[1], maxShade);
		}
	
		return new Figure();
	}

	// Renders only the points which are visible within image.
	public Figure line(R2Point p1, R2Point p2, int maxShade) {
	
		boolean p1Visible = isVisible(p1);
		boolean p2Visible = isVisible(p2);
	
		if (p1Visible && p2Visible) {
			return lineFull(p1, p2);
		}
		if (p1Visible) {
			return lineP1Visible(p1, p2, maxShade);
		}
		if (p2Visible) {
			return lineP1Visible(p2, p1, maxShade);
		}
		return lineNeitherVisible(p1, p2, maxShade);
	}

	public Figure line(R2Point p1, R2Point p2) {
		return line(p1, p2, ShadeHandling.MAX_SHADE);
	}

	/*
	 * Draws a ray in the given direction, stopping when the ray is no longer
	 * visible. Provided direction vector assumed to have a magnitude greater than
	 * 1.
	 */
	public Figure ray(R2Point point, R2Point direction, int maxShade) {
	
		R2Point directionCopy = new R2Point(direction);
		
		directionCopy.scale(2 * furthestOut);
		
		directionCopy.translate(point);
		
		return line(point, directionCopy, maxShade);
		
	}

	public Figure ray(R2Point point, R2Point direction) {
	
		return ray(point, direction, ShadeHandling.MAX_SHADE);
	
	}

	/*
	 * Graphs a polygonal approximation of f, with numSteps evenly spaced line
	 * segments.
	 */
	public Figure graph(RealFunction f, double start, double end, int numSteps) {
	
		if (start > end || numSteps <= 0) {
			throw new IllegalArgumentException();
		}
	
		Figure graph = new Figure();
	
		double step = (end - start) / numSteps;
	
		R2Point current = new R2Point(start, f.f(start));
		R2Point next = new R2Point(start + step, f.f(start + step));
	
		for (double i = start + 2 * step; i < end; i += step) {
			graph.add(line(current, next));
			current = new R2Point(next);
			next.set(i, f.f(i));
		}
	
		return graph;
	}

	/*
	 * Draws a line from p1 to p2 such that no two points have the same horizontal
	 * component. That is, no two points lie on the same vertical line.
	 * 
	 * Doesn't include the endpoint p2. 
	 * 
	 * Takes on the shade of p1.
	 * 
	 * p1 assumed to be further left than p2.
	 * 
	 * This was a very tedious algorithm to design. My initial idea was to move y
	 * by downDist / rightDist at every iteration then trying to distribute the
	 * remaining y distance evenly, but I had a lot of trouble figuring out how
	 * to distribute it evenly.
	 * 
	 * I was close to giving up and just draw the line by truncating floats until I
	 * realized that truncating divisions is equivalent to taking quotients.
	 * 
	 * This algorithm moves the movingPoint y every time the quotient (i* excess)
	 * / downDist increases by one. That is, every time currMod += excess exceeds
	 * rightDist. I did it this way because calculating a quotient at every
	 * iteration felt inefficient.
	 */
	private Figure lineWithoutHorizontalRepetition(Pixel p1, Pixel p2) {
	
		Figure line = new Figure();
	
		// difference
	
		int rightDist = p2.getX() - p1.getX();
		int downDif = p2.getY() - p1.getY();
	
		if (rightDist == 0 || p1.getX() > rightBound) {
			return line;
		}
	
		// direction
		int downDir = MiscFunctions.sign(downDif);
	
		// distance
		int downDist = Math.abs(downDif);
	
		int minDownStep = downDif / rightDist;
	
		// remaining y distance that will need to be distributed across iterations.
		int excess = downDist % rightDist;
	
		int currMod = excess;
		
		int visibleLength = Math.min(rightDist, rightBound - p1.getX());
		
		Pixel movingPixel = new Pixel(p1);
		
		for (int i = 0; i < visibleLength; i++) {
			
			line.add(new Pixel(movingPixel));
			
			movingPixel.incrementX(1);
			movingPixel.incrementY(minDownStep);
			
			currMod += excess;
			if (currMod >= rightDist) {
				currMod -= rightDist;
				movingPixel.incrementY(downDir);
			}
		}
	
		return line;
	}

	// Starts the line at leftBound if it crosses it
	private Figure lWHRCut(Pixel p1, Pixel p2) {

		int r1 = p1.getX();
		int r2 = p2.getX();

		if (r1 > leftBound) {
			return lineWithoutHorizontalRepetition(p1, p2);
		}
		if (r2 > leftBound) {

			int d1 = p1.getY();
			int d2 = p2.getY();

			Pixel start = new Pixel(leftBound, d1 + ((leftBound - r1) * (d2 - d1)) / (r2 - r1), p1.getShade());

			return lineWithoutHorizontalRepetition(start, p2);
		}

		return new Figure();
	}

	/*
	 * Draws a vertical line from p1 to p2, assuming p1 and p2 have the same
	 * horizontal component. Includes p1 and p2. p1 assumed to have a lower y
	 * component.
	 * 
	 * Takes on the shade of the uppermost element.
	 */
	private Figure verticalLineAuxiliary(Pixel p1, Pixel p2) {
		Figure line = new Figure();

		Pixel movingPixel = new Pixel(p1);
	
		if (upBound > p1.getY()) {
			movingPixel.setY(upBound);
		}
		int visibleLength = Math.min(p2.getY() - movingPixel.getY(), downBound - movingPixel.getY() - 1);
	
		for (int i = 0; i <= visibleLength; i++) {
			line.add(new Pixel(movingPixel));
			movingPixel.incrementY(1);
		}
	
		return line;
	}

	public Figure verticalLine(Pixel p1, Pixel p2) {
		if (p1.getY() < p2.getY()) {
			return verticalLineAuxiliary(p1, p2);
		}
		return verticalLineAuxiliary(p2, p1);
	}

	// p1 is assumed to be the leftmost point and p3 is assumed to be rightmost.
	public Figure jaggedTriangleAuxiliary(Pixel p1, Pixel p2, Pixel p3) {

		Figure triangle = new Figure();
	
			Figure line23 = lWHRCut(p2, p3);
			Figure line13 = lWHRCut(p1, p3);
			Figure line12 = lWHRCut(p1, p2);
	
			for (int i = 0; i < line12.size(); i++) {
				triangle.add(verticalLine(line13.get(i), line12.get(i)));
			}
	
			for (int i = 0; i < line23.size(); i++) {
				triangle.add(verticalLine(line13.get(i + line12.size()), line23.get(i)));
	
			}
	
			return triangle;
	}

	/*
	 * Draws a triangle between p1, p2, and p3 with only the points that are visible
	 * within image.
	 * 
	 * I did this in a way that that attempts to minimize the amount of processing
	 * which is done for the parts of the triangle which are not visible.
	 */
	public Figure jaggedTriangle(Pixel p1, Pixel p2, Pixel p3) {

		boolean r2r3 = p2.getX() <= p3.getX();
		boolean r1r3 = p1.getX() <= p3.getX();
		boolean r1r2 = p1.getX() <= p2.getX();

		/*
		 * Maybe not the cleanest way to do it, but I didn't want to go through the
		 * process of sorting to account for 6 total permutations of 3 objects.
		 * 
		 * In fact, I found that doing it the "clean" way (through comparators and
		 * lists) had a slightly higher processing time.
		 */
		if (r1r2) {
			if (r2r3) {
				return jaggedTriangleAuxiliary(p1, p2, p3);
			}
			if (r1r3) {
				return jaggedTriangleAuxiliary(p1, p3, p2);
			}
			return jaggedTriangleAuxiliary(p3, p1, p2);
		}
		if (r1r3) {
			return jaggedTriangleAuxiliary(p2, p1, p3);
		}
		if (r2r3) {
			return jaggedTriangleAuxiliary(p2, p3, p1);
		}
		return jaggedTriangleAuxiliary(p3, p2, p1);
		
	}

	public Figure jaggedTriangle(R2Point p1, R2Point p2, R2Point p3, int maxShade) {
		return jaggedTriangle(p1.truncate(maxShade), p2.truncate(maxShade), p3.truncate(maxShade));
	}

	public Figure triangle(R2Point p1, R2Point p2, R2Point p3, int maxShade) {
	
		Figure triangle = new Figure();
	
		triangle.add(jaggedTriangle(p1.truncate(maxShade), p2.truncate(maxShade), p3.truncate(maxShade)));
		triangle.add(line(p1, p2, maxShade));
		triangle.add(line(p2, p3, maxShade));
		triangle.add(line(p1, p3, maxShade));
	
		return triangle;
	}

	public Figure triangle(R2Point p1, R2Point p2, R2Point p3, int maxShade, boolean isSmooth) {
		if (isSmooth) {
			return triangle(p1,p2,p3,maxShade);
		}
		return jaggedTriangle(p1,p2,p3,maxShade);
	}

}
