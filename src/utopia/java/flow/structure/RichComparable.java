package utopia.java.flow.structure;

/**
 * This is a simple extended version of the comparable interface
 * @author Mikko Hilpinen
 * @since 4.7.2018
 * @param <T> The type of item compared to this item
 */
public interface RichComparable<T> extends Comparable<T>
{
	// OTHER	--------------------
	
	/**
	 * @param other Another item
	 * @return Whether this item is larger than the provided item
	 */
	public default boolean isLargerThan(T other)
	{
		return compareTo(other) > 0;
	}
	
	/**
	 * @param other Another item
	 * @return Whether this item is smaller than the provided item
	 */
	public default boolean isSmallerThan(T other)
	{
		return compareTo(other) < 0;
	}
}
