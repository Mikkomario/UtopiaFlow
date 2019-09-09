package utopia.flow.structure;

import java.lang.ref.WeakReference;

import utopia.flow.structure.iterator.RichIterator;

/**
 * Used for building weakly referenced lists
 * @author Mikko Hilpinen
 * @since 3.9.2019
 * @param <A> Type of referenced item
 */
public class WeakListBuilder<A> extends Builder<WeakList<A>, ListBuilder<WeakReference<A>>, A>
{
	/**
	 * Creates a new weak list builder
	 */
	public WeakListBuilder()
	{
		super(new ListBuilder<>());
	}
	
	/**
	 * Creates a new weak list builder
	 * @param capacity Builder capacity
	 */
	public WeakListBuilder(int capacity)
	{
		super(new ListBuilder<>(capacity));
	}
	
	/**
	 * Creates a new weak list builder
	 * @param capacity Builder capacity
	 */
	public WeakListBuilder(Option<Integer> capacity)
	{
		super(new ListBuilder<>(capacity));
	}

	@Override
	protected WeakList<A> newResultFrom(ListBuilder<WeakReference<A>> buffer)
	{
		// Will not include empty references
		return new WeakList<>(buffer.result().filter(ref -> ref.get() != null));
	}

	@Override
	protected ListBuilder<WeakReference<A>> copyBuffer(ListBuilder<WeakReference<A>> old)
	{
		ListBuilder<WeakReference<A>> newBuilder = new ListBuilder<>();
		newBuilder.add(old);
		return newBuilder;
	}

	@Override
	protected void append(ListBuilder<WeakReference<A>> buffer, A newItem)
	{
		buffer.add(new WeakReference<>(newItem));
	}

	@Override
	protected RichIterator<A> iteratorFrom(ListBuilder<WeakReference<A>> buffer)
	{
		return buffer.iterator().flatMap(ref -> new Option<>(ref.get()));
	}
}
