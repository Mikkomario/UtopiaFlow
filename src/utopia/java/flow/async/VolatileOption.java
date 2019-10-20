package utopia.java.flow.async;

import utopia.java.flow.structure.Option;
import utopia.java.flow.structure.RichIterable;
import utopia.java.flow.structure.iterator.RichIterator;

/**
 * This is a mutable thread-safe container that contains 0 to 1 value(s), like a mutable option
 * @author Mikko Hilpinen
 * @since 29.3.2019
 * @param <T> The type of item stored in this container
 */
public class VolatileOption<T> extends Volatile<Option<T>> implements RichIterable<T>
{
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new volatile container
	 * @param value The value that will be stored in this container. None if this container should 
	 * be left empty.
	 */
	public VolatileOption(Option<T> value)
	{
		super(value);
	}
	
	/**
	 * Creates a new volatile container
	 * @param value The value that will be stored in this container
	 * @return A volatile container with a value
	 */
	public static <T> VolatileOption<T> filled(T value)
	{
		return new VolatileOption<>(Option.some(value));
	}
	
	/**
	 * @return An empty volatile container
	 */
	public static <T> VolatileOption<T> empty()
	{
		return new VolatileOption<>(Option.none());
	}
	
	
	// IMPLEMENTED	------------------

	@Override
	public RichIterator<T> iterator()
	{
		return get().iterator();
	}
	
	@Override
	public Option<Integer> estimatedSize()
	{
		return get().estimatedSize();
	}
	
	
	// OTHER	----------------------
	
	/**
	 * Specifies a value to this volatile container
	 * @param newValue The new value to be set
	 */
	public void setOne(T newValue)
	{
		set(Option.some(newValue));
	}
	
	/**
	 * Clears any possible value from this container
	 */
	public void clear()
	{
		set(Option.none());
	}
	
	/**
	 * Removes and returns the value in this container. This container will be empty afterwards.
	 * @return The removed value. None if this container was empty.
	 */
	public Option<T> pop()
	{
		return getAndSet(Option.none());
	}
}
