package interfaces;

public interface VectorSpace<T> {

	public T sum(T vector);
	
	public T difference(T vector);
	
	public void translate(T vector);
	
	public void scale(double factor);
}
