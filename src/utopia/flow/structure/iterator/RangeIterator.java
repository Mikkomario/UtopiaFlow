package utopia.flow.structure.iterator;

import java.util.function.Function;

/**
 * Used for iterating over a range of items
 * @author Mikko Hilpinen
 * @since 6.9.2019
 * @param <A> Type of iterated item
 */
public class RangeIterator<A extends Comparable<? super A>> implements RichIterator<A>
{
	// ATTRIBUTES	----------------
	
	private Function<? super A, ? extends A> increment;
	private A nextItem;
	private A end;
	private boolean isInclusive;
	private int direction;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new iterator
	 * @param first The first returned item
	 * @param end The end item
	 * @param increment An increment function (must move the value towards the last value or 
	 * else this iterator will never complete)
	 * @param inclusive Whether the end value should be included
	 */
	public RangeIterator(A first, A end, Function<? super A, ? extends A> increment, boolean inclusive)
	{
		this.increment = increment;
		this.nextItem = first;
		this.end = end;
		this.direction = end.compareTo(first);
		this.isInclusive = inclusive;
	}
	
	/**
	 * Creates a new iterator
	 * @param first First returned item
	 * @param last Last returned item
	 * @param increment An increment function (must move the value towards the last value or 
	 * else this iterator will never complete)
	 * @return A new iterator
	 */
	public static <A extends Comparable<? super A>> RangeIterator<A> inclusive(A first, A last, 
			Function<? super A, ? extends A> increment)
	{
		return new RangeIterator<>(first, last, increment, true);
	}
	
	/**
	 * Creates a new iterator
	 * @param first First returned item
	 * @param end end value (exclusive)
	 * @param increment An increment function (must move the value towards the end value or 
	 * else this iterator will never complete)
	 * @return A new iterator
	 */
	public static <A extends Comparable<? super A>> RangeIterator<A> exclusive(A first, A end, 
			Function<? super A, ? extends A> increment)
	{
		return new RangeIterator<>(first, end, increment, false);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @param by Increment (towards end value)
	 * @param inclusive Whether the end value should be included
	 * @return Iterator
	 */
	public static IntRangeIterator forIntegers(int start, int end, int by, boolean inclusive)
	{
		if (inclusive)
			return IntRangeIterator.fromTo(start, end, by);
		else
			return IntRangeIterator.fromUntil(start, end, by);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @param by Increment (towards end value)
	 * @return Iterator
	 */
	public static IntRangeIterator forIntegersInclusive(int start, int end, int by)
	{
		return forIntegers(start, end, by, true);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @param by Increment (towards end value)
	 * @return Iterator
	 */
	public static IntRangeIterator forIntegersExclusive(int start, int end, int by)
	{
		return forIntegers(start, end, by, false);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @param inclusive Whether the end value should be included
	 * @return An iterator that traverses from start to end by changing the value by 1 each iteration
	 */
	public static IntRangeIterator forIntegers(int start, int end, boolean inclusive)
	{
		return forIntegers(start, end, 1, inclusive);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @return An iterator that traverses from start to end by changing the value by 1 each iteration
	 */
	public static IntRangeIterator forIntegersInclusive(int start, int end)
	{
		return forIntegers(start, end, 1, true);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @return An iterator that traverses from start to end by changing the value by 1 each iteration
	 */
	public static IntRangeIterator forIntegersExclusive(int start, int end)
	{
		return forIntegers(start, end, 1, false);
	}
	
	
	// IMPLEMENTED	----------------
	
	@Override
	public boolean hasNext()
	{
		int compare = end.compareTo(nextItem);
		if (isInclusive)
			return compare == direction || compare == 0;
		else
			return compare != 0 && compare == direction;
	}

	@Override
	public A next()
	{
		A next = nextItem;
		nextItem = increment.apply(next);
		return next;
	}

	@Override
	public A poll()
	{
		return nextItem;
	}
}
