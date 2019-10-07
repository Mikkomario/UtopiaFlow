package utopia.flow.structure;

import java.util.function.Function;

import utopia.flow.util.StringRepresentable;

/**
 * Mutable instances are used for temporarily wrapping an immutable item so that the value can be modified during 
 * closures, etc.
 * @author Mikko Hilpinen
 * @since 16.8.2018
 * @param <T> The type of item wrapped
 */
public class Mutable<T> implements Wrapper<T>, StringRepresentable
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
	
	@Override
	public String toString()
	{
		return value.toString();
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
	
	/**
	 * Updates the value in this mutable based on previous value
	 * @param f A mutating function
	 */
	public void update(Function<? super T, ? extends T> f)
	{
		value = f.apply(value);
	}
}
