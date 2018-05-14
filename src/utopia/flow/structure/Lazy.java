package utopia.flow.structure;

import java.util.function.Supplier;

/**
 * This utility class allows lazy initialization of values
 * @author Mikko Hilpinen
 * @param <T> The type of object accessed through this class
 * @since 16.1.2018
 */
public class Lazy<T> implements Supplier<T>
{
	// ATTRIBUTES	------------------
	
	private Option<T> item = Option.none();
	private Supplier<? extends T> generator;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new lazily initialized item
	 * @param make The function that initializes the item when it is first requested
	 */
	public Lazy(Supplier<? extends T> make)
	{
		this.generator = make;
	}
	
	private Lazy(T item)
	{
		this.item = Option.some(item);
	}
	
	/**
	 * Wraps an object to look like a lazy instance
	 * @param item An item
	 * @return The wrapped item
	 */
	public static <T> Lazy<T> wrap(T item)
	{
		return new Lazy<>(item);
	}

	
	// OTHER METHODS	------------
	
	/**
	 * @return The lazily initialized item
	 */
	@Override
	public synchronized T get()
	{
		if (this.item.isDefined())
			return this.item.get();
		else
		{
			T newItem = this.generator.get();
			this.item = Option.some(newItem);
			// Forgets the generator to release memory
			this.generator = null;
			
			return newItem;
		}
	}
}
