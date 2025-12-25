package rendering2D;

import java.util.ArrayList;

import classes2D.R2Point;

public class Figure {
	/*
	 * A figure is any list of pixels to represent something (line, a cube, e.t.c.).
	 * If a list contains two pixels with identical coordinates, the color of that
	 * pixel when the figure is displayed will be the one with the greater shade
	 * (lighter if you're using dark mode, darker if you're using light mode)
	 */

	ArrayList<Pixel> figure = new ArrayList<Pixel>();

	public void add(R2Point point, int maxShade) {
		for (int j = 0; j < 4; j++) {
			figure.add(point.view(maxShade)[j]);
		}
	}

	public void add(Pixel pixel) {
		figure.add(pixel);
	}
	
	public void add(Figure figure) {
		this.figure.addAll(figure.figure);
	}
	
	public Pixel get(int index) {
		return figure.get(index);
	}
	
	public int size() {
		return figure.size();
	}
	
	// This is only really used to measure the efficiency of the drawing methods.
	public Figure unique() {
		Figure unique = new Figure();

		boolean isntIn = true;
		Pixel currI = new Pixel();
		Pixel currJ = new Pixel();
		for (int i = 0; i < figure.size(); i++) {
			currI = figure.get(i);
			for (int j = 0; j < unique.size(); j++) {
				currJ = unique.get(j);

				if (currI.getRight() == currJ.getRight() && currI.getDown() == currJ.getDown()) {
					currI.setShade(Math.max(currI.getShade(), currJ.getShade()));
					isntIn = false;
					break;
				}
			}
			if (isntIn) {
				unique.add(currI);
			}
			isntIn = true;

		}

		return unique;

	}

}
