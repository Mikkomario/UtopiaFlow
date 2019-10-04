package utopia.java.flow.structure.range;

/**
 * Common interface for ranges that have a specified starting value
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of range end point(s)
 */
public interface RangeWithBeginning<A extends Comparable<? super A>> extends Range<A>
{
	// ABSTRACT	------------------
	
	/**
	 * @return The first value inside this range
	 */
	public A first();
}
