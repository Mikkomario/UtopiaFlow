package utopia.java.flow.structure;

import utopia.java.flow.structure.iterator.RichIterator;

/**
 * Used for building options. Please note that only lastly added item will be included
 * @author Mikko Hilpinen
 * @since 3.9.2019
 * @param <A> Type of added item
 */
public class OptionBuilder<A> extends Builder<Option<A>, Mutable<Option<A>>, A>
{
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new builder
	 */
	public OptionBuilder()
	{
		super(new Mutable<>(Option.none()));
	}
	
	
	// IMPLEMENTED	-----------------

	@Override
	protected Option<A> newResultFrom(Mutable<Option<A>> buffer)
	{
		return buffer.get();
	}

	@Override
	protected Mutable<Option<A>> copyBuffer(Mutable<Option<A>> old)
	{
		return new Mutable<>(old.get());
	}

	@Override
	protected void append(Mutable<Option<A>> buffer, A newItem)
	{
		buffer.set(Option.some(newItem));
	}

	@Override
	protected RichIterator<A> iteratorFrom(Mutable<Option<A>> buffer)
	{
		return buffer.get().iterator();
	}
}
