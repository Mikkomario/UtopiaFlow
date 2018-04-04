package utopia.flow.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This filter class can be used for combining multiple filters under a logical operation
 * @author Mikko Hilpinen
 * @since 29.4.2016
 * @param <T> The type of object filtered by this filter
 * @deprecated Please use Java 8 filters instead
 */
public class CombinedFilter<T> implements Filter<T>
{
	// ATTRIBUTES	--------------
	
	private ConditionOperator operator;
	private List<Filter<T>> filters;
	
	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new filter that takes multiple filters into account
	 * @param operator The operator used for checking inclusivity. For example, with AND, 
	 * all filters must include the element. With OR, only one of the filters must include 
	 * the element
	 * @param filters The filters that are used
	 */
	public CombinedFilter(ConditionOperator operator, Collection<? extends Filter<T>> filters)
	{
		this.operator = operator;
		this.filters = new ArrayList<>();
		this.filters.addAll(filters);
	}
	
	/**
	 * Creates a new filter that takes multiple filters into account
	 * @param operator The operator used for checking inclusivity. For example, with AND, 
	 * all filters must include the element. With OR, only one of the filters must include 
	 * the element
	 * @param first The first filter
	 * @param second The second filter
	 */
	public CombinedFilter(ConditionOperator operator, Filter<T> first, Filter<T> second)
	{
		this.operator = operator;
		this.filters = new ArrayList<>();
		this.filters.add(first);
		this.filters.add(second);
	}
	
	
	// IMPLEMENTED METHODS	-----

	@Override
	public boolean includes(T e)
	{
		boolean[] inclusions = new boolean[this.filters.size()];
		for (int i = 0; i < inclusions.length; i++)
		{
			inclusions[i] = this.filters.get(i).includes(e);
		}
		
		return this.operator.operate(inclusions);
	}
}
