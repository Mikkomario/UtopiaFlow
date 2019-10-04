package utopia.java.flow.structure.range;

import utopia.java.flow.structure.Option;
import utopia.java.flow.structure.View;

/**
 * Common interface for ranges that have a measurable length
 * @author Mikko Hilpinen
 * @since 26.9.2019
 * @param <A> Type of range end points
 * @param <L> Type of length measurement in this range
 */
public interface RangeWithLength<A extends Comparable<? super A>, L extends Comparable<? super L>> 
	extends DefinedRange<A>
{
	// ABSTRACT	---------------------------
	
	/**
	 * @param a original amount
	 * @param amount Amount to increase
	 * @return Increased amount
	 */
	public A increase(A a, L amount);
	
	/**
	 * @param a Original amount
	 * @param amount Amount to decrease
	 * @return Decreased amount
	 */
	public A decrease(A a, L amount);
	
	/**
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return Distance between these two values
	 */
	public L distanceBetween(A min, A max);

	
	// OTHER	--------------------------
	
	/**
	 * @return Length of this range
	 */
	public default L length()
	{
		return distanceBetween(min(), max());
	}
	
	/**
	 * @param increment Step length
	 * @return A view of this range that moves specified amount each step
	 */
	public default View<A> by(L increment)
	{
		if (isAscending())
			return view(a -> increase(a, increment));
		else
			return view(a -> decrease(a, increment));
	}
	
	/**
	 * @param other Another range
	 * @return The length of the overlapping area in these ranges. None if these ranges don't overlap
	 */
	public default Option<L> overlapLength(DefinedRange<A> other)
	{
		if (other.contains(this))
			return Option.some(length());
		else if (contains(other))
			return Option.some(distanceBetween(other.min(), other.max()));
		else if (contains(other.min()))
			return Option.some(distanceBetween(other.min(), max()));
		else if (contains(other.max()))
			return Option.some(distanceBetween(min(), other.max()));
		else
			return Option.none();
	}
	
	/**
	 * @param other Another range
	 * @return Length of area between these two ranges. None if these ranges overlap.
	 */
	public default Option<L> distanceFrom(DefinedRange<? extends A> other)
	{
		// Distance is the start of the further item - end of previous item
		if (min().compareTo(other.max()) >= 0)
			return Option.some(distanceBetween(other.max(), min()));
		else if (max().compareTo(other.min()) <= 0)
			return Option.some(distanceBetween(max(), other.min()));
		else
			return Option.none();
	}
}
