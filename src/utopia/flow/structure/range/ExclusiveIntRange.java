package utopia.flow.structure.range;

import utopia.flow.structure.View;
import utopia.flow.structure.iterator.RangeIterator;
import utopia.flow.structure.iterator.RichIterator;

/**
 * An exclusive implementation of int range
 * @author Mikko Hilpinen
 * @since 11.9.2019
 */
public class ExclusiveIntRange extends ExclusiveRange<Integer> implements IntRange<ExclusiveIntRange>
{
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new range
	 * @param first First value in range (inclusive)
	 * @param end Ending value (exclusive)
	 */
	public ExclusiveIntRange(int first, int end)
	{
		super(first, end);
	}
	
	
	// IMPLEMENTED	-----------------

	@Override
	public RichIterator<Integer> iterator()
	{
		return RangeIterator.forIntegersExclusive(first(), end());
	}

	@Override
	public int length()
	{
		return Math.abs(end() -  first());
	}

	@Override
	public View<Integer> by(int increment)
	{
		return new View<>(() -> RangeIterator.forIntegersExclusive(first(), end(), increment), 
				length());
	}

	@Override
	public ExclusiveIntRange reversed()
	{
		if (isDescending())
			return new ExclusiveIntRange(end() + 1, first() + 1);
		else
			return new ExclusiveIntRange(end() - 1, first() - 1);
	}

	@Override
	public ExclusiveIntRange withFirst(int newFirst)
	{
		return new ExclusiveIntRange(newFirst, end());
	}
}
