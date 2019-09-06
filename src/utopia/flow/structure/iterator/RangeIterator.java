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
	private A last;
	private int defaultCompare;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new iterator
	 * @param first The first returned item
	 * @param last The last returned item
	 * @param increment An increment function (must move the value towards the last value or 
	 * else this iterator will never complete)
	 */
	public RangeIterator(A first, A last, Function<? super A, ? extends A> increment)
	{
		this.increment = increment;
		this.nextItem = first;
		this.last = last;
		this.defaultCompare = first.compareTo(last);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @param by Increment (should go towards end value)
	 * @return Iterator
	 */
	public static RangeIterator<Integer> forIntegers(int start, int end, int by)
	{
		return new RangeIterator<>(start, end, i -> i + by);
	}
	
	/**
	 * Creates a new iterator that iterates through integers
	 * @param start First returned integer
	 * @param end Last returned integer
	 * @return An iterator that traverses from start to end by changing the value by 1 each iteration
	 */
	public static RangeIterator<Integer> forIntegers(int start, int end)
	{
		if (start <= end)
			return forIntegers(start, end, 1);
		else
			return forIntegers(start, end, -1);
	}
	
	
	// IMPLEMENTED	----------------
	
	@Override
	public boolean hasNext()
	{
		int compare = nextItem.compareTo(last);
		return compare == defaultCompare || compare == 0;
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
