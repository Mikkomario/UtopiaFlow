package utopia.flow.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import utopia.flow.structure.iterator.FlatIterator;
import utopia.flow.structure.iterator.MapIterator;
import utopia.flow.structure.iterator.MergeIterator;
import utopia.flow.structure.iterator.RepeatingIterator;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.iterator.StringCharIterator;

/**
 * Views are used for viewing certain collections / iterators. Views access items on-demand and don't keep them in 
 * memory.
 * @author Mikko Hilpinen
 * @since 27.6.2018
 * @param <T> The type of item viewed through this view
 */
public class View<T> implements RichIterable<T>
{
	// ATTIRUBTES	--------------------
	
	private Supplier<? extends RichIterator<T>> newIterator;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new view
	 * @param newIterator A supplier that provides new iterators
	 */
	public View(Supplier<? extends RichIterator<T>> newIterator)
	{
		this.newIterator = newIterator;
	}
	
	/**
	 * Flattens a two level deep collection of items into a single level deep view
	 * @param iterable An iterable element that contains iterable elements
	 * @return A flattened view of the target element
	 */
	public static <T> View<T> flatten(Iterable<? extends RichIterable<? extends T>> iterable)
	{
		return new View<>(() -> new FlatIterator<>(iterable.iterator()));
	}
	
	/**
	 * Combines multiple collections into a single flattened view
	 * @param first The first collection
	 * @param second The second collection
	 * @param more more collections
	 * @return A view of the flattened collection
	 */
	@SafeVarargs
	public static <T> View<T> flatten(RichIterable<? extends T> first, RichIterable<? extends T> second, 
			RichIterable<? extends T>... more)
	{
		return flatten(ImmutableList.withValues(first, second, more));
	}
	
	/**
	 * Creates a view of an iterable element
	 * @param iterable An iterable element
	 * @return A view for the element
	 */
	public static <T> View<T> of(RichIterable<T> iterable)
	{
		return new View<T>(iterable::iterator);
	}
	
	/**
	 * Creates a view of an array
	 * @param array An array
	 * @return A view of the specified array
	 */
	public static <T> View<T> of(T[] array)
	{
		return new View<>(() -> new ArrayIterator<>(array));
	}
	
	/**
	 * Creates a view for a string's characters
	 * @param string the viewed string
	 * @return A view of the string's characters
	 */
	public static View<Character> of(String string)
	{
		return new View<>(() -> new StringCharIterator(string));
	}
	
	/**
	 * @return A new empty view
	 */
	public static <T> View<T> empty()
	{
		return new View<>(EmptyIterator::new);
	}
	
	/**
	 * @param item An item that will be viewed
	 * @return A new view that only views the provided item
	 */
	public static <T> View<T> wrap(T item)
	{
		return new View<>(() -> new SingleItemIterator<>(item));
	}
	
	
	// IMPLEMENTED METHODS	-----------

	@Override
	public RichIterator<T> iterator()
	{
		Iterator<T> iter = this.newIterator.get();
		
		if (iter instanceof RichIterator)
			return (RichIterator<T>) iter;
		else
			return RichIterator.wrap(iter);
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Forces this view into a concrete list
	 * @return A concrete list from this view
	 */
	public ImmutableList<T> force()
	{
		return force(ListBuilder::new);
	}
	
	/**
	 * @param makeBuilder A function for producing a new builder
	 * @return A concrete version of this view collected by the specified builder
	 */
	public <R> R force(Supplier<? extends Builder<? extends R, ? , ? super T>> makeBuilder)
	{
		Builder<? extends R, ? , ? super T> builder = makeBuilder.get();
		builder.read(iterator());
		return builder.build();
	}
	
	/**
	 * Finds the item at the specific index in this view. Please note that this is an O(n) operation and if you wish 
	 * to read multiple items, it's better to force this view into a concrete list instead
	 * @param index The target index
	 * @return An item in the specified index or none if this view didn't have such an index
	 */
	public Option<T> itemAtIndex(int index)
	{
		if (index < 0)
			return Option.none();
		
		RichIterator<T> iter = iterator();
		int nextIndex = 0;
		while (nextIndex < 0 && iter.hasNext())
		{
			iter.next();
			nextIndex ++;
		}
		
		if (iter.hasNext())
			return Option.some(iter.next());
		else
			return Option.none();
	}
	
	/**
	 * Maps this view. The actual mapping is done when items are requested from the resulting view
	 * @param f A mapping function
	 * @return A view that performs mapping on-demand
	 */
	public <B> View<B> map(Function<? super T, ? extends B> f)
	{
		return new View<>(() -> new MapIterator<>(iterator(), f));
	}
	
	/**
	 * Maps this view into a single level deep view. The actual mapping is done when items are requested from the 
	 * resulting view
	 * @param f A mapping function
	 * @return A view that performs mapping on-demand
	 */
	public <B> View<B> flatMap(Function<? super T, ? extends RichIterable<? extends B>> f)
	{
		return new View<>(() -> new FlatIterator<>(new MapIterator<>(iterator(), f)));
	}
	
	/**
	 * Filters this view. The filtering is only done on-demand
	 * @param f A filter function
	 * @return A view that performs filtering on demand
	 */
	public View<T> filter(Predicate<? super T> f)
	{
		return flatMap(t -> f.test(t) ? Option.some(t) : Option.none());
	}
	
	/**
	 * Creates a new extended view
	 * @param items The first set of items to be added
	 * @param moreItems The seconds set of items to be added
	 * @param evenMoreItems More item sets
	 * @return A view that spans all of the item sets
	 */
	public View<T> plus(RichIterable<? extends T> items, RichIterable<? extends T> moreItems, 
			@SuppressWarnings("unchecked") RichIterable<? extends T>... evenMoreItems)
	{
		return flatten(ImmutableList.withValues(this, items, moreItems).plus(evenMoreItems));
	}
	
	/**
	 * Creates a new extended view
	 * @param items The first set of items to be added
	 * @return A view that spans all of the item sets
	 */
	public View<T> plus(RichIterable<? extends T> items)
	{
		return flatten(this, items);
	}
	
	/**
	 * Merges this view with another iterable instance
	 * @param other Another iterable item
	 * @param merge A function that merges two values
	 * @return A view of the merge of these two iterable items
	 */
	public <B, Merge> View<Merge> mergedWith(RichIterable<? extends B> other, 
			BiFunction<? super T, ? super B, ? extends Merge> merge)
	{
		return new View<>(() -> new MergeIterator<>(iterator(), other.iterator(), merge));
	}
	
	/**
	 * Merges this view with another iterable instance
	 * @param other Another iterable item
	 * @return A view of pairs formed from these two iterable items
	 */
	public <B> View<Pair<T, B>> mergedWith(RichIterable<? extends B> other)
	{
		return mergedWith(other, Pair::new);
	}
	
	/**
	 * Tries to collect items from this view into a list, but cancels the proces if the terminator function activates
	 * @param terminator A function that will terminate / cancel the process when it returns true
	 * @return A list of the items in this view or none if the process was terminated
	 */
	public Option<ImmutableList<T>> tryCollect(Predicate<? super T> terminator)
	{
		ListBuilder<T> buffer = new ListBuilder<>();
		for (T item : this)
		{
			if (terminator.test(item))
				return Option.none();
			else
				buffer.add(item);
		}
		
		return Option.some(buffer.build());
	}
	
	/**
	 * @return A repeating version of this view where the items are returned again and again
	 */
	public View<T> repeating()
	{
		return new View<>(() -> new RepeatingIterator<>(newIterator));
	}
	
	
	// NESTED CLASSES	-----------------
	
	private static class EmptyIterator<T> implements RichIterator<T>
	{
		@Override
		public boolean hasNext()
		{
			return false;
		}

		@Override
		public T next()
		{
			throw new NoSuchElementException("Trying to read value from an empty iterator");
		}

		@Override
		public T poll()
		{
			return null;
		}
	}
	
	private static class SingleItemIterator<T> implements RichIterator<T>
	{
		// ATTRIBUTES	----------------
		
		private T item;
		
		
		// CONSTRUCTOR	---------------
		
		public SingleItemIterator(T item)
		{
			this.item = item;
		}

		
		// IMPLEMENTED	--------------

		@Override
		public boolean hasNext()
		{
			return this.item != null;
		}

		@Override
		public T next()
		{
			T next = this.item;
			this.item = null;
			return next;
		}

		@Override
		public T poll()
		{
			return item;
		}
	}
	
	private static class ArrayIterator<T> implements RichIterator<T>
	{
		// ATTRIBUTES	-------------
		
		private T[] array;
		private int nextIndex = 0;
		
		
		// CONSTRUCTOR	-------------
		
		public ArrayIterator(T[] array)
		{
			this.array = array;
		}
		
		
		// IMPLEMENTED	------------
		
		@Override
		public boolean hasNext()
		{
			return nextIndex < array.length;
		}

		@Override
		public T next()
		{
			T next = array[nextIndex];
			nextIndex ++;
			return next;
		}

		@Override
		public T poll()
		{
			return array[nextIndex];
		}
	}
}
