package utopia.flow.structure;

import utopia.flow.structure.iterator.RichIterator;

/**
 * Interface extended by structures that index their items and can be appended / combined
 * @author Mikko Hilpinen
 * @since 9.9.2019
 * @param <A> Type of iterated item
 * @param <Repr> A concrete representation of this class
 * @param <DefaultBuilder> Builder used for building new instances of this class
 */
public interface AppendableSequence<A, Repr extends Appendable<A, Repr, DefaultBuilder>, 
	DefaultBuilder extends Builder<? extends Repr, ?, A>> extends 
	Appendable<A, Repr, DefaultBuilder>, Sequence<A, Repr, DefaultBuilder>
{
	/**
	 * Creates a new sequnce with the element prepended (to the beginning of this sequence)
	 * @param element an element
	 * @return a sequence with the element prepended
	 */
	public default Repr prepend(A element)
	{
		DefaultBuilder builder = newBuilder(size() + 1);
		builder.add(element);
		builder.add(this);
		return builder.result();
	}
	
	/**
	 * Creates a new sequnce with the element prepended (to the beginning of this sequence)
	 * @param element an element
	 * @return a sequence with the element prepended
	 */
	public default Repr prepend(RichIterable<? extends A> element)
	{
		if (element.nonEmpty())
		{
			DefaultBuilder builder = newBuilder(element.estimatedSize().map(s -> s + size()));
			builder.add(element);
			builder.add(this);
			return builder.result();
		}
		else
			return self();
	}
	
	/**
	 * Creates a new sequence with the specified element added to a certain index
	 * @param element The element that is added
	 * @param index The index the element is added to
	 * @return A sequence with the element added
	 */
	public default Repr plus(A element, int index)
	{
		if (index <= 0)
			return prepend(element);
		else if (index >= size() - 1)
			return plus(element);
		else
		{
			DefaultBuilder builder = newBuilder(size() + 1);
			RichIterator<A> iter = iterator();
			
			// Adds the beginning portion of this collection, then the specified item and finally 
			// The rest of this collection
			iter.forFirst(index, a -> builder.add(a));
			builder.add(element);
			builder.read(iter);
			
			return builder.result();
		}
	}
	
	/**
	 * Overwrites an element at a specified index
	 * @param element The new element
	 * @param index The index that is replaced
	 * @return The new version of this sequence
	 */
	public default Repr overwrite(A element, int index)
	{
		if (index < 0)
			return prepend(element);
		else if (index >= size())
			return plus(element);
		else
		{
			// Takes first items, then adds specified element and skips one item in iterator
			// Finally adds rest of iterator
			DefaultBuilder builder = newBuilder(size());
			RichIterator<A> iter = iterator();
			iter.forFirst(index, a -> builder.add(a));
			iter.skip(1);
			builder.add(element);
			builder.read(iter);
			return builder.result();
		}
	}
}
