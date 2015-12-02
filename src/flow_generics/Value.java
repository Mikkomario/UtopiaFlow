package flow_generics;

import java.time.LocalDate;
import java.time.LocalDateTime;

import flow_generics.ValueParser.ValueParseException;

/**
 * This is an immutable value class that has extra support for the basic data types
 * @author Mikko Hilpinen
 * @since 8.11.2015
 */
public class Value
{
	// ATTRIBUTES	------------------
	
	private Object value;
	private DataType type;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new value. The provided data type should match the provided value type.
	 * @param value The object value
	 * @param type The data type of the value
	 */
	public Value(Object value, DataType type)
	{
		this.value = value;
		this.type = type;
	}
	
	/**
	 * Creates a new value.
	 * @param value The object value
	 * @param from The data type of the provided value
	 * @param to The data type of this value instance
	 * @throws ValueParseException If the provided value couldn't be parsed to desired type
	 */
	public Value(Object value, DataType from, DataType to) throws ValueParseException
	{
		this.value = DataTypes.getInstance().parse(value, from, to);
		this.type = to;
	}
	
	/**
	 * Creates a new value by parsing another
	 * @param other The other value
	 * @param to The data type of this value instance
	 * @throws ValueParseException If the value parsing failed
	 */
	public Value(Value other, DataType to) throws ValueParseException
	{
		this.value = DataTypes.getInstance().parse(other, to);
		this.type = to;
	}
	
	/**
	 * Wraps a boolean value
	 * @param bool a boolean value
	 * @return wrapped boolean value
	 */
	public static Value Boolean(Boolean bool)
	{
		return new Value(bool, BasicDataType.BOOLEAN);
	}
	
	/**
	 * Wraps an extra boolean value
	 * @param bool an extra boolean value
	 * @return wrapped value
	 */
	public static Value ExtraBoolean(ExtraBoolean bool)
	{
		return new Value(bool, BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * Wraps a number value
	 * @param number a number value
	 * @return wrapped value
	 */
	public static Value Number(Number number)
	{
		return new Value(number, BasicDataType.NUMBER);
	}
	
	/**
	 * Wraps a integer value
	 * @param number a integer value
	 * @return wrapped value
	 */
	public static Value Integer(Integer number)
	{
		return new Value(number, BasicDataType.INTEGER);
	}
	
	/**
	 * Wraps a double value
	 * @param number a double value
	 * @return wrapped value
	 */
	public static Value Double(Double number)
	{
		return new Value(number, BasicDataType.DOUBLE);
	}
	
	/**
	 * Wraps a long value
	 * @param number a long value
	 * @return wrapped value
	 */
	public static Value Long(Long number)
	{
		return new Value(number, BasicDataType.LONG);
	}
	
	/**
	 * Wraps a date value
	 * @param date a date value
	 * @return wrapped value
	 */
	public static Value Date(LocalDate date)
	{
		return new Value(date, BasicDataType.DATE);
	}
	
	/**
	 * Wraps a datetime value
	 * @param time a datetime value
	 * @return wrapped value
	 */
	public static Value DateTime(LocalDateTime time)
	{
		return new Value(time, BasicDataType.DATETIME);
	}
	
	/**
	 * Creates a new null value
	 * @param type The data type of the null value
	 * @return A null value
	 */
	public static Value NullValue(DataType type)
	{
		return new Value(null, type);
	}
	
	
	// IMPLEMENTED METHODS	-----------

	/**
	 * @return The object value in this value instance. The data type of the returned object 
	 * reflects this value's data type
	 */
	public Object getObjectValue()
	{
		return this.value;
	}

	/**
	 * @return The data type of this value
	 */
	public DataType getType()
	{
		return this.type;
	}

	/**
	 * @return Is the value a null value
	 */
	public boolean isNull()
	{
		return getObjectValue() == null;
	}
	
	/**
	 * Casts the value to a string like when casting it to any other data type
	 * @return The value casted to a string
	 */
	@Override
	public String toString()
	{
		return (String) parseTo(BasicDataType.STRING);
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
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
		if (!(obj instanceof Value))
			return false;
		
		Value other = (Value) obj;
		if (getType() == null)
		{
			if (other.getType() != null)
				return false;
		}
		else if (other.getType() == null)
			return false;
		else if (!getType().equals(other.getType()))
			return false;
		
		if (isNull())
			return isNull();
		else if (other.isNull())
			return false;
		
		return getObjectValue().equals(other.getObjectValue());
	}
	
	
	// OTHER METHODS	--------------

	/**
	 * Returns the value's object value in a specific data type
	 * @param type The desired data type
	 * @return The value's object value in the desired data type
	 * @throws DataTypeException If the parsing failed
	 */
	public Object parseTo(DataType type) throws DataTypeException
	{
		if (getType().equals(type))
			return getObjectValue();
		
		return DataTypes.getInstance().parse(this, type);
	}
	
	/**
	 * Casts the value to a new value with a different data type
	 * @param type The desired data type
	 * @return The value cast to the desired data type
	 * @throws DataTypeException If the casting failed
	 */
	public Value castTo(DataType type) throws DataTypeException
	{
		if (getType().equals(type))
			return this;
		
		return new Value(parseTo(type), type);
	}
	
	/**
	 * @return The value casted to boolean
	 */
	public Boolean toBoolean()
	{
		return (Boolean) parseTo(BasicDataType.BOOLEAN);
	}
	
	/**
	 * @return The value casted to extra boolean
	 */
	public ExtraBoolean toExtraBoolean()
	{
		return (ExtraBoolean) parseTo(BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * @return The value casted to number
	 */
	public Number toNumber()
	{
		return (Number) parseTo(BasicDataType.NUMBER);
	}
	
	/**
	 * @return The value casted to integer
	 */
	public Integer toInteger()
	{
		return (Integer) parseTo(BasicDataType.INTEGER);
	}
	
	/**
	 * @return The value casted to double
	 */
	public Double toDouble()
	{
		return (Double) parseTo(BasicDataType.DOUBLE);
	}
	
	/**
	 * @return The value casted to long
	 */
	public Long toLong()
	{
		return (Long) parseTo(BasicDataType.LONG);
	}
	
	/**
	 * @return The value casted to localDate
	 */
	public LocalDate toLocalDate()
	{
		return (LocalDate) parseTo(BasicDataType.DATE);
	}
	
	/**
	 * @return The value casted to localDateTime
	 */
	public LocalDateTime toLocalDateTime()
	{
		return (LocalDateTime) parseTo(BasicDataType.DATETIME);
	}
	
	/**
	 * @return The value casted to variable
	 */
	public Variable toVariable()
	{
		return (Variable) parseTo(BasicDataType.VARIABLE);
	}
	
	/**
	 * @return The value casted to model
	 */
	public Model toModel()
	{
		return (Model) parseTo(BasicDataType.MODEL);
	}
}
