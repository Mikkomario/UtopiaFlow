package utopia.flow.structure.range;

import utopia.flow.structure.View;
import utopia.flow.structure.iterator.RangeIterator;
import utopia.flow.structure.iterator.RichIterator;

/**
 * An inclusive implementation of integer range
 * @author Mikko Hilpinen
 * @since 11.9.2019
 */
public class InclusiveIntRange extends InclusiveRange<Integer> implements IntRange<InclusiveIntRange>
{
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new range
	 * @param first First included number
	 * @param last Last included number
	 */
	public InclusiveIntRange(int first, int last)
	{
		super(first, last);
	}
	
	
	// IMPLEMENTED	-------------------

	@Override
	public RichIterator<Integer> iterator()
	{
		return RangeIterator.forIntegersInclusive(first(), last());
	}

	@Override
	public int length()
	{
		return Math.abs(last() - first()) + 1;
	}

	@Override
	public View<Integer> by(int increment)
	{
		return new View<>(() -> RangeIterator.forIntegersInclusive(first(), last(), increment), 
				length());
	}

	@Override
	public InclusiveIntRange reversed()
	{
		return new InclusiveIntRange(last(), first());
	}

	@Override
	public InclusiveIntRange withFirst(int newFirst)
	{
		return new InclusiveIntRange(newFirst, last());
	}

	@Override
	public boolean isEmpty()
	{
		return super.isEmpty();
	}
}
