package utopia.flow.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * This class can be used for building immutable lists in a mutable fashion
 * @author Mikko Hilpinen
 * @param <T> The type of items in the final list
 * @since 14.5.2018
 */
public class ListBuilder<T> implements RichIterable<T>
{
	// ATTRIBUTES	-----------------
	
	private final List<T> buffer;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new builder
	 */
	public ListBuilder()
	{
		this.buffer = new ArrayList<>();
	}
	
	/**
	 * Creates a new builder with initial capacity
	 * @param capacity The estimated size of the final list
	 */
	public ListBuilder(int capacity)
	{
		this.buffer = new ArrayList<>(capacity);
	}
	
	
	// IMPLEMENTED METHODS	--------
	
	@Override
	public RichIterator<T> iterator()
	{
		return RichIterator.wrap(this.buffer.iterator());
	}

	
	// OTHER METHODS	------------
	
	/**
	 * @return An immutable list based on this builder contents
	 */
	public ImmutableList<T> build()
	{
		return ImmutableList.of(this.buffer);
	}
	
	/**
	 * Adds an item to this builder
	 * @param item An item
	 */
	public void add(T item)
	{
		this.buffer.add(item);
	}
	
	/**
	 * Adds multiple items to this builder
	 * @param first The first item
	 * @param second The second item
	 * @param more More items
	 */
	public void add(T first, T second, @SuppressWarnings("unchecked") T... more)
	{
		this.buffer.add(first);
		this.buffer.add(second);
		for (T item : more)
		{
			this.buffer.add(item);
		}
	}
	
	/**
	 * Adds multiple items to this builder
	 * @param items The items to be added
	 */
	public void add(ImmutableList<T> items)
	{
		this.buffer.addAll(items.toMutableList());
	}
}
