package utopia.flow.structure;

import java.util.function.Consumer;

import utopia.flow.structure.iterator.FlatIterator;
import utopia.flow.structure.iterator.RichIterator;
import utopia.flow.structure.range.InclusiveIntRange;
import utopia.flow.structure.range.IntRange;

/**
 * This list implementation is optimized for integers and is always ordered
 * @author Mikko Hilpinen
 * @since 7.6.2019
 */
public class IntSet implements RichIterable<Integer>, Appendable<Integer, IntSet, IntSetBuilder>
{
	// ATTRIBUTES	------------------
	
	/**
	 * An empty int list
	 */
	public static final IntSet EMPTY = new IntSet(ImmutableList.empty());
	
	private ImmutableList<InclusiveIntRange> ranges;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates an int set from an existing set of sorted ranges. Should only be used inside Flow 
	 * by appropriate builder(s)
	 * @param ranges Ranges that form this set
	 */
	protected IntSet(ImmutableList<InclusiveIntRange> ranges)
	{
		this.ranges = ranges;
	}
	
	/**
	 * Builds a new int set by using a buffer
	 * @param capacity Capacity for the buffer
	 * @param b A function to fill the buffer
	 * @return build IntSet
	 */
	public static IntSet build(int capacity, Consumer<? super IntSetBuilder> b)
	{
		IntSetBuilder newBuilder = new IntSetBuilder(capacity);
		b.accept(newBuilder);
		return newBuilder.result();
	}
	
	/**
	 * Builds a new int set by using a buffer
	 * @param b A function to fill the buffer
	 * @return build IntSet
	 */
	public static IntSet build(Consumer<? super IntSetBuilder> b)
	{
		IntSetBuilder newBuilder = new IntSetBuilder();
		b.accept(newBuilder);
		return newBuilder.result();
	}
	
	/**
	 * Wraps an immutable list
	 * @param numbers Numbers list
	 * @return An int list
	 */
	public static IntSet of(ImmutableList<Integer> numbers)
	{
		return build(numbers.size(), b -> b.add(numbers));
	}
	
	/**
	 * @param number A number
	 * @return A list that only contains that one number
	 */
	public static IntSet withValue(int number)
	{
		return new IntSet(ImmutableList.withValue(new InclusiveIntRange(number, number)));
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
	public static IntSet ofRanges(ImmutableList<InclusiveIntRange> ranges)
	{
		return new IntSet(combine(ranges.sorted()));
	}
	
	
	// IMPLEMENTED	------------------
	
	@Override
	public IntSetBuilder newBuilder(Option<Integer> capacity)
	{
		return new IntSetBuilder(capacity);
	}

	@Override
	public Option<Integer> estimatedSize()
	{
		return Option.some(size());
	}

	@Override
	public IntSet self()
	{
		return this;
	}
	
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
	public ImmutableList<InclusiveIntRange> ranges()
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
			if (range.last() == number)
				return Option.some(true);
			// Case 2: Not yet correct range
			else if (range.last() < number)
				return Option.none();
			// Case 3: Correct range
			else
				return Option.some(range.first() <= number);
			
		}).getOrElse(false);
	}
	
	/**
	 * @param number A number
	 * @return A copy of this set without the specified number
	 */
	public IntSet minus(int number)
	{
		Duo<ImmutableList<InclusiveIntRange>> parts = ranges.splitAt(r -> r.contains(number));
		
		if (parts.getSecond().isEmpty())
			return this;
		else
		{
			return new IntSet(ImmutableList.build(b -> 
			{
				b.add(parts.getFirst());
				
				InclusiveIntRange targetRange = parts.getSecond().head();
				if (targetRange.first().equals(number))
					b.add(new InclusiveIntRange(number + 1, targetRange.last()));
				else if (targetRange.last().equals(number))
					b.add(new InclusiveIntRange(targetRange.first(), number - 1));
				else
				{
					b.add(new InclusiveIntRange(targetRange.first(), number - 1));
					b.add(new InclusiveIntRange(number  + 1, targetRange.last()));
				}
					
				b.add(parts.getSecond().tail());
			})); 
		}
	}
	
	private static ImmutableList<InclusiveIntRange> combine(ImmutableList<InclusiveIntRange> ranges)
	{
		if (ranges.isEmpty())
			return ranges;
		else
			return ImmutableList.build(b -> 
			{
				InclusiveIntRange lastRange = ranges.head();
				for (InclusiveIntRange range : ranges.tail())
				{
					if (lastRange.last() < range.first() - 1)
					{
						b.add(lastRange);
						lastRange = range;
					}
					else
						lastRange = IntRange.inclusive(lastRange.first(), range.last());
				}
				b.add(lastRange);
			});
	}
}
