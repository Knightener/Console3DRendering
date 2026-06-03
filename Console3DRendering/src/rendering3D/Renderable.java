package rendering3D;

public interface Renderable {
	
	public void render();
	
	public void updatePerspective();
	
	// All renderable objects have an associated observer. 
	public Observer getObserver();

}
