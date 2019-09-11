package utopia.flow.structure.range;

import java.util.function.Function;

import utopia.flow.structure.Option;
import utopia.flow.structure.RichComparable;
import utopia.flow.structure.View;
import utopia.flow.structure.iterator.RangeIterator;

/**
 * Range that has a specified start and end point
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of range end points
 */
public interface DefinedRange<A extends Comparable<? super A>> extends Range<A>, 
		RangeWithBeginning<A>, RangeWithEnd<A>, RichComparable<DefinedRange<A>>
{
	// IMPLEMENTED	------------------
	
	@Override
	public default int direction()
	{
		return last().compareTo(first());
	}
	
	@Override
	public default boolean isEmpty()
	{
		return false;
	}
	
	@Override
	public default boolean contains(A item)
	{
		return item.compareTo(min()) >= 0 && item.compareTo(max()) <= 0;
	}
	
	@Override
	public default View<A> view(Function<? super A, ? extends A> move)
	{
		return new View<>(() -> RangeIterator.inclusive(first(), last(), move), Option.none());
	}
	
	@Override
	public default int compareTo(DefinedRange<A> other)
	{
		int startCompare = first().compareTo(other.first());
		if (startCompare == 0)
			return last().compareTo(other.last());
		else
			return startCompare;
	}
	
	
	// OTHER	----------------------
	
	/**
	 * @return Minimum value in this range
	 */
	public default A min()
	{
		if (isDescending())
			return last();
		else
			return first();
	}
	
	/**
	 * @return Maximum value in this range
	 */
	public default A max()
	{
		if (isDescending())
			return first();
		else
			return last();
	}

	/**
	 * @param other Another range
	 * @return Whether this range contains the whole other range
	 */
	public default boolean contains(DefinedRange<? extends A> other)
	{
		return contains(other.first()) && contains(other.last());
	}
	
	/**
	 * @param other Another range
	 * @return Whether these two ranges overlap
	 */
	public default boolean overlapsWith(DefinedRange<? extends A> other)
	{
		return contains(other.first()) || contains(other.last());
	}
}
