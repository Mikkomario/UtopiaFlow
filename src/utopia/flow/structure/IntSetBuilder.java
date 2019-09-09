package utopia.flow.structure;

import utopia.flow.structure.iterator.RichIterator;

/**
 * Used for building int sets
 * @author Mikko Hilpinen
 * @since 5.9.2019
 */
public class IntSetBuilder extends Builder<IntSet, ListBuilder<Integer>, Integer>
{
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new builder
	 */
	public IntSetBuilder()
	{
		super(new ListBuilder<>());
	}
	
	/**
	 * Creates a new builder
	 * @param capacity Capacity of this builder
	 */
	public IntSetBuilder(int capacity)
	{
		super(new ListBuilder<>(capacity));
	}
	
	/**
	 * Creates a new builder
	 * @param capacity Capacity of this builder
	 */
	public IntSetBuilder(Option<Integer> capacity)
	{
		super(new ListBuilder<>(capacity));
	}
	
	
	// IMPLEMENTED	--------------------

	@Override
	protected IntSet newResultFrom(ListBuilder<Integer> buffer)
	{
		return new IntSet(combine(buffer.result().sorted()));
	}

	@Override
	protected ListBuilder<Integer> copyBuffer(ListBuilder<Integer> old)
	{
		ListBuilder<Integer> newBuilder = new ListBuilder<>();
		newBuilder.add(old);
		return newBuilder;
	}

	@Override
	protected void append(ListBuilder<Integer> buffer, Integer newItem)
	{
		buffer.add(newItem);
	}

	@Override
	protected RichIterator<Integer> iteratorFrom(ListBuilder<Integer> buffer)
	{
		return buffer.iterator();
	}
	
	// Expects a sorted input
	private static ImmutableList<IntRange> combine(ImmutableList<Integer> numbers)
	{
		if (numbers.isEmpty())
			return ImmutableList.empty();
		else if (numbers.size() < 2)
			return ImmutableList.withValue(IntRange.wrap(numbers.head()));
		else
			return ImmutableList.build(b -> 
			{
				Integer lastStart = numbers.head();
				Integer lastNumber = lastStart;
				
				// Goes through the numbers. At each gap, creates a new range
				for (Integer number : numbers.tailView())
				{
					if (number > lastNumber + 1)
					{
						b.add(IntRange.fromTo(lastStart, lastNumber));
						lastStart = number;
					}
					lastNumber = number;
				}
				
				b.add(IntRange.fromTo(lastStart, lastNumber));
			});
	}
	
	/*
	private static ImmutableList<IntRange> combine(ImmutableList<IntRange> ranges)
	{
		if (ranges.isEmpty())
			return ranges;
		else
			return ImmutableList.build(b -> 
			{
				IntRange lastRange = ranges.head();
				for (IntRange range : ranges.tail())
				{
					if (lastRange.getEnd() < range.getStart() - 1)
					{
						b.add(lastRange);
						lastRange = range;
					}
					else
						lastRange = new IntRange(lastRange.getStart(), range.getEnd());
				}
				b.add(lastRange);
			});
	}*/
}
