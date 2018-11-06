package utopia.flow.structure;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import utopia.flow.function.FunctionUtils;
import utopia.flow.function.ThrowingConsumer;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.iterator.SkipFirstIterator;

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
	public static final BiPredicate<Object, Object> SAFE_EQUALS = FunctionUtils.SAFE_EQUALS;
	
	
	// ABSTRACT METHODS	--------------------
	
	@Override
	public RichIterator<T> iterator();
	
	
	// STATIC	----------------------------
	
	/**
	 * Finds the maximum value from the provided items
	 * @param items The items the maximum value is searched from
	 * @return The maximum value from the list. None if list is empty.
	 */
	public static <T extends Comparable<? super T>> Option<T> maxFrom(RichIterable<T> items)
	{
		return items.max((a, b) -> a.compareTo(b));
	}
	
	/**
	 * Finds the minimum value from the provided items
	 * @param items The items the minimum value is searched from
	 * @return The minimum value from the list. None if list is empty.
	 */
	public static <T extends Comparable<? super T>> Option<T> minFrom(RichIterable<T> items)
	{
		return items.min((a, b) -> a.compareTo(b));
	}
	
	
	// OTHER METHODS	--------------------
	
	/**
	 * @return Whether this iterable is empty (contains no items)
	 */
	public default boolean isEmpty()
	{
		return !iterator().hasNext();
	}
	
	/**
	 * @return The first element in this iterable
	 * @throws NoSuchElementException If the iterable is empty
	 * @see #headOption()
	 */
	public default T head() throws NoSuchElementException
	{
		RichIterator<T> iterator = iterator();
		if (iterator.hasNext())
			return iterator.next();
		else
			throw new NoSuchElementException("This iterable is empty");
	}
	
	/**
	 * @return The first element in this iterable. None if this iterable is empty.
	 */
	public default Option<T> headOption()
	{
		return iterator().nextOption();
	}
	
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
	 * Creates a view where the first n elements are skipped
	 * @param n The amount of elements to skip
	 * @return A view of the latter portion of this iterable
	 */
	public default View<T> dropFirstView(int n)
	{
		return new View<>(() -> new SkipFirstIterator<>(iterator(), n));
	}
	
	/**
	 * @return A view of all but the first item in this iterable
	 */
	public default View<T> tailView()
	{
		return dropFirstView(1);
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
	 * Counts the number of items that satisfy the provided predicate
	 * @param f A predicate
	 * @return The number of items that satisfy the provided predicate
	 */
	public default int count(Predicate<? super T> f)
	{
		int total = 0;
		for (T element : this)
		{
			if (f.test(element))
				total ++;
		}
		
		return total;
	}
	
	/**
	 * Returns the first transformed item where the transformation is available. Similar to calling flatMap(f).first(), 
	 * except that this function doesn't transform unnecessary items.
	 * @param f The transformation function
	 * @return The first transformation result where the transformation is defined. None if none of this list's items 
	 * could be transformed.
	 */
	public default <B> Option<B> flatMapFirst(Function<? super T, Option<B>> f)
	{
		for (T item : this)
		{
			Option<B> result = f.apply(item);
			if (result.isDefined())
				return result;
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
	 * @param comparator A comparator
	 * @return The maximum value in this iterable, using the specified comparator
	 */
	public default Option<T> max(Comparator<? super T> comparator)
	{
		Option<T> first = headOption();
		if (first.isEmpty())
			return Option.none();
		else
		{
			T max = first.get();
			for (T item : tailView())
			{
				if (comparator.compare(item, max) > 0)
					max = item;
			}
			
			return Option.some(max);
		}
	}
	
	/**
	 * @param comparator A comparator
	 * @return The minimum value in this iterable, using the specified comparator
	 */
	public default Option<T> min(Comparator<? super T> comparator)
	{
		return max(comparator.reversed());
	}
	
	/**
	 * Finds the maximum value in this list based on a mapped value
	 * @param f A mapping function
	 * @return The maximum item in the list based on the mapped value. None if list is empty.
	 * @see #maxFrom(RichIterable)
	 */
	public default <K extends Comparable<? super K>> Option<T> maxBy(Function<? super T, ? extends K> f)
	{
		return max((a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Finds the minimum value in this list based on a mapped value
	 * @param f A mapping function
	 * @return The minimum item in the list based on the mapped value. None if list is empty.
	 * @see #minFrom(RichIterable)
	 */
	public default <K extends Comparable<? super K>> Option<T> minBy(Function<? super T, ? extends K> f)
	{
		return min((a, b) -> f.apply(a).compareTo(f.apply(b)));
	}
	
	/**
	 * Maps the items and finds the smallest mapped value
	 * @param map A mapping function
	 * @param comparator A comparator for mapped values
	 * @return The smallest mapped value
	 */
	public default <B> Option<B> mapMin(Function<? super T, ? extends B> map, Comparator<? super B> comparator)
	{
		Option<T> first = headOption();
		if (first.isEmpty())
			return Option.none();
		else
		{
			B min = map.apply(first.get());
			for (T next : tailView())
			{
				B mapped = map.apply(next);
				if (comparator.compare(mapped, min) < 0)
					min = mapped;
			}
			
			return Option.some(min);
		}
	}
	
	/**
	 * Maps the items and finds the smallest mapped value
	 * @param map A mapping function
	 * @return The smallest mapped value
	 */
	public default <B extends Comparable<? super B>> Option<B> mapMin(Function<? super T, ? extends B> map)
	{
		return mapMin(map, (a, b) -> a.compareTo(b));
	}
	
	/**
	 * Maps the items and finds the largest mapped value
	 * @param map A mapping function
	 * @param comparator A comparator for mapped values
	 * @return The largest mapped value
	 */
	public default <B> Option<B> mapMax(Function<? super T, ? extends B> map, Comparator<? super B> comparator)
	{
		return mapMin(map, comparator.reversed());
	}
	
	/**
	 * Maps the items and finds the largest mapped value
	 * @param map A mapping function
	 * @return The largest mapped value
	 */
	public default <B extends Comparable<? super B>> Option<B> mapMax(Function<? super T, ? extends B> map)
	{
		return mapMax(map, (a, b) -> a.compareTo(b));
	}
	
	/**
	 * Performs an operation on two lists at the same time
	 * @param other Another list
	 * @param f A function that operates on two values at the same time. The left values come from this list. 
	 * The right values come from the other list.
	 */
	public default <U> void forEachSimultaneouslyWith(Iterable<? extends U> other, BiConsumer<? super T, ? super U> f)
	{
		RichIterator<T> myIter = iterator();
		Iterator<? extends U> otherIter = other.iterator();
		
		while (myIter.hasNext() && otherIter.hasNext())
		{
			f.accept(myIter.next(), otherIter.next());
		}
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
	
	/*
	public default <U, R extends Iterable<? extends U>> R map(
			Supplier<? extends Builder<? extends R, ?, ? super U>> newBuffer, Function<? super T, ? extends U> map)
	{
		Builder<? extends R, ?, ? super U> buffer = newBuffer.get();
		forEach(item -> buffer.add(map.apply(item)));
		
		return buffer.build();
	}*/
	
	/**
	 * @return A stream from this object's contents
	 */
	public default Stream<T> stream()
	{
		return StreamSupport.stream(spliterator(), false);
	}
	
	/**
	 * @return A view for this iterable
	 */
	public default View<T> view()
	{
		return new View<>(this::iterator);
	}
}
