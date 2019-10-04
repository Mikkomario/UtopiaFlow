package utopia.java.flow.util;

import java.util.Collection;

/**
 * This filter only includes values contained within a specific collection
 * @author Mikko Hilpinen
 * @since 30.4.2016
 * @deprecated Please use Java 8 filters instead
 */
public class OneOfFilter implements Filter<Object>
{
	// ATTRIBUTES	------------
	
	private Collection<?> included;
	
	
	// CONSTRUCTOR	-----------
	
	/**
	 * Creates a new filter
	 * @param included The elements that are included by the filter
	 */
	public OneOfFilter(Collection<?> included)
	{
		this.included = included;
	}

	
	// IMPLEMENTED METHODS	---
	
	@Override
	public boolean includes(Object e)
	{
		return this.included.contains(e);
	}
}
