package utopia.java.flow.structure.range;

import java.util.function.Function;

import utopia.java.flow.structure.View;

/**
 * Common interface for all range implementations
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of compared item
 */
public interface Range<A extends Comparable<? super A>>
{
	// STATIC	--------------------
	
	/**
	 * @return An empty range
	 */
	public static <A extends Comparable<? super A>> EmptyRange<A> empty()
	{
		return new EmptyRange<>();
	}
	
	/**
	 * @param item An item to wrap
	 * @return A range that only contains the specified item
	 */
	public static <A extends Comparable<? super A>> SingleItemRange<A> wrap(A item)
	{
		return new SingleItemRange<>(item);
	}
	
	
	// ABSTRACT	--------------------
	
	/**
	 * @return Direction of this range. Positive if ascending, negative if descending, 0 if static.
	 */
	public int direction();
	
	/**
	 * @return Whether this range is empty (contains 0 items)
	 */
	public boolean isEmpty();
	
	/**
	 * @param item Searched item
	 * @return Whether this range contains the specified item
	 */
	public boolean contains(A item);
	
	/**
	 * @param move A function for increasing / decreasing the target value. 
	 * <b>Must always follow this range's direction</b>.
	 * @return A view based on this range's items
	 */
	public View<A> view(Function<? super A, ? extends A> move);
	
	
	// OTHER	--------------------
	
	/**
	 * @return Whether this range contains items
	 */
	public default boolean nonEmpty()
	{
		return !isEmpty();
	}
	
	/**
	 * @return Whether this range's start is smaller than end
	 */
	public default boolean isAscending()
	{
		return direction() > 0;
	}
	
	/**
	 * @return Whether this range's start is larger than end
	 */
	public default boolean isDescending()
	{
		return direction() < 0;
	}
}
