package utopia.flow.structure;

import java.util.function.Function;

/**
 * Ranges specify a start and end value and contain all values in between
 * @author Mikko Hilpinen
 * @since 25.7.2018
 * @param <T> The type of the objects contained in this range
 */
public class Range<T extends Comparable<? super T>> implements RichIterable<T>, RichComparable<Range<T>>
{
	// ATTRIBUTES	--------------------
	
	private T first, last;
	private Function<? super T, ? extends T> next;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new range
	 * @param first The first value (inclusive)
	 * @param last The last value (inclusive)
	 * @param next A function for incrementing the value
	 */
	public Range(T first, T last, Function<? super T, ? extends T> next)
	{
		this.first = first;
		this.last = last;
		this.next = next;
	}
	
	/**
	 * Creates a new integer range
	 * @param first The first value (inclusive)
	 * @param last The last value (inclusive)
	 * @return A range of integers
	 */
	public static Range<Integer> fromTo(int first, int last)
	{
		return new Range<>(first, last, i -> i + 1);
	}
	
	/**
	 * Creates a new integer range
	 * @param first The first value (inclusive)
	 * @param lastExclusive The last value (exclusive)
	 * @return A range of integers
	 */
	public static Range<Integer> fromUntil(int first, int lastExclusive)
	{
		return fromTo(first, lastExclusive - 1);
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	public String toString()
	{
		return this.first + "-" + this.last;
	}
	
	@Override
	public RichIterator<T> iterator()
	{
		return new RangeIterator();
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
	 * @return The last value in this range (exclusive)
	 */
	public T getLastExclusive()
	{
		return this.next.apply(this.last);
	}
	
	
	// OTHER	------------------------
	
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
	 * @return A list containing each value in this range
	 */
	public ImmutableList<T> toList()
	{
		return view().force();
	}
	
	
	// NESTED CLASSES	----------------
	
	private class RangeIterator implements RichIterator<T>
	{
		// ATTRIBUTES	----------------
		
		private T nextItem = getFirst();
		
		@Override
		public boolean hasNext()
		{
			return this.nextItem.compareTo(getLast()) <= 0;
		}

		@Override
		public T next()
		{
			T next = this.nextItem;
			this.nextItem = Range.this.next.apply(next);
			return next;
		}
	}
}
