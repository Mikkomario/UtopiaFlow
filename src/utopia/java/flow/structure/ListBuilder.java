package utopia.java.flow.structure;

import java.util.ArrayList;

import utopia.java.flow.structure.iterator.RichIterator;

/**
 * This class can be used for building immutable lists in a mutable fashion. List buffers are not thread safe.
 * @author Mikko Hilpinen
 * @param <T> The type of items in the final list
 * @since 14.5.2018
 */
public class ListBuilder<T> extends Builder<ImmutableList<T>, ArrayList<T>, T>
{
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new builder
	 * @param capacity The (initial) capacity of this builder (optional)
	 */
	public ListBuilder(Option<Integer> capacity)
	{
		super(capacity.handleMap(ArrayList::new, ArrayList::new));
	}
	
	/**
	 * Creates a new builder
	 */
	public ListBuilder()
	{
		super(new ArrayList<>());
	}
	
	/**
	 * Creates a new builder
	 * @param initialCapacity The (initial) capacity of this builder
	 */
	public ListBuilder(int initialCapacity)
	{
		super(new ArrayList<>(initialCapacity));
	}
	
	/**
	 * @param item The initial item in the builder
	 * @return A builder with first value set
	 */
	public static <T> ListBuilder<T> withValue(T item)
	{
		ListBuilder<T> builder = new ListBuilder<>();
		builder.add(item);
		return builder;
	}
	
	/**
	 * @param items The initial items in the builder
	 * @return A builder with first values set
	 */
	public static <T> ListBuilder<T> withValues(RichIterable<? extends T> items)
	{
		ListBuilder<T> builder = new ListBuilder<>();
		builder.add(items);
		return builder;
	}
	
	
	// IMPLEMENTED METHODS	--------

	@Override
	protected ImmutableList<T> newResultFrom(ArrayList<T> buffer)
	{
		return new ImmutableList<>(buffer);
	}

	@Override
	protected ArrayList<T> copyBuffer(ArrayList<T> old)
	{
		return new ArrayList<>(old);
	}

	@Override
	protected void append(ArrayList<T> buffer, T newItem)
	{
		buffer.add(newItem);
	}

	@Override
	protected RichIterator<T> iteratorFrom(ArrayList<T> buffer)
	{
		return RichIterator.wrap(buffer.iterator());
	}
	
	
	// OTHER	-------------------
	
	/**
	 * Removes specified item from this builder
	 * @param item Item to remove
	 */
	public void remove(Object item)
	{
		getBuffer().remove(item);
	}
	
	/**
	 * Removes specified index from this builder
	 * @param index Index to remove
	 */
	public void removeIndex(int index)
	{
		getBuffer().remove(index);
	}
}
