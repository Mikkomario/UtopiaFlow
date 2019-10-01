package utopia.flow.structure.range;

import java.util.Objects;
import java.util.function.Function;

import utopia.flow.structure.Option;
import utopia.flow.structure.RichComparable;
import utopia.flow.structure.View;
import utopia.flow.structure.iterator.RangeIterator;

/**
 * Range that has an exclusive end value
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of range end points
 */
public class ExclusiveRange<A extends Comparable<? super A>> implements RangeWithBeginning<A>, 
		RangeWithExclusiveEnd<A>, RichComparable<ExclusiveRange<A>>
{
	// ATTRIBUTES	--------------
	
	private A first;
	private A end;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new range
	 * @param first First value in range (inclusive)
	 * @param end End value (exclusive)
	 */
	public ExclusiveRange(A first, A end)
	{
		this.first = first;
		this.end = end;
	}
	
	
	// IMPLEMENTED	--------------
	
	@Override
	public A first()
	{
		return first;
	}
	
	@Override
	public A end()
	{
		return end;
	}
	
	@Override
	public int direction()
	{
		return end().compareTo(first());
	}
	
	@Override
	public boolean isEmpty()
	{
		return direction() == 0;
	}
	
	@Override
	public boolean contains(A item)
	{
		if (isAscending())
			return item.compareTo(first()) >= 0 && item.compareTo(end()) < 0;
		else
			return item.compareTo(first()) <= 0 && item.compareTo(end()) > 0;
	}
	
	@Override
	public View<A> view(Function<? super A, ? extends A> move)
	{
		return new View<>(() -> RangeIterator.exclusive(first(), end(), move), Option.none());
	}
	
	@Override
	public int compareTo(ExclusiveRange<A> other)
	{
		int startCompare = first().compareTo(other.first());
		if (startCompare == 0)
			return end().compareTo(other.end());
		else
			return startCompare;
	}
	
	@Override
	public String toString()
	{
		return first + " until " + end;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(end, first);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ExclusiveRange))
			return false;
		ExclusiveRange<?> other = (ExclusiveRange<?>) obj;
		return Objects.equals(end, other.end) && Objects.equals(first, other.first);
	}
}
