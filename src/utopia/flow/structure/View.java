package utopia.flow.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
	
	private Supplier<? extends Iterator<T>> newIterator;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new view
	 * @param newIterator A supplier that provides new iterators
	 */
	public View(Supplier<? extends Iterator<T>> newIterator)
	{
		this.newIterator = newIterator;
	}
	
	/**
	 * Flattens a two level deep collection of items into a single level deep view
	 * @param iterable An iterable element that contains iterable elements
	 * @return A flattened view of the target element
	 */
	public static <T> View<T> flatten(Iterable<? extends Iterable<? extends T>> iterable)
	{
		return new View<>(() -> new FlatIterator<>(iterable.iterator()));
	}
	
	/**
	 * Combines multiple collections into a single flattened view
	 * @param first The first collection
	 * @param more more collections
	 * @return A view of the flattened collection
	 */
	@SafeVarargs
	public static <T> View<T> flatten(Iterable<? extends T> first, Iterable<? extends T>... more)
	{
		return flatten(ImmutableList.withValues(first, more));
	}
	
	/**
	 * Creates a view of an iterable element
	 * @param iterable An iterable element
	 * @return A view for the element
	 */
	public static <T> View<T> of(Iterable<T> iterable)
	{
		return new View<T>(iterable::iterator);
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
		return ImmutableList.readWith(iterator());
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
	public <B> View<B> flatMap(Function<? super T, ? extends Iterable<? extends B>> f)
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
	 * @param more More item sets to be added
	 * @return A view that spans all of the item sets
	 */
	public View<T> plus(Iterable<? extends T> items, @SuppressWarnings("unchecked") Iterable<? extends T>... more)
	{
		return flatten(ImmutableList.withValues(this, items).plus(more));
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
	}
}
