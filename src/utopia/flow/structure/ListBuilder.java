package utopia.flow.structure;

import java.util.ArrayList;

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
	 */
	public ListBuilder()
	{
		super(new ArrayList<>());
	}
	
	/**
	 * Creates a new builder
	 * @param initialCapacity The intial capacity of the buffer
	 */
	public ListBuilder(int initialCapacity)
	{
		super(new ArrayList<>(initialCapacity));
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
}
