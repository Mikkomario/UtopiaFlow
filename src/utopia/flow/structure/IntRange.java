package utopia.flow.structure;

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
		return new RangeIterator(i -> i + 1);
	}
	
	
	// OTHER	-----------------------
	
	/**
	 * @return The length of this range
	 */
	public int length()
	{
		return getLast() - getFirst() + 1;
	}
}
