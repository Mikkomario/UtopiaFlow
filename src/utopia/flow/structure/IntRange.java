package utopia.flow.structure;

import utopia.flow.structure.iterator.RangeIterator;
import utopia.flow.structure.iterator.RichIterator;

/**
 * This range spans all integers between two integer values
 * @author Mikko Hilpinen
 * @since 25.7.2018
 */
public class IntRange extends Range<Integer> implements RichIterable<Integer>
{
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new range
	 * @param first The first value (inclusive)
	 * @param last The last value (inclusive)
	 */
	public IntRange(int first, int last)
	{
		super(first, last);
	}
	
	/**
	 * Creates a range with length of 1
	 * @param number A number
	 * @return A range that only contains the provided number
	 */
	public static IntRange wrap(int number)
	{
		return new IntRange(number, number);
	}
	
	
	// IMPLEMENTED	-------------------

	@Override
	public RichIterator<Integer> iterator()
	{
		return RangeIterator.forIntegers(getStart(), getEnd());
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * @return The length of this range
	 */
	public int length()
	{
		return Math.abs(getLast() - getFirst()) + 1;
	}
	
	/**
	 * @return A copy of this range in opposite order
	 */
	public IntRange reversed()
	{
		return new IntRange(getLast(), getFirst());
	}
	
	/**
	 * @param start New starting value
	 * @return A copy of this range with the new starting value. Please note that range order may 
	 * change.
	 */
	public IntRange withStart(int start)
	{
		return new IntRange(start, getEnd());
	}
	
	/**
	 * @param end New ending value
	 * @return A copy of this range with the new end value. Please note that range order may 
	 * change.
	 */
	public IntRange withEnd(int end)
	{
		return new IntRange(getStart(), end);
	}
}
