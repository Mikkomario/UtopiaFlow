package utopia.flow.structure.range;

import utopia.flow.structure.Option;
import utopia.flow.structure.RichIterable;
import utopia.flow.structure.View;

/**
 * Common interface for ranges that consist of integers
 * @author Mikko Hilpinen
 * @param <Repr> A concrete implementation of this interface
 * @since 11.9.2019
 */
public interface IntRange<Repr extends IntRange<Repr>> extends RangeWithBeginning<Integer>, RichIterable<Integer>
{
	// STATIC	--------------------
	
	/**
	 * @param first The first included value
	 * @param last The last included value
	 * @return An int range with specified values
	 */
	public static InclusiveIntRange inclusive(int first, int last)
	{
		return new InclusiveIntRange(first, last);
	}
	
	/**
	 * @param first The first included value
	 * @param end The ending value (exclusive)
	 * @return A new int range
	 */
	public static ExclusiveIntRange exclusive(int first, int end)
	{
		return new ExclusiveIntRange(first, end);
	}
	
	
	// ABSTRACT	--------------------
	
	/**
	 * @return The length of this range
	 */
	public int length();
	
	/**
	 * @param increment How many integers are passed each step 
	 * (Eg. 2 would iterate through every second number in this range)
	 * @return A view of this range that uses specified increment
	 */
	public View<Integer> by(int increment);
	
	/**
	 * @return A copy of this range that goes the opposite direction
	 */
	public Repr reversed();
	
	/**
	 * @param newFirst The new first value
	 * @return A copy of this range with specified first value
	 */
	public Repr withFirst(int newFirst);
	
	
	// IMPLEMENTED	----------------
	
	@Override
	public default boolean isEmpty()
	{
		return length() == 0;
	}
	
	@Override
	default boolean nonEmpty()
	{
		return !isEmpty();
	}
	
	@Override
	public default Option<Integer> estimatedSize()
	{
		return Option.some(length());
	}
	
	
	// OTHER	--------------------
	
	/**
	 * @param newLast New last value for this range (inclusive)
	 * @return A copy of this range with specified last value
	 */
	public default InclusiveIntRange withLastInclusive(int newLast)
	{
		return new InclusiveIntRange(first(), newLast);
	}
	
	/**
	 * @param newEnd New ending value for this range (exclusive)
	 * @return A copy of this range with specified end value
	 */
	public default ExclusiveIntRange withEndExclusive(int newEnd)
	{
		return new ExclusiveIntRange(first(), newEnd);
	}
}
