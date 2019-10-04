package utopia.java.flow.util;

/**
 * This class can be used for reversing a filter's logical inclusion
 * @author Mikko Hilpinen
 * @since 29.4.2016
 * @param <T> The type of object filtered by this filter
 * @deprecated Please use Java 8 filters instead
 */
public class InvertedFilter<T> implements Filter<T>
{
	// ATTRIBUTES	--------------
	
	private Filter<T> filter;
	
	
	// CONSTRUCTOR	--------------

	/**
	 * Creates a new inverted filter. The filter will work exactly like the provided filter, 
	 * except include all elements the other would exclude and exclude the ones the other 
	 * would include.
	 * @param filter The filter this filter is based on
	 */
	public InvertedFilter(Filter<T> filter)
	{
		this.filter = filter;
	}
	
	
	// IMPLEMENTED METHODS	------
	
	@Override
	public boolean includes(T e)
	{
		return !this.filter.includes(e);
	}
}
