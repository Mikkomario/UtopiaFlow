package utopia.flow.structure;

import java.util.function.Function;

import utopia.flow.structure.iterator.RangeIterator;
import utopia.flow.util.StringRepresentable;

/**
 * Ranges specify a start and end value and contain all values in between
 * @author Mikko Hilpinen
 * @since 25.7.2018
 * @param <T> The type of the objects contained in this range
 */
public class Range<T extends Comparable<? super T>> implements RichComparable<Range<T>>, StringRepresentable
{
	// ATTRIBUTES	--------------------
	
	private T first, last;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new range
	 * @param first The first value (inclusive)
	 * @param last The last value (inclusive)
	 */
	public Range(T first, T last)
	{
		this.first = first;
		this.last = last;
	}
	
	/**
	 * Creates a new integer range
	 * @param first The first value (inclusive)
	 * @param last The last value (inclusive)
	 * @return A range of integers
	 */
	public static IntRange fromTo(int first, int last)
	{
		return new IntRange(first, last);
	}
	
	/**
	 * Creates a new integer range
	 * @param first The first value (inclusive)
	 * @param lastExclusive The last value (exclusive)
	 * @return A range of integers
	 */
	public static IntRange fromUntil(int first, int lastExclusive)
	{
		return fromTo(first, lastExclusive - 1);
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	public String toString()
	{
		if (first.equals(last))
			return first.toString();
		else
			return this.first + "-" + this.last;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((last == null) ? 0 : last.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Range))
			return false;
		Range<?> other = (Range<?>) obj;
		if (this.first == null)
		{
			if (other.first != null)
				return false;
		} else if (!this.first.equals(other.first))
			return false;
		if (this.last == null)
		{
			if (other.last != null)
				return false;
		} else if (!this.last.equals(other.last))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(Range<T> other)
	{
		int firstCompare = this.first.compareTo(other.first);
		if (firstCompare == 0)
			return this.last.compareTo(other.last);
		else
			return firstCompare;
	}
	
	
	// ACCESSORS	--------------------

	/**
	 * @return The first value in this range (inclusive)
	 */
	public T getFirst()
	{
		return this.first;
	}
	
	/**
	 * @return The last value in this range (inclusive)
	 */
	public T getLast()
	{
		return this.last;
	}
	
	/**
	 * @return The first value in this range (inclusive)
	 */
	public T getStart()
	{
		return getFirst();
	}
	
	/**
	 * @return The last value in this range (inclusive)
	 */
	public T getEnd()
	{
		return getLast();
	}
	
	
	// OTHER	------------------------
	
	/**
	 * @return The minimum (smallest) value in this range
	 */
	public T getMin()
	{
		if (first.compareTo(last) <= 0)
			return first;
		else
			return last;
	}
	
	/**
	 * @return The maximum (largest) value in this range
	 */
	public T getMax()
	{
		if (first.compareTo(last) <= 0)
			return last;
		else
			return first;
	}
	
	/**
	 * Checks whether the provided item is contained in this range
	 * @param item An item
	 * @return Whether this range contains the specified item
	 */
	public boolean contains(T item)
	{
		return item.compareTo(this.first) >= 0 && item.compareTo(this.last) <= 0;
	}
	
	/**
	 * @param other Another range
	 * @return Whether this range contains the whole other range
	 */
	public boolean contains(Range<? extends T> other)
	{
		return contains(other.getStart()) && contains(other.getEnd());
	}
	
	/**
	 * @param other Another range
	 * @return Whether these two ranges overlap
	 */
	public boolean overlapsWith(Range<? extends T> other)
	{
		return contains(other.getStart()) || contains(other.getEnd());
	}
	
	/**
	 * Converts this range to a view of items in the range
	 * @param increment A function used for incrementing the values (Must always move towards the end value)
	 * @return A view of the items in this range
	 */
	public View<T> view(Function<? super T, ? extends T> increment)
	{
		return new View<>(() -> new RangeIterator<>(getFirst(), getLast(), increment));
	}
	
	/**
	 * Converts this range to a view of items in the range
	 * @param increment A function used for incrementing the values (Must always return a larger value!)
	 * @return A view of the items in this range
	 * @deprecated Please use {@link #view(Function)} instead
	 */
	public View<T> toView(Function<? super T, ? extends T> increment)
	{
		return new View<>(() -> new RangeIterator<>(getFirst(), getLast(), increment));
	}
}
