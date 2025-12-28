package zBuffered2DRendering;

import java.util.ArrayList;

import classes2D.R2Point;
import rendering2D.Pixel;

public class ZFigure {
	
	ArrayList<ZPixel> figure = new ArrayList<ZPixel>();

	public void add(ZPixel pixel) {
		figure.add(pixel);
	}
	
	public Pixel get(int index) {
		return figure.get(index);
	}
	
	public int size() {
		return figure.size();
	}
}
