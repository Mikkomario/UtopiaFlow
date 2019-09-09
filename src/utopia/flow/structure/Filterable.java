package utopia.flow.structure;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * This interface is extended by collections that can be filtered and can reproduce themselves
 * @author Mikko Hilpinen
 * @param <A> Type of iterated item
 * @param <Repr> Concrete type of this collection
 * @param <DefaultBuilder> Type of builder used by this collection
 * @since 3.9.2019
 */
public interface Filterable<A, Repr extends RichIterable<? extends A>, 
	DefaultBuilder extends Builder<? extends Repr, ?, A>> extends RichIterable<A>
{
	// ABSTRACT	---------------
	
	/**
	 * @param capacity Estimated size of the new collection. None if no estimation could be provided
	 * @return A new empty builder used for creating items like this one
	 */
	public DefaultBuilder newBuilder(Option<Integer> capacity);
	
	/**
	 * @return this collection as "Repr"
	 */
	public Repr self();
	
	
	// OTHER	---------------
	
	/**
	 * @return A new empty builder used for creating items like this one
	 */
	public default DefaultBuilder newBuilder()
	{
		return newBuilder(Option.none());
	}
	
	/**
	 * @param capacity Estimated size of the new collection
	 * @return A new empty builder used for creating items like this one
	 */
	public default DefaultBuilder newBuilder(int capacity)
	{
		return newBuilder(Option.some(capacity));
	}
	
	/**
	 * @return An empty copy of this collection
	 */
	public default Repr emptyCopy()
	{
		return newBuilder(0).result();
	}
	
	/**
	 * The first n items in this iterable
	 * @param n The number of items included (at maximum)
	 * @return Up to the first n items from this iterable
	 */
	public default Repr first(int n)
	{
		return iterator().take(n, this::newBuilder);
	}
	
	/**
	 * Takes elements as long as they satisfy a predicate
	 * @param f A function used for testing the items
	 * @return The first n items that satisfy the provided predicate
	 */
	public default Repr takeWhile(Predicate<? super A> f)
	{
		return iterator().takeWhile(f, () -> newBuilder(estimatedSize()));
	}
	
	/**
	 * Drops items as long as the specified predicate is fulfilled and returns the rest of this collection
	 * @param f A predicate for dropping items from the beginning of this collection
	 * @return A collection without the first n items that satisfy the predicate
	 */
	public default Repr dropWhile(Predicate<? super A> f)
	{
		return dropWhile(f, this::newBuilder);
	}
	
	/**
	 * Splits this collection in half at a specified index
	 * @param index the split index
	 * @return First the items before the split, then the rest of the items (including specified index)
	 */
	public default Duo<Repr> splitAt(int index)
	{
		return splitAt(index, this::newBuilder);
	}
	
	/**
	 * Splits this collection in half at the first item accepted by the predicate
	 * @param find A predicate for finding split index
	 * @return First the items before the split, then the rest of the items (including search result)
	 */
	public default Duo<Repr> splitAt(Predicate<? super A> find)
	{
		return splitAt(find, this::newBuilder);
	}
	
	/**
	 * Divides this list into two categories
	 * @param f The filter function that is used for splitting this list
	 * @return The filter results. One list for accepted values and one list for not accepted values
	 */
	public default ImmutableMap<Boolean, Repr> divideBy(Predicate<? super A> f)
	{
		return divideBy(f, this::newBuilder);
	}
	
	/**
	 * Creates a filtered copy of this collection
	 * @param f a filter function
	 * @return a copy of this collection with only elements accepted by the filter
	 */
	public default Repr filter(Predicate<? super A> f)
	{
		DefaultBuilder builder = newBuilder(estimatedSize());
		forEach(a -> 
		{
			if (f.test(a))
				builder.add(a);
		});
		return builder.result();
	}
	
	/**
	 * Filters this collection, preferring items selected by the provided predicate. 
	 * However, if there are no such items, returns this collection instead
	 * @param f A filter / preference predicate
	 * @return A non-empty collection of preferred items or this
	 */
	public default Repr prefer(Predicate<? super A> f)
	{
		if (isEmpty())
			return self();
		else
		{
			Repr filtered = filter(f);
			if (filtered.isEmpty())
				return self();
			else
				return filtered;
		}
	}
	
	/**
	 * Filters this collection so that it contains only unique elements. When filtering out elements, 
	 * the leftmost unique item is preserved. For example, when using distinct on [1, 2, 3, 4, 4, 3, 1], the 
	 * resulting collection is [1, 2, 3, 4]
	 * @param equals A function that is used for checking equality between items
	 * @return A collection containing only a single instance of each unique item from this collection.
	 */
	public default Repr distinct(BiPredicate<? super A, ? super A> equals)
	{
		DefaultBuilder distinctValues = newBuilder(estimatedSize());
		forEach(item -> 
		{
			if (!distinctValues.contains(item, equals))
				distinctValues.add(item);
		});
		
		return distinctValues.result();
	}
	
	/**
	 * @return A distinct copy of this collection that doesn't contain any duplicates (uses .equals)
	 */
	public default Repr distinct()
	{
		return distinct(SAFE_EQUALS);
	}
	
	/**
	 * @param item Item to remove
	 * @param equals Function for checking equality
	 * @return A copy of this collection without specified item
	 */
	public default <B> Repr without(B item, BiPredicate<? super A, ? super B> equals)
	{
		return filter(a -> !equals.test(a, item));
	}
	
	/**
	 * @param item Item to remove
	 * @return A copy of this collection without specified item
	 */
	public default Repr without(Object item)
	{
		return filter(a -> !SAFE_EQUALS.test(a, item));
	}
	
	/**
	 * @param items Items to remove
	 * @param equals Function for checking equality
	 * @return A copy of this collection without any of specified items
	 */
	public default <B> Repr minus(RichIterable<? extends B> items, BiPredicate<? super B, ? super A> equals)
	{
		if (items.isEmpty())
			return self();
		else
			return filter(a -> !items.contains(a, equals));
	}
	
	/**
	 * @param items Items to remove
	 * @return A copy of this collection without any of specified items
	 */
	public default Repr minus(RichIterable<?> items)
	{
		return minus(items, SAFE_EQUALS);
	}
	
	/**
	 * @return This item but only if not empty
	 */
	public default Option<Repr> notEmpty()
	{
		if (isEmpty())
			return Option.none();
		else
			return Option.some(self());
	}
}
