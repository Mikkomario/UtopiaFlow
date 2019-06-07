package utopia.flow.structure;

import utopia.flow.structure.iterator.FlatIterator;
import utopia.flow.structure.iterator.RichIterator;

/**
 * This list implementation is optimized for integers and is always ordered
 * @author Mikko Hilpinen
 * @since 7.6.2019
 */
public class IntSet implements RichIterable<Integer>
{
	// ATTRIBUTES	------------------
	
	/**
	 * An empty int list
	 */
	public static final IntSet EMPTY = new IntSet(ImmutableList.empty());
	
	private ImmutableList<IntRange> ranges;
	
	
	// CONSTRUCTOR	------------------
	
	private IntSet(ImmutableList<IntRange> ranges)
	{
		this.ranges = ranges;
	}
	
	/**
	 * Wraps an immutable list
	 * @param numbers Numbers list
	 * @return An int list
	 */
	public static IntSet of(ImmutableList<Integer> numbers)
	{
		return new IntSet(combine(numbers.sorted().map(IntRange::wrap)));
	}
	
	/**
	 * @param number A number
	 * @return A list that only contains that one number
	 */
	public static IntSet withValue(int number)
	{
		return new IntSet(ImmutableList.withValue(IntRange.wrap(number)));
	}
	
	/**
	 * @param first First number
	 * @param second Second number
	 * @param more More numbers
	 * @return A list that contains all of the numbers
	 */
	public static IntSet withValues(int first, int second, Integer... more)
	{
		return of(ImmutableList.withValues(first, second, more));
	}
	
	/**
	 * @param ranges A set of integer ranges
	 * @return An intset from the specified ranges
	 */
	public static IntSet ofRanges(ImmutableList<IntRange> ranges)
	{
		return new IntSet(combine(ranges.sorted()));
	}
	
	
	// IMPLEMENTED	------------------
	
	@Override
	public String toString()
	{
		return ranges.toString();
	}
	
	@Override
	public RichIterator<Integer> iterator()
	{
		return new FlatIterator<>(ranges.iterator());
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ranges == null) ? 0 : ranges.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntSet))
			return false;
		IntSet other = (IntSet) obj;
		if (ranges == null)
		{
			if (other.ranges != null)
				return false;
		}
		else if (!ranges.equals(other.ranges))
			return false;
		return true;
	}
	
	
	// ACCESSORS	------------------

	/**
	 * @return The numbers in this list as ranges
	 */
	public ImmutableList<IntRange> ranges()
	{
		return ranges;
	}
	
	
	// OTHER	---------------------
	
	/**
	 * @return The amount of numbers in this set
	 */
	public int size()
	{
		return ranges.fold(0, (total, r) -> total + r.length());
	}
	
	/**
	 * @param number A number
	 * @return Whether this set contains the number
	 */
	public boolean contains(int number)
	{
		return ranges.flatMapFirst(range -> 
		{
			// Case 1: Number at range end
			if (range.getEnd() == number)
				return Option.some(true);
			// Case 2: Not yet correct range
			else if (range.getEnd() < number)
				return Option.none();
			// Case 3: Correct range
			else
				return Option.some(range.getStart() <= number);
			
		}).getOrElse(false);
	}
	
	/**
	 * @param number A number
	 * @return A copy of this set with provided number added
	 */
	public IntSet plus(int number)
	{
		if (contains(number))
			return this;
		else
			return new IntSet(combine(ranges.plus(IntRange.wrap(number)).sorted()));
	}
	
	/**
	 * @param numbers multiple numbers
	 * @return A copy of this set with the numbers added
	 */
	public IntSet plus(RichIterable<? extends Integer> numbers)
	{
		ImmutableList<? extends Integer> newNumbers = View.of(numbers).filter(i -> !contains(i)).force();
		if (newNumbers.isEmpty())
			return this;
		else
			return new IntSet(combine(ranges.plus(newNumbers.map(IntRange::wrap)).sorted()));
	}
	
	/**
	 * @param first A number
	 * @param second Another number
	 * @param more More numbers
	 * @return A copy of this set with numbers added
	 */
	public IntSet plus(int first, int second, Integer... more)
	{
		return plus(ImmutableList.withValues(first, second, more));
	}
	
	/**
	 * @param number A number
	 * @return A copy of this set without the specified number
	 */
	public IntSet minus(int number)
	{
		Duo<ImmutableList<IntRange>> parts = ranges.splitAt(r -> r.contains(number));
		
		if (parts.getSecond().isEmpty())
			return this;
		else
		{
			return new IntSet(ImmutableList.build(b -> 
			{
				b.add(parts.getFirst());
				
				IntRange targetRange = parts.getSecond().head();
				if (targetRange.getStart().equals(number))
					b.add(new IntRange(number + 1, targetRange.getEnd()));
				else if (targetRange.getEnd().equals(number))
					b.add(new IntRange(targetRange.getStart(), number - 1));
				else
				{
					b.add(new IntRange(targetRange.getStart(), number - 1));
					b.add(new IntRange(number  + 1, targetRange.getEnd()));
				}
					
				b.add(parts.getSecond().tail());
			})); 
		}
	}
	
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
	}
}
