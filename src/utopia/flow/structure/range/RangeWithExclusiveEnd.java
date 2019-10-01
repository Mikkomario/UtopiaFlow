package utopia.flow.structure.range;

/**
 * Common interface for ranges with an exclusive ending value
 * @author Mikko Hilpinen
 * @since 27.9.2019
 * @param <A> Type of range points
 */
public interface RangeWithExclusiveEnd<A extends Comparable<? super A>> extends Range<A>
{
	/**
	 * @return The ending value of this range (exlusive)
	 */
	public A end();
}
