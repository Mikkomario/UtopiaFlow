package utopia.flow.structure.iterator;

/**
 * A more efficient implementation of RangeIterator for integer ranges
 * @author Mikko Hilpinen
 * @since 27.9.2019
 */
public class IntRangeIterator implements RichIterator<Integer>
{
	// ATTRIBUTES	-------------------
	
	private int next;
	private int endExclusive;
	private int step;
	private boolean isAscending;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new iterator
	 * @param first First returned number
	 * @param endExclusive The point at which this iterator will no longer return values (exclusive)
	 * @param step Amount of increase / decrease per iteration (sign doesn't matter)
	 */
	public IntRangeIterator(int first, int endExclusive, int step)
	{
		isAscending = endExclusive >= first;
		this.next = first;
		this.endExclusive = endExclusive;
		this.step = isAscending ? Math.abs(step) : -Math.abs(step);
	}
	
	/**
	 * Creates a new iterator that includes the end value
	 * @param first First returned value
	 * @param last Last returned value
	 * @param by Increment by each iteration (sign doesn't matter)
	 * @return A new iterator
	 */
	public static IntRangeIterator fromTo(int first, int last, int by)
	{
		if (last >= first)
			return new IntRangeIterator(first, last + 1, by);
		else
			return new IntRangeIterator(last, first - 1, by);
	}
	
	/**
	 * Creates a new iterator that includes the end value
	 * @param first First returned value
	 * @param last Last returned value
	 * @return A new iterator
	 */
	public static IntRangeIterator fromTo(int first, int last)
	{
		return fromTo(first, last, 1);
	}
	
	/**
	 * Creates a new iterator that excludes the end value
	 * @param first First returned value
	 * @param end Ending value (excluded)
	 * @param by Increment by each iteration (sign doesn't matter)
	 * @return A new iterator
	 */
	public static IntRangeIterator fromUntil(int first, int end, int by)
	{
		return new IntRangeIterator(first, end, by);
	}
	
	/**
	 * Creates a new iterator that excludes the end value
	 * @param first First returned value
	 * @param end Ending value (excluded)
	 * @return A new iterator
	 */
	public static IntRangeIterator fromUntil(int first, int end)
	{
		return fromUntil(first, end, 1);
	}
	
	
	// IMPLEMENTED	-------------------

	@Override
	public boolean hasNext()
	{
		if (isAscending)
			return next < endExclusive;
		else
			return next > endExclusive;
	}

	@Override
	public Integer next()
	{
		int r = next;
		next += step;
		return r;
	}

	@Override
	public Integer poll()
	{
		return next;
	}
}
