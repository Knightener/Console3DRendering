package zBuffered2DRendering;

import java.util.ArrayList;

import rendering2D.Figure;
import rendering2D.Pixel;

public class ZFigure {
	
	ArrayList<ZPixel> figure = new ArrayList<ZPixel>();

	public void add(ZPixel pixel) {
		figure.add(pixel);
	}
	
	public ZPixel get(int index) {
		return figure.get(index);
	}
	
	public void add(ZFigure figure) {
		this.figure.addAll(figure.figure);
	}
	
	
	public int size() {
		return figure.size();
	}
	
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
