package utopia.flow.structure.range;

/**
 * Common interface for ranges with specified end / last value
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of range end point(s)
 */
public interface RangeWithEnd<A extends Comparable<? super A>> extends Range<A>
{
	// ABSTRACT	-----------------
	
	/**
	 * @return The last value <b>within</b> this range
	 */
	public A last();
}
