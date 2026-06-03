package rendering3D;

public interface Renderable {
	
	public void render();
	
	// All renderable objects have an associated observer. 
	public Observer getObserver();

}
