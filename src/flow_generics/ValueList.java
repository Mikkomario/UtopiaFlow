package flow_generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A value list contains multiple values. All of the values are cast to a certain data type
 * @author Mikko Hilpinen
 * @since 1.1.2016
 */
public class ValueList extends ArrayList<Value>
{
	// ATTRIBUTES	---------------
	
	private static final long serialVersionUID = 633234460211587182L;

	private DataType type;
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new list
	 * @param type The data type of the values in the list
	 */
	public ValueList(DataType type)
	{
		this.type = type;
	}

	/**
	 * Creates a new list. The list's type will depend from the provided value's type
	 * @param value The initial value of the list
	 */
	public ValueList(Value value)
	{
		this.type = value.getType();
		add(value);
	}
	
	/**
	 * Creates a new list
	 * @param type The data type of the values in the list
	 * @param values a collection of values that will be added to the list
	 */
	public ValueList(DataType type, Collection<? extends Value> values)
	{
		this.type = type;
		addAll(values);
	}
	
	/**
	 * Copies a list from another list
	 * @param other Another list
	 */
	public ValueList(ValueList other)
	{
		this.type = other.getType();
		super.addAll(other);
	}
	
	
	// IMPLEMENTED METHODS	--------------
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("Value list (");
		s.append(getType());
		s.append(")");
		if (!isEmpty())
		{
			s.append(": ");
			boolean isFirst = true;
			for (Value value : this)
			{
				if (!isFirst)
					s.append(", ");
				
				if (value == null)
					s.append("null");
				else
					s.append(value.toString());
				
				isFirst = false;
			}
		}
		
		return s.toString();
	}
	
	/**
	 * Adds a value to the end of the list. The value is cast to the list's type
	 * @throws DataTypeException if the value couldn't be cast to the list's type
	 */
	@Override
	public boolean add(Value value) throws DataTypeException
	{
		// Casts the value to the correct data type first.
		if (value != null)
			return super.add(value.castTo(getType()));
		else
			return super.add(value);
	}
	
	/**
	 * Adds multiple values to the list. The values are cast to the list's data type
	 * @throws DataTypeException if a value couldn't be cast to the list's data type
	 */
	@Override
	public boolean addAll(Collection<? extends Value> values) throws DataTypeException
	{
		if (values == null)
			return false;
		else if (values.isEmpty())
			return false;
		else
		{
			for (Value value : values)
			{
				add(value);
			}
			return true;
		}
	}
	
	@Override
	public boolean contains(Object o)
	{
		if (o == null)
			return super.contains(o);
		else if (o instanceof Value)
			return contains((Value) o);
		else
			return false;
	}
	
	@Override
	public boolean remove(Object o)
	{
		if (o == null)
			return super.remove(o);
		else if (o instanceof Value)
			return super.remove(((Value) o).castTo(getType()));
		else
			return false;
	}
	
	@Override
	public boolean removeAll(Collection<? extends Object> o)
	{
		if (o == null || o.isEmpty())
			return false;
		else
		{
			for (Object object : o)
			{
				remove(object);
			}
			
			return true;
		}
	}
	
	
	// ACCESSORS	----------------------
	
	/**
	 * @return The data type of the values in the list
	 */
	public DataType getType()
	{
		return this.type;
	}
	
	
	// OTHER METHODS	------------------
	
	/**
	 * Checks whether the list contains the provided value
	 * @param value A value that the list may contain
	 * @return Whether the list contains the provided value
	 */
	public boolean contains(Value value)
	{
		if (value == null)
			return super.contains(value);
		else
			return super.contains(value.castTo(getType()));
	}
	
	/**
	 * @return All the object values of the values inside the list. The class of the objects 
	 * depends from the data type of the list
	 */
	public List<Object> getObjectValues()
	{
		List<Object> values = new ArrayList<>();
		for (Value value : this)
		{
			values.add(value.getObjectValue());
		}
		
		return values;
	}
	
	/**
	 * Parses all the values in the list
	 * @param targetType The data type of the requested object values
	 * @return All the object values of the values in the list. The class depends from the 
	 * provided data type value
	 * @throws DataTypeException If value casting failed
	 */
	public List<Object> getObjectValues(DataType targetType) throws DataTypeException
	{
		List<Object> values = new ArrayList<>();
		for (Value value : this)
		{
			values.add(value.parseTo(targetType));
		}
		
		return values;
	}
	
	/**
	 * Casts the list to a list of another data type. The returned list is a copy and changes 
	 * made to it won't affect this list.
	 * @param type The data type of the new list
	 * @return A copy of this list with the new data type
	 * @throws DataTypeException If value casting failed
	 */
	public ValueList castTo(DataType type) throws DataTypeException
	{
		return new ValueList(type, this);
	}
	
	/**
	 * Creates a new list that contains the provided value
	 * @param value A value
	 * @return A new list that contains the provided value
	 */
	public ValueList plus(Value value)
	{
		ValueList list = new ValueList(this);
		list.add(value);
		
		return list;
	}
	
	/**
	 * Creates a new list that contains the elements from both lists
	 * @param other Another value list
	 * @return a list that contains value from both lists
	 */
	public ValueList plus(Collection<? extends Value> other)
	{
		ValueList list = new ValueList(this);
		list.addAll(other);
		
		return list;
	}
	
	/**
	 * Creates a new list that doesn't contain the provided value
	 * @param value a value
	 * @return A copy of this list without the provided value
	 */
	public ValueList minus(Value value)
	{
		ValueList list = new ValueList(this);
		list.remove(value);
		
		return list;
	}
	
	/**
	 * Creates a new list that doesn't contain any of the provided elements
	 * @param other Another list
	 * @return A copy of this list without any of the provided values
	 */
	public ValueList minus(Collection<? extends Value> other)
	{
		ValueList list = new ValueList(this);
		list.removeAll(other);
		
		return list;
	}
}
