package utopia.flow.structure;

import java.util.Iterator;
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
}
