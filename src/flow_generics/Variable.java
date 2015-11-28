package flow_generics;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Variable's have an inmutable data type and name, but their values can change.
 * @author Mikko Hilpinen
 * @since 10.11.2015
 */
public class Variable implements Value
{
	// ATTRIBUTES	-------------------
	
	private DataType dataType;
	private String name;
	private Value value;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new variable. The provided value will define the variable's data type.
	 * @param name The name of the variable
	 * @param value The value assigned to the variable
	 */
	public Variable(String name, Value value)
	{
		this.dataType = value.getType();
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Creates a new 'uninitialised' variable with a null value.
	 * @param name The name of the variable.
	 * @param type The data type of the variable.
	 */
	public Variable(String name, DataType type)
	{
		this.dataType = type;
		this.name = name;
		this.value = new NullValue(type);
	}
	
	/**
	 * Creates a new variable from an object value
	 * @param name The name of the variable
	 * @param type The data type of the variable
	 * @param value The object value of the variable. Must reflect the correct data type.
	 * @throws DataTypeException If the casting fails
	 */
	public Variable(String name, DataType type, Object value) throws DataTypeException
	{
		this.dataType = type;
		this.name = name;
		this.value = DataTypes.getInstance().wrap(value, type);
	}
	
	/**
	 * Creates a new variable.
	 * @param name The name of the variable
	 * @param type The data type of the variable
	 * @param value The value that will be assigned to the variable
	 * @throws DataTypeException If the casting fails
	 */
	public Variable(String name, DataType type, Value value) throws DataTypeException
	{
		this.dataType = type;
		this.name = name;
		this.value = DataTypes.cast(value, type);
	}
	
	/**
	 * Creates a new variable by casting an object value to correct type
	 * @param name The name of the variable
	 * @param type The data type of the variable
	 * @param value The object value of the variable
	 * @param valueType The data type of the provided object value
	 * @throws DataTypeException If the casting fails
	 */
	public Variable(String name, DataType type, Object value, DataType valueType) throws DataTypeException
	{
		this.dataType = type;
		this.name = name;
		this.value = DataTypes.createValue(value, valueType, type);
	}
	
	/**
	 * Creates a copy of another variable
	 * @param other the variable that will be copied
	 */
	public Variable(Variable other)
	{
		this.dataType = other.getType();
		this.name = other.getName();
		this.value = other.value;
	}
	
	
	// IMPLEMENTED METHODS	----------------

	@Override
	public Object getObjectValue()
	{
		return this.value.getObjectValue();
	}

	@Override
	public DataType getType()
	{
		return this.dataType;
	}

	@Override
	public boolean isNull()
	{
		return this.value.isNull();
	}
	
	/**
	 * Parses the variable into a readable declaration, which contains the variable name, 
	 * data type and value. Eg. 'foo (INTEGER) = 1' or 'bar (STRING) = null'
	 */
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder(this.name);
		s.append(" (");
		s.append(getType());
		s.append(") = ");
		if (isNull())
			s.append("null");
		else
			s.append(getStringValue());
		
		return s.toString();
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Variable))
			return false;
		
		return equals((Variable) obj).toBoolean();
	}
	
	
	// ACCESSORS	------------------------

	/**
	 * @return The name of the variable
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Assigns a new value to this variable. The provided value may be cast to the correct 
	 * type.
	 * @param value The value assigned to this variable
	 */
	public void setValue(Value value)
	{
		this.value = DataTypes.cast(value, getType());
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Checks how equal the two values are
	 * @param other Another value
	 * @return EXTRA TRUE if the other value is a variable with the same name 
	 * (case-sensitive) and equal value<br>
	 * WEAK_TRUE if the other value is a variable with the same name (case-insensitive) and 
	 * equal value<br>
	 * WEAK_FALSE if the other value is a variable with equal value or a value that equals this 
	 * variable's value<br>
	 * EXTRA FALSE otherwise
	 */
	public ExtraBoolean equals(Value other)
	{
		if (other == null)
			return ExtraBoolean.EXTRA_FALSE;
		
		if (other instanceof Variable)
			return equals((Variable) other);
		
		if (Value.valuesAreEqual(this, other))
			return ExtraBoolean.WEAK_FALSE;
		
		return ExtraBoolean.EXTRA_FALSE;
	}
	
	private ExtraBoolean equals(Variable other)
	{
		if (other == null)
			return ExtraBoolean.EXTRA_FALSE;
		
		if (!getName().equals(other.getName()))
		{
			if (getName().equalsIgnoreCase(other.getName()) && Value.valuesAreEqual(this, other))
				return ExtraBoolean.WEAK_TRUE;
			if (Value.valuesAreEqual(this, other))
				return ExtraBoolean.WEAK_FALSE;
			else
				return ExtraBoolean.EXTRA_FALSE;
		}
		else if (Value.valuesAreEqual(this, other))
			return ExtraBoolean.EXTRA_TRUE;
		else
			return ExtraBoolean.EXTRA_FALSE;
	}
	
	/**
	 * Checks if the two variables have equal values
	 * @param other The other variable
	 * @return Do the variables have equal values (same data type and object value)
	 */
	public boolean hasEqualValueWith(Variable other)
	{
		if (other == null)
			return false;
		
		return Value.valuesAreEqual(this.value, other.value);
	}
	
	/**
	 * Assigns a new object value to this variable. The provided value may be cast to the 
	 * correct type.
	 * @param value an object value
	 * @param type The data type that of the provided value
	 */
	public void setValue(Object value, DataType type)
	{
		this.value = DataTypes.createValue(value, type, getType());
	}
	
	/**
	 * Returns the object value of this variable. The value will be cast to the correct 
	 * data type.
	 * @param desiredType The data type the object value is required in
	 * @return The variable's object value in the correct data type
	 */
	public Object getObjectValue(DataType desiredType)
	{
		if (isNull())
			return null;
		return DataTypes.getInstance().parse(getObjectValue(), getType(), desiredType);
	}
	
	/**
	 * @return The variable's value as a string
	 */
	public String getStringValue()
	{
		return (String) getObjectValue(BasicDataType.STRING);
	}
	
	/**
	 * @return The variable's value as a number
	 */
	public Number getNumberValue()
	{
		return (Number) getObjectValue(BasicDataType.NUMBER);
	}
	
	/**
	 * @return The variable's value as an integer
	 */
	public Integer getIntegerValue()
	{
		return (Integer) getObjectValue(BasicDataType.INTEGER);
	}
	
	/**
	 * @return The variable's value as a double
	 */
	public Double getDoubleValue()
	{
		return (Double) getObjectValue(BasicDataType.DOUBLE);
	}
	
	/**
	 * @return The variable's value as a long
	 */
	public Long getLongValue()
	{
		return (Long) getObjectValue(BasicDataType.LONG);
	}
	
	/**
	 * @return The variable's value as a boolean
	 */
	public Boolean getBooleanValue()
	{
		return (Boolean) getObjectValue(BasicDataType.BOOLEAN);
	}
	
	/**
	 * @return The variable's value as an extra boolean
	 */
	public ExtraBoolean getExtraBooleanValue()
	{
		return (ExtraBoolean) getObjectValue(BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * @return The variable's value as a localDate
	 */
	public LocalDate getDateValue()
	{
		return (LocalDate) getObjectValue(BasicDataType.DATE);
	}
	
	/**
	 * @return The variable's value as a localDateTime
	 */
	public LocalDateTime getDateTimeValue()
	{
		return (LocalDateTime) getObjectValue(BasicDataType.DATETIME);
	}
}
