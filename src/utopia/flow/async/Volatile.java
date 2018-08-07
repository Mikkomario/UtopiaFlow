package utopia.flow.async;

import java.util.function.Function;

/**
 * This class is used as a wrapper for a value that may be changed from multiple threads. This class should 
 * only be used with immutable instances that optimally also have value semantics. This class is, by nature, mutable.
 * @author Mikko Hilpinen
 * @param <T> The type of the value held in this object
 * @since 7.8.2018
 */
public class Volatile<T>
{
	// ATTRIBUTES	--------------
	
	private volatile T value;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new volatile wrapper
	 * @param value The original wrapped value
	 */
	public Volatile(T value)
	{
		this.value = value;
	}

	
	// OTHER	------------------
	
	/**
	 * @return The current, up-to-date value of this container
	 */
	public synchronized T get()
	{
		return value;
	}
	
	/**
	 * Sets a new value to this container. The new value shouldn't be related to the previous value
	 * @param newValue The new value for this container
	 * @see #update(Function)
	 */
	public synchronized void set(T newValue)
	{
		this.value = newValue;
	}
	
	/**
	 * Updates the value of this container based on the previous value. The value will be updated to the return value 
	 * of the function. The current value of this container is passed to the function. This container is locked during 
	 * the operation.
	 * @param modifier A function that is used for modifying the value
	 */
	public synchronized void update(Function<? super T, ? extends T> modifier)
	{
		this.value = modifier.apply(value);
	}
}
