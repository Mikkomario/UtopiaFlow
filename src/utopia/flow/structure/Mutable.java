package utopia.flow.structure;

/**
 * Mutable instances are used for temporarily wrapping an immutable item so that the value can be modified during 
 * closures, etc.
 * @author Mikko Hilpinen
 * @since 16.8.2018
 * @param <T> The type of item wrapped
 */
public class Mutable<T> implements Wrapper<T>
{
	// ATTRIBUTES	---------------
	
	private T value;
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new mutable wrapper
	 * @param value The initial value
	 */
	public Mutable(T value)
	{
		this.value = value;
	}
	
	
	// IMPLEMENTED	---------------

	@Override
	public T get()
	{
		return value;
	}

	
	// OTHER	--------------------
	
	/**
	 * Updates the value in this wrapper
	 * @param value The new value
	 */
	public void set(T value)
	{
		this.value = value;
	}
}
