package utopia.flow.structure.range;

/**
 * An inclusive implementation of integer range
 * @author Mikko Hilpinen
 * @since 11.9.2019
 */
public class InclusiveIntRange extends InclusiveRange<Integer> 
	implements IntRange<InclusiveIntRange>, RangeWithLength<Integer, Integer>
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
	public Integer end()
	{
		if (last() >= first())
			return last() + 1;
		else
			return last() - 1;
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

	@Override
	public Integer increase(Integer a, Integer amount)
	{
		return a + amount;
	}

	@Override
	public Integer decrease(Integer a, Integer amount)
	{
		return a - amount;
	}

	@Override
	public Integer distanceBetween(Integer min, Integer max)
	{
		return max - min;
	}

	@Override
	public Integer length()
	{
		return IntRange.super.length();
	}
}
