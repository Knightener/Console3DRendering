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
	
//	public ZFigure unique() {
//		ZFigure unique = new ZFigure();
//
//		boolean isntIn = true;
//		ZPixel currI = new ZPixel();
//		ZPixel currJ = new ZPixel();
//		for (int i = 0; i < figure.size(); i++) {
//			currI = figure.get(i);
//			for (int j = 0; j < unique.size(); j++) {
//				currJ = unique.get(j);
//
//				if (currI.getRight() == currJ.getRight() && currI.getDown() == currJ.getDown()) {
//					isntIn = false;
//					break;
//				}
//			}
//			if (isntIn) {
//				unique.add(currI);
//			}
//			isntIn = true;
//
//		}
//
//		return unique;
//
//	}
}
