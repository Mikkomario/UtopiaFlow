package flow_util;

import java.time.LocalDate;
import java.time.LocalDateTime;

import flow_util.ValueParser.ValueParseException;

/**
 * This is an immutable value class that has extra support for the basic data types
 * @author Mikko Hilpinen
 * @since 8.11.2015
 */
public class BasicValue implements Value
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
	public BasicValue(Object value, DataType type)
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
	public BasicValue(Object value, DataType from, DataType to) throws ValueParseException
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
	public BasicValue(Value other, DataType to) throws ValueParseException
	{
		this.value = DataTypes.getInstance().parse(other, to);
		this.type = to;
	}
	
	/**
	 * Wraps a boolean value
	 * @param bool a boolean value
	 * @return wrapped boolean value
	 */
	public static BasicValue Boolean(Boolean bool)
	{
		return new BasicValue(bool, BasicDataType.BOOLEAN);
	}
	
	/**
	 * Wraps an extra boolean value
	 * @param bool an extra boolean value
	 * @return wrapped value
	 */
	public static BasicValue ExtraBoolean(ExtraBoolean bool)
	{
		return new BasicValue(bool, BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * Wraps a number value
	 * @param number a number value
	 * @return wrapped value
	 */
	public static BasicValue Number(Number number)
	{
		return new BasicValue(number, BasicDataType.NUMBER);
	}
	
	/**
	 * Wraps a integer value
	 * @param number a integer value
	 * @return wrapped value
	 */
	public static BasicValue Integer(Integer number)
	{
		return new BasicValue(number, BasicDataType.INTEGER);
	}
	
	/**
	 * Wraps a double value
	 * @param number a double value
	 * @return wrapped value
	 */
	public static BasicValue Double(Double number)
	{
		return new BasicValue(number, BasicDataType.DOUBLE);
	}
	
	/**
	 * Wraps a long value
	 * @param number a long value
	 * @return wrapped value
	 */
	public static BasicValue Long(Long number)
	{
		return new BasicValue(number, BasicDataType.LONG);
	}
	
	/**
	 * Wraps a date value
	 * @param date a date value
	 * @return wrapped value
	 */
	public static BasicValue Date(LocalDate date)
	{
		return new BasicValue(date, BasicDataType.DATE);
	}
	
	/**
	 * Wraps a datetime value
	 * @param time a datetime value
	 * @return wrapped value
	 */
	public static BasicValue DateTime(LocalDateTime time)
	{
		return new BasicValue(time, BasicDataType.DATETIME);
	}
	
	
	// IMPLEMENTED METHODS	-----------

	@Override
	public Object getObjectValue()
	{
		return this.value;
	}

	@Override
	public DataType getType()
	{
		return this.type;
	}

	@Override
	public boolean isNull()
	{
		return getObjectValue() == null;
	}
	
	@Override
	public String toString()
	{
		return BasicValueParser.parseString(getObjectValue());
	}

	
	// OTHER METHODS	--------------
	
	/**
	 * @return The value casted to boolean
	 */
	public Boolean toBoolean()
	{
		return BasicValueParser.parseBoolean(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to extra boolean
	 */
	public ExtraBoolean toExtraBoolean()
	{
		return BasicValueParser.parseExtraBoolean(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to number
	 */
	public Number toNumber()
	{
		return BasicValueParser.parseNumber(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to integer
	 */
	public Integer toInteger()
	{
		return BasicValueParser.parseInteger(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to double
	 */
	public Double toDouble()
	{
		return BasicValueParser.parseDouble(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to long
	 */
	public Long toLong()
	{
		return BasicValueParser.parseLong(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to localDate
	 */
	public LocalDate toLocalDate()
	{
		return BasicValueParser.parseDate(getObjectValue(), getType());
	}
	
	/**
	 * @return The value casted to localDateTime
	 */
	public LocalDateTime toLocalDateTime()
	{
		return BasicValueParser.parseDateTime(getObjectValue(), getType());
	}
}
