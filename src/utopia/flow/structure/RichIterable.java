package utopia.flow.structure;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import utopia.flow.function.ThrowingConsumer;

/**
 * Classes implementing this interface will have access to methods additional to normal iterables
 * @author Mikko Hilpinen
 * @since 14.5.2018
 * @param <T> The type of item in this iterable
 */
public interface RichIterable<T> extends Iterable<T>
{
	// ATTRIBUTES	------------------------
	
	/**
	 * A check for whether two objects are equal. Null safe
	 */
	public static final BiPredicate<Object, Object> SAFE_EQUALS = (a, b) -> a == null ? b == null : a.equals(b);
	
	
	// ABSTRACT METHODS	--------------------
	
	@Override
	public RichIterator<T> iterator();
	
	
	// OTHER METHODS	--------------------
	
	/**
	 * The first n items in this iterable
	 * @param n The number of items included (at maximum)
	 * @return Up to the first n items from this iterable
	 */
	public default ImmutableList<T> first(int n)
	{
		return iterator().take(n);
	}
	
	/**
	 * Takes elements as long as they satisfy a predicate
	 * @param f A function used for testing the items
	 * @return The first n items that satisfy the provided predicate
	 */
	public default ImmutableList<T> takeWhile(Predicate<? super T> f)
	{
		return iterator().takeWhile(f);
	}
	
	/**
	 * Checks whether this list contains an element that satisfies the predicate
	 * @param f a predicate
	 * @return Is the predicate true for any of the elements in this list. False if empty.
	 */
	public default boolean exists(Predicate<? super T> f)
	{
		for (T element : this)
		{
			if (f.test(element))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks whether this collection contains the provided item
	 * @param item An item
	 * @return whether the item is contained within this collection
	 */
	public default boolean contains(Object item)
	{
		return exists(o -> SAFE_EQUALS.test(o, item));
	}
	
	/**
	 * Checks if a predicate is true for all elements in the list
	 * @param f a predicate
	 * @return Is the predicate true for all of the elements in this list. True if empty.
	 */
	public default boolean forAll(Predicate<? super T> f)
	{
		return !exists(f.negate());
	}
	
	/**
	 * Finds the first element that satisfies the predicate
	 * @param f A predicate
	 * @return The first element that satisfies the predicate
	 */
	public default Option<T> find(Predicate<? super T> f)
	{
		for (T element : this)
		{
			if (f.test(element))
				return Option.some(element);
		}
		return Option.none();
	}
	
	/**
	 * Performs a fold operation over this list, going from left to right
	 * @param start The starting value
	 * @param f A function that folds items into the result value
	 * @return The resulting value
	 */
	public default <B> B fold(B start, BiFunction<? super B, ? super T, ? extends B> f)
	{
		B result = start;
		for (T item : this)
		{
			result = f.apply(result, item);
		}
		
		return result;
	}
	
	/**
	 * Performs a throwing operation on each of the elements in this collection. Stops iterating on the first exception.
	 * @param f The function that is performed for each element in the list
	 * @throws Exception The first exception thrown by the function
	 */
	public default <E extends Exception> void forEachThrowing(ThrowingConsumer<? super T, ? extends E> f) throws E
	{
		for (T item : this)
		{
			f.accept(item);
		}
	}
	
	/**
	 * @return A stream from this object's contents
	 */
	public default Stream<T> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}
}