package utopia.flow.structure.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ListBuilder;
import utopia.flow.structure.Option;
import utopia.flow.structure.RichIterable;

/**
 * This interface offers some additional methods for all iterators
 * @author Mikko Hilpinen
 * @since 14.5.2018
 * @param <T> The type of item iterated through
 */
public interface RichIterator<T> extends Iterator<T>
{
	// ABSTRACT	---------------------------
	
	/**
	 * @return Checks the next item from this iterator but won't advance.
	 */
	public T poll();
	
	
	// OTHER METHODS	-------------------
	
	/**
	 * @return The next item from this iterator. None if there is no such item.
	 */
	public default Option<T> nextOption()
	{
		if (hasNext())
			return Option.some(next());
		else
			return Option.none();
	}
	
	/**
	 * @return The next item from this iterator without advancing it. None if there are no more items.
	 */
	public default Option<T> pollOption()
	{
		if (hasNext())
			return Option.some(poll());
		else
			return Option.none();
	}
	
	/**
	 * @param n The amount of elements to be read
	 * @return The next n elements from this iterator. Less if this iterator ran out of items.
	 */
	public default ImmutableList<T> take(int n)
	{
		List<T> buffer = new ArrayList<>(n);
		for (int i = 0; i < n; i++)
		{
			nextOption().forEach(buffer::add);
		}
		
		return ImmutableList.of(buffer);
	}
	
	/**
	 * Creates a new matching iterator with mapped values. Please note that the iterators affect each other.
	 * @param transform A transform function
	 * @return A mapped version of this iterator
	 */
	public default <B> MapIterator<T, B> map(Function<? super T, ? extends B> transform)
	{
		return new MapIterator<>(this, transform);
	}
	
	/**
	 * Creates a new matching iterator with flat mapped values. Please note that the iterators affect each other
	 * @param transform A transform function
	 * @return A flat mapped version of this iterator
	 */
	public default <B> FlatIterator<B> flatMap(Function<? super T, ? extends RichIterable<B>> transform)
	{
		return new FlatIterator<>(map(transform));
	}
	
	/**
	 * @param f A predicate
	 * @return A list of the first n elements in this iterator that satisfy the provided predicate
	 */
	public default ImmutableList<T> takeWhile(Predicate<? super T> f)
	{
		ListBuilder<T> buffer = new ListBuilder<>();
		
		while (pollOption().exists(f))
		{
			nextOption().forEach(buffer::add);
		}
		
		return buffer.build();
	}
	
	/**
	 * Wraps a normal iterator into a rich iterator
	 * @param iterator A normal iterator
	 * @return A wrapped iterator
	 */
	public static <T> RichIterator<T> wrap(Iterator<? extends T> iterator)
	{
		return new Wrapper<>(iterator);
	}
	
	
	// NESTED CLASSES	---------------------
	
	/**
	 * A wrapper for normal iterators so taht they can provide access to rich iterator methods
	 * @author Mikko Hilpinen
	 * @since 14.5.2018
	 * @param <T> The type of iterated item
	 */
	static class Wrapper<T> implements RichIterator<T>
	{
		// ATTRIBUTES	---------------------
		
		private Option<T> polled = Option.none();
		private Iterator<? extends T> value;
		
		
		// CONSTRUCTOR	---------------------
		
		private Wrapper(Iterator<? extends T> value)
		{
			this.value = value;
		}
		
		
		// IMPLEMENTED METHODS	-------------
		
		@Override
		public boolean hasNext()
		{
			return polled.isDefined() || value.hasNext();
		}

		@Override
		public T next()
		{
			if (polled.isDefined())
			{
				T next = polled.get();
				polled = Option.none();
				return next;
			}
			else
				return value.next();
		}

		@Override
		public T poll()
		{
			T next = next();
			polled = Option.some(next);
			return next;
		}
	}
}
