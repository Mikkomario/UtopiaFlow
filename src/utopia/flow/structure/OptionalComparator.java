package utopia.flow.structure;

import java.util.Comparator;

/**
 * This comparator provides an optional result which allows one to chain the comparators together
 * @author Mikko Hilpinen
 * @since 4.4.2018
 * @param <T> The type of object compared
 */
@FunctionalInterface
public interface OptionalComparator<T> extends Comparator<T>
{
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
	 * Compares a number of elements in order
	 * @param first The first set of elements
	 * @param second The second set of elements
	 * @return A comparation result for the elements (0 only if all elements are equal)
	 */
	public static <T> int compareAll(ImmutableList<? extends Comparable<? super T>> first, ImmutableList<? extends T> second)
	{
		return first.mergedWith(second).flatMapFirst(p -> 
		{
			int result = p.getFirst().compareTo(p.getSecond());
			if (result == 0)
				return Option.none();
			else
				return Option.some(result);
			
		}).getOrElse(0);
	}
}
