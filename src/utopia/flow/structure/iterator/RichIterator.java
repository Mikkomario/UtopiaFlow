package utopia.flow.structure.iterator;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import utopia.flow.structure.Builder;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ListBuilder;
import utopia.flow.structure.Option;
import utopia.flow.structure.RichIterable;

/**
 * This interface offers some additional methods for all iterators
 * @author Mikko Hilpinen
 * @since 14.5.2018
 * @param <A> The type of item iterated through
 */
public interface RichIterator<A> extends Iterator<A>
{
	// ABSTRACT	---------------------------
	
	/**
	 * @return Checks the next item from this iterator but won't advance.
	 */
	public A poll();
	
	
	// OTHER METHODS	-------------------
	
	/**
	 * @return The next item from this iterator. None if there is no such item.
	 */
	public default Option<A> nextOption()
	{
		if (hasNext())
			return Option.some(next());
		else
			return Option.none();
	}
	
	/**
	 * @return The next item from this iterator without advancing it. None if there are no more items.
	 */
	public default Option<A> pollOption()
	{
		if (hasNext())
			return Option.some(poll());
		else
			return Option.none();
	}
	
	/**
	 * @param n The amount of elements to be read
	 * @param makeBuilder A function for producing the final collection builder.
	 * @return The next n elements from this iterator. Less if this iterator ran out of items.
	 */
	public default <R> R take(int n, 
			Function<? super Integer, ? extends Builder<? extends R, ?, ? super A>> makeBuilder)
	{
		Builder<? extends R, ?, ? super A> buffer = makeBuilder.apply(Math.max(n, 0));
		for (int i = 0; i < n; i++)
		{
			Option<A> item = nextOption();
			if (item.isEmpty())
				return buffer.result();
			else
				buffer.add(item.get());
		}
		return buffer.result();
	}
	
	/**
	 * @param n The amount of elements to be read
	 * @return The next n elements from this iterator. Less if this iterator ran out of items.
	 */
	public default ImmutableList<A> take(int n)
	{
		return take(n, ListBuilder::new);
	}
	
	/**
	 * Creates a new matching iterator with mapped values. Please note that the iterators affect each other.
	 * @param transform A transform function
	 * @return A mapped version of this iterator
	 */
	public default <B> MapIterator<A, B> map(Function<? super A, ? extends B> transform)
	{
		return new MapIterator<>(this, transform);
	}
	
	/**
	 * Creates a new matching iterator with flat mapped values. Please note that the iterators affect each other
	 * @param transform A transform function
	 * @return A flat mapped version of this iterator
	 */
	public default <B> FlatIterator<B> flatMap(Function<? super A, ? extends RichIterable<B>> transform)
	{
		return new FlatIterator<>(map(transform));
	}
	
	/**
	 * @param f A predicate
	 * @param makeBuilder A function for producing a builder for the final collection
	 * @return A collection of the first n elements in this iterator that satisfy the provided predicate
	 */
	public default <R> R takeWhile(Predicate<? super A> f, 
			Supplier<? extends Builder<? extends R, ?, ? super A>> makeBuilder)
	{
		Builder<? extends R, ?, ? super A> builder = makeBuilder.get();
		while (pollOption().exists(f))
		{
			nextOption().forEach(builder::add);
		}
		return builder.result();
	}
	
	/**
	 * @param f A predicate
	 * @return A list of the first n elements in this iterator that satisfy the provided predicate
	 */
	public default ImmutableList<A> takeWhile(Predicate<? super A> f)
	{
		return takeWhile(f, ListBuilder::new);
	}
	
	/**
	 * Calls function 'f' for the first 'n' read items (or less if this iterator runs out of items 
	 * before n items are reached)
	 * @param n The maximum number of items read
	 * @param f A function called for the read items
	 */
	public default void forFirst(int n, Consumer<? super A> f)
	{
		int itemsRead = 0;
		while (hasNext() && itemsRead < n)
		{
			f.accept(next());
			itemsRead += 1;
		}
	}
	
	/**
	 * Skips the next 'n' items in this iterator
	 * @param n The number of items to be skipped
	 */
	public default void skip(int n)
	{
		int itemsSkipped = 0;
		while (hasNext() && itemsSkipped < n)
		{
			next();
		}
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
