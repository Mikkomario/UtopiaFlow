package utopia.flow.structure;

import java.util.function.Supplier;

/**
 * This class contains a single value that is lazily initialised. Unlike the implementation of {@link Lazy}, this 
 * class allows one to change / reset the held value. This class does not have value semantics and is not safe to 
 * pass around
 * @author Mikko Hilpinen
 * @param <T> The type of item stored in this lazy container
 * @since 22.1.2018
 */
public class MutableLazy<T>
{
	// ATTRIBUTES	-------------------
	
	private Option<T> item = Option.none();
	private Supplier<? extends T> generator;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new lazily initialized item
	 * @param generator The generator that produces the final value
	 */
	public MutableLazy(Supplier<? extends T> generator)
	{
		this.generator = generator;
	}

	
	// OTHER METHODS	--------------
	
	/**
	 * @return The lazily initialised value
	 */
	public T get()
	{
		if (this.item.isDefined())
			return this.item.get();
		else
		{
			T newItem = this.generator.get();
			this.item = Option.some(newItem);
			return newItem;
		}
	}
	
	/**
	 * Updates a new value to the lazy container
	 * @param item A new item
	 */
	public void set(T item)
	{
		this.item = Option.some(item);
	}
	
	/**
	 * Updates a new value to the container. The value is updated lazily.
	 * @param lazy A supplier for the new value
	 */
	public void set(Supplier<? extends T> lazy)
	{
		this.generator = lazy;
		reset();
	}
	
	/**
	 * Resets the value so that it will be generated again when next requested
	 */
	public void reset()
	{
		this.item = Option.none();
	}
}
