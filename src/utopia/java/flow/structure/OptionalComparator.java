package utopia.java.flow.structure;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;

/**
 * This comparator provides an optional result which allows one to chain the comparators together
 * @author Mikko Hilpinen
 * @since 4.4.2018
 * @param <T> The type of object compared
 */
@FunctionalInterface
public interface OptionalComparator<T> extends Comparator<T>
{
	// CONSTRUCTOR	-------------------------
	
	/**
	 * Wraps a normal comparator to an optional comparator
	 * @param c A normal comparator
	 * @return An optional comparator
	 */
	public static <T> OptionalComparator<T> wrap(Comparator<? super T> c)
	{
		return (a, b) -> 
		{
			int result = c.compare(a, b);
			if (result == 0)
				return Option.none();
			else
				return Option.some(result > 0);
		};
	}
	
	/**
	 * Creates a new comparator that uses mapped values
	 * @param f a mapping function
	 * @return An optional comparator based on the map comparison
	 */
	public static <T, B extends Comparable<? super B>> OptionalComparator<T> compareBy(Function<? super T, ? extends B> f)
	{
		return wrap((a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Creates a new comparator that uses mapped values
	 * @param f a mapping function
	 * @param comparator A comparator used for comparing the mapped values
	 * @return An optional comparator based on the map comparison
	 */
	public static <T, B> OptionalComparator<T> compareBy(Function<? super T, ? extends B> f, 
			OptionalComparator<? super B> comparator)
	{
		return (a, b) -> comparator.testIsLarger(f.apply(a), f.apply(b));
	}
	
	/**
	 * @return An optional comparator that can be used for already comparable items
	 */
	public static <T extends Comparable<? super T>> OptionalComparator<T> forComparables()
	{
		return wrap((a, b) -> a.compareTo(b));
	}
	
	/**
	 * @return A comparator to be used with collections consisting of comparable items
	 */
	public static <T extends Comparable<? super T>, C extends Iterable<? extends T>> OptionalComparator<C> forComparableCollections()
	{
		return (l1, l2) -> 
		{
			Iterator<? extends T> i1 = l1.iterator();
			Iterator<? extends T> i2 = l2.iterator();
			
			// Compares items sequentially
			while (i1.hasNext() && i2.hasNext())
			{
				int comparison = i1.next().compareTo(i2.next());
				
				if (comparison > 0)
					return Option.some(true);
				else if (comparison < 0)
					return Option.some(false);
			}
			
			// If all compared items are equal, collection with more items is larger
			if (i1.hasNext())
				return Option.some(true);
			else if (i2.hasNext())
				return Option.some(false);
			
			// If both had the same items, cannot determine which is larger
			return Option.none();
		};
	}
	
	/**
	 * This comparator compares collections by mapping their values into comparable form. Mapping is done only when 
	 * necessary.
	 * @param f A mapping function
	 * @return A comparator for collections that need mapping
	 */
	public static <T, B extends Comparable<? super B>, C extends RichIterable<? extends T>> OptionalComparator<C> 
			mapCompare(Function<? super T, ? extends B> f)
	{
		return compareBy(list -> list.view().map(f), forComparableCollections());
	}
	
	/**
	 * Creates a new comparator that compares items based on a mapping function. The mapping function may return a 
	 * collection of comparable items, which are then compared separately
	 * @param f a mapping function
	 * @return An optional comparator based on the map comparison
	 */
	public static <T, B extends Comparable<? super B>, C extends Iterable<? extends B>> OptionalComparator<T> 
			compareByCollection(Function<? super T, ? extends C> f)
	{
		return compareBy(f, forComparableCollections());
	}
	
	
	// ABSTRACT	-----------------------------
	
	/**
	 * Compares the two objects
	 * @param first The first object
	 * @param second The second object
	 * @return True if the first object is larger than the second object. False if the second object is larger than 
	 * the first object. None if neither is true.
	 */
	public Option<Boolean> testIsLarger(T first, T second);
	
	
	// IMPLEMENTED METHODS	----------------
	
	@Override
	public default int compare(T first, T second)
	{
		Option<Boolean> result = testIsLarger(first, second);
		if (result.isDefined())
			return result.get() ? 1 : -1;
		else
			return 0;
	}
	
	
	// OTHER METHODS	---------------------
	
	/**
	 * Creates a comparator that uses this comparator with another as a backup
	 * @param other Another comparator which is used for specifying the result of this comparator
	 * @return A combined comparator
	 */
	public default OptionalComparator<T> and(OptionalComparator<? super T> other)
	{
		return (a, b) -> testIsLarger(a, b).orElse(() -> other.testIsLarger(a, b));
	}
	
	/**
	 * Compares a number of elements in order
	 * @param first The first set of elements
	 * @param second The second set of elements
	 * @return A comparation result for the elements (0 only if all elements are equal)
	 */
	public static <T> int compareAll(ImmutableList<? extends Comparable<? super T>> first, ImmutableList<? extends T> second)
	{
		return first.zip(second).flatMapFirst(p -> 
		{
			int result = p.first().compareTo(p.second());
			if (result == 0)
				return Option.none();
			else
				return Option.some(result);
			
		}).getOrElse(0);
	}
}
