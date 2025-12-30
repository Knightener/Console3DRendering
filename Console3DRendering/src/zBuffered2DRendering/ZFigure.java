package zBuffered2DRendering;

import java.util.ArrayList;

import functionalInterfaces.MutatorMethod;

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
	
	public void change(MutatorMethod method) {
		for (ZPixel pixel : figure) {
			method.change(pixel);
		}
	}
}
