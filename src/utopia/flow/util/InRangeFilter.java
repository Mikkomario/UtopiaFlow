package utopia.flow.util;

import java.util.Comparator;

/**
 * This filter type includes values that are within specific range
 * @author Mikko Hilpinen
 * @since 30.4.2016
 * @param <T> The type of object filtered by this filter (must be comparable)
 */
public class InRangeFilter<T> implements Filter<T>
{
	// ATTRIBUTES	----------------
	
	private Comparator<T> comparator;
	private T min, max;
	private boolean minIncluded = true, maxIncluded = true;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new filter with minimum and maximum included values
	 * @param comparator The comparator used for comparing the values
	 * @param min The minimum included value (inclusive)
	 * @param max The maximum included value (inclusive)
	 */
	public InRangeFilter(Comparator<T> comparator, T min, T max)
	{
		this.comparator = comparator;
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Creates a new filter with minimum and maximum values
	 * @param comparator The comparator used for comparing the values
	 * @param min The minimum value
	 * @param max The maximum value
	 * @param minIncluded Should the minimum value be included
	 * @param maxIncluded Should the maximum value be included
	 */
	public InRangeFilter(Comparator<T> comparator, T min, T max, boolean minIncluded, 
			boolean maxIncluded)
	{
		this.comparator = comparator;
		this.min = min;
		this.max = max;
		this.minIncluded = minIncluded;
		this.maxIncluded = maxIncluded;
	}
	
	/**
	 * Creates a new filter with minimum and maximum included values
	 * @param min The minimum included value (inclusive)
	 * @param max The maximum included value (inclusive)
	 * @return The filter
	 */
	public static <T extends Comparable<T>> InRangeFilter<T> createFilter(T min, T max)
	{
		return new InRangeFilter<>(new SimpleComparator<>(), min, max);
	}
	
	/**
	 * Creates a new filter with minimum and maximum values
	 * @param min The minimum value
	 * @param max The maximum value
	 * @param minIncluded Should the minimum value be included
	 * @param maxIncluded Should the maximum value be included
	 * @return The filter
	 */
	public static <T extends Comparable<T>> InRangeFilter<T> createFilter(T min, T max, 
			boolean minIncluded, boolean maxIncluded)
	{
		return new InRangeFilter<>(new SimpleComparator<>(), min, max, minIncluded, maxIncluded);
	}
	
	
	// IMPLEMENTED METHODS	-------

	@Override
	public boolean includes(T e)
	{
		int compareMin = this.comparator.compare(this.min, e);
		if (compareMin > 0 || (compareMin == 0 && !this.minIncluded))
			return false;
		
		int compareMax = this.comparator.compare(this.max, e);
		if (compareMax < 0 || (compareMax == 0 && !this.maxIncluded))
			return false;
		
		return true;
	}
	
	
	// NESTED CLASSES	-----------
	
	private static class SimpleComparator<T extends Comparable<T>> implements Comparator<T>
	{
		@Override
		public int compare(T o1, T o2)
		{
			if (o1 == null)
			{
				if (o2 == null)
					return 0;
				else
					return -1;
			}
			else if (o2 == null)
				return 1;
			
			return o1.compareTo(o2);
		}	
	}
}
