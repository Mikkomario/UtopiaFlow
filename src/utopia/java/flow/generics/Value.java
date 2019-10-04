package utopia.java.flow.generics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.Option;
import utopia.java.flow.util.ExtraBoolean;
import utopia.java.flow.util.RichString;
import utopia.java.flow.util.StringRepresentable;

/**
 * This is an immutable value class that has extra support for the basic data types
 * @author Mikko Hilpinen
 * @since 8.11.2015
 */
public class Value implements StringRepresentable
{
	// ATTRIBUTES	------------------
	
	/**
	 * An empty value with object data type
	 */
	public static final Value EMPTY = Value.NullValue(BasicDataType.OBJECT);
	
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
	/*
	public Value(Object value, DataType from, DataType to) throws ValueParseException
	{
		this.value = DataTypes.getInstance().cast(value, from, to);
		this.type = to;
	}
	*/
	/**
	 * Creates a new value by parsing another
	 * @param other The other value
	 * @param to The data type of this value instance
	 * @throws ValueParseException If the value parsing failed
	 */
	/*
	public Value(Value other, DataType to) throws ValueParseException
	{
		this.value = DataTypes.getInstance().parse(other, to);
		this.type = to;
	}
	*/
	
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
	 * Wraps a float into a value
	 * @param number a float number
	 * @return the wrapped value
	 */
	public static Value Float(Float number)
	{
		return new Value(number, BasicDataType.FLOAT);
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
	 * Wraps a localTime value
	 * @param time a time value
	 * @return wrapped value
	 */
	public static Value Time(LocalTime time)
	{
		return new Value(time, BasicDataType.TIME);
	}
	
	/**
	 * Wraps a string value
	 * @param string a string object
	 * @return A string value
	 */
	public static Value String(String string)
	{
		// Empty strings count as no value
		if (string == null || string.isEmpty())
			return NullValue(BasicDataType.STRING);
		else
			return new Value(string, BasicDataType.STRING);
	}
	
	/**
	 * Wraps a variable object value
	 * @param var a variable object
	 * @return a variable value
	 */
	public static Value Variable(Variable var)
	{
		return new Value(var, BasicDataType.VARIABLE);
	}
	
	/**
	 * Wraps a model object value
	 * @param model a model object value
	 * @return a model value
	 */
	public static Value Model(Model<Variable> model)
	{
		return new Value(model, BasicDataType.MODEL);
	}
	
	/**
	 * Wraps a variable declaration object value
	 * @param declaration a variable declaration
	 * @return a variable declaration value
	 */
	public static Value VariableDeclaration(VariableDeclaration declaration)
	{
		return new Value(declaration, BasicDataType.VARIABLE_DECLARATION);
	}
	
	/**
	 * Wraps a model declaration object
	 * @param declaration a model declaration
	 * @return a model declaration value
	 */
	public static Value ModelDeclaration(ModelDeclaration declaration)
	{
		return new Value(declaration, BasicDataType.MODEL_DECLARATION);
	}
	
	/**
	 * Wraps a value list
	 * @param list a value list
	 * @return a value list value
	 * @deprecated Please use {@link #of(ImmutableList)} instead
	 */
	public static Value List(ValueList list)
	{
		return new Value(list, BasicDataType.LIST);
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
	
	/**
	 * Wraps a boolean value
	 * @param bool a boolean value
	 * @return wrapped boolean value
	 */
	public static Value of(Boolean bool)
	{
		return new Value(bool, BasicDataType.BOOLEAN);
	}
	
	/**
	 * Wraps an extra boolean value
	 * @param bool an extra boolean value
	 * @return wrapped value
	 */
	public static Value of(ExtraBoolean bool)
	{
		return new Value(bool, BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * Wraps a number value
	 * @param number a number value
	 * @return wrapped value
	 */
	public static Value of(Number number)
	{
		return new Value(number, BasicDataType.NUMBER);
	}
	
	/**
	 * Wraps a integer value
	 * @param number a integer value
	 * @return wrapped value
	 */
	public static Value of(Integer number)
	{
		return new Value(number, BasicDataType.INTEGER);
	}
	
	/**
	 * Wraps a double value
	 * @param number a double value
	 * @return wrapped value
	 */
	public static Value of(Double number)
	{
		return new Value(number, BasicDataType.DOUBLE);
	}
	
	/**
	 * Wraps a float into a value
	 * @param number a float number
	 * @return the wrapped value
	 */
	public static Value of(Float number)
	{
		return new Value(number, BasicDataType.FLOAT);
	}
	
	/**
	 * Wraps a long value
	 * @param number a long value
	 * @return wrapped value
	 */
	public static Value of(Long number)
	{
		return new Value(number, BasicDataType.LONG);
	}
	
	/**
	 * Wraps a date value
	 * @param date a date value
	 * @return wrapped value
	 */
	public static Value of(LocalDate date)
	{
		return new Value(date, BasicDataType.DATE);
	}
	
	/**
	 * Wraps a datetime value
	 * @param time a datetime value
	 * @return wrapped value
	 */
	public static Value of(LocalDateTime time)
	{
		return new Value(time, BasicDataType.DATETIME);
	}
	
	/**
	 * Wraps a localTime value
	 * @param time a time value
	 * @return wrapped value
	 */
	public static Value of(LocalTime time)
	{
		return new Value(time, BasicDataType.TIME);
	}
	
	/**
	 * Wraps a string value
	 * @param string a string object
	 * @return A string value
	 */
	public static Value of(String string)
	{
		return new Value(string, BasicDataType.STRING);
	}
	
	/**
	 * Wraps a list value
	 * @param list a list of values
	 * @return A list value
	 */
	public static Value of(ImmutableList<Value> list)
	{
		return new Value(list, BasicDataType.IMMUTABLE_LIST);
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
	 * @return Whether this value is null / empty
	 */
	public boolean isEmpty()
	{
		return isNull();
	}
	
	/**
	 * @return Whether this value has a real value associated with it
	 */
	public boolean isDefined()
	{
		return !isEmpty();
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
	
	/**
	 * @return The value casted to string or none if the value is empty or couldn't be casted.
	 */
	public Option<String> toStringOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((String) parseTo(BasicDataType.STRING));
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
			return other.isNull();
		else if (other.isNull())
			return false;
		
		return getObjectValue().equals(other.getObjectValue());
	}
	
	@Override
	public RichString description()
	{
		return toRichString();
	}
	
	
	// OTHER METHODS	--------------

	/**
	 * @return A description of this value, containing the object value and the data type. 
	 * For example "test (STRING)" or "null (INTEGER)"
	 */
	public String getDescription()
	{
		StringBuilder s = new StringBuilder();
		
		if (isNull())
			s.append("null");
		else
			s.append(toString());
		
		s.append(" (");
		s.append(getType());
		s.append(")");
		
		return s.toString();
	}
	
	/**
	 * Returns the value's object value in a specific data type
	 * @param type The desired data type
	 * @return The value's object value in the desired data type
	 * @throws DataTypeException If the parsing failed
	 */
	public Object parseTo(DataType type) throws DataTypeException
	{
		if (getType().equals(type) || DataTypes.dataTypeIsOfType(getType(), type))
			return getObjectValue();
		
		return DataTypes.getInstance().cast(this, type).getObjectValue();
	}
	
	/**
	 * Returns the value's object value in a specific data type
	 * @param type The desired data type
	 * @return Casted value or null
	 */
	public Object safeParseTo(DataType type)
	{
		try
		{
			return parseTo(type);
		}
		catch (DataTypeException e)
		{
			return null;
		}
	}
	
	/**
	 * Casts the value to a new value with a different data type
	 * @param type The desired data type
	 * @return The value cast to the desired data type
	 * @throws DataTypeException If the casting failed
	 */
	public Value castTo(DataType type) throws DataTypeException
	{
		return DataTypes.getInstance().cast(this, type);
	}
	
	/**
	 * Casts the value to a new value with one a different data type
	 * @param types The data types the end result may have
	 * @return The value cast to one of the desired data types
	 * @throws DataTypeException If the casting failed
	 */
	public Value castTo(SubTypeSet types) throws DataTypeException
	{
		return DataTypes.getInstance().cast(this, types);
	}
	
	/**
	 * Performs a value operation on this and another value
	 * @param operation The operation performed
	 * @param other Another value
	 * @return The result of the operation
	 * @throws ValueOperation.ValueOperationException If the operation failed
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public Value operate(ValueOperation operation, Value other) throws ValueOperation.ValueOperationException
	{
		return DataTypes.getInstance().operate(this, operation, other);
	}
	
	/**
	 * Combines the two values together
	 * @param other Another value
	 * @return a combination of the two values
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public Value plus(Value other)
	{
		return operate(BasicValueOperation.PLUS, other);
	}
	
	/**
	 * Subtracts a value from this value
	 * @param other Another value
	 * @return The result of the operation
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public Value minus(Value other)
	{
		return operate(BasicValueOperation.MINUS, other);
	}
	
	/**
	 * Multiplies this value with another value
	 * @param other A value that multiplies this value
	 * @return The multiplication of the two values
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public Value times(Value other)
	{
		return operate(BasicValueOperation.MULTIPLY, other);
	}
	
	/**
	 * Divides this value with another value
	 * @param other The value that divides this value
	 * @return The result of the operation
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public Value divided(Value other)
	{
		return operate(BasicValueOperation.DIVIDE, other);
	}
	
	/**
	 * @return The value casted to boolean
	 */
	public Boolean toBoolean()
	{
		if (isNull())
			return false;
		else
			return (Boolean) parseTo(BasicDataType.BOOLEAN);
	}
	
	/**
	 * @return The value casted to boolean and wrapped to option
	 */
	public Option<Boolean> toBooleanOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((Boolean) safeParseTo(BasicDataType.BOOLEAN));
	}
	
	/**
	 * @return The value casted to extra boolean
	 */
	public ExtraBoolean toExtraBoolean()
	{
		return (ExtraBoolean) parseTo(BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * @return The value casted to extra boolean and wrapped to option
	 */
	public Option<ExtraBoolean> toExtraBooleanOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((ExtraBoolean) safeParseTo(BasicDataType.EXTRA_BOOLEAN));
	}
	
	/**
	 * @return The value casted to number
	 */
	public Number toNumber()
	{
		if (isNull())
			return 0;
		else
			return (Number) parseTo(BasicDataType.NUMBER);
	}
	
	/**
	 * @return The value casted to integer
	 */
	public Integer toInteger()
	{
		if (isNull())
			return 0;
		else
			return (Integer) parseTo(BasicDataType.INTEGER);
	}
	
	/**
	 * @return The value casted to integer and wrapped to option
	 */
	public Option<Integer> toIntegerOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((Integer) safeParseTo(BasicDataType.INTEGER));
	}
	
	/**
	 * @return The value casted to double
	 */
	public Double toDouble()
	{
		if (isNull())
			return 0.0;
		else
			return (Double) parseTo(BasicDataType.DOUBLE);
	}
	
	/**
	 * @return The value casted to double and wrapped to option
	 */
	public Option<Double> toDoubleOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((Double) safeParseTo(BasicDataType.DOUBLE));
	}
	
	/**
	 * @return The value casted to long
	 */
	public Long toLong()
	{
		if (isNull())
			return 0l;
		else
			return (Long) parseTo(BasicDataType.LONG);
	}
	
	/**
	 * @return The value casted to long and wrapped to option
	 */
	public Option<Long> toLongOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((Long) safeParseTo(BasicDataType.LONG));
	}
	
	/**
	 * @return The value casted to float
	 */
	public Float toFloat()
	{
		if (isNull())
			return 0f;
		else
			return (Float) parseTo(BasicDataType.FLOAT);
	}
	
	/**
	 * @return The value casted to float and wrapped to option
	 */
	public Option<Float> toFloatOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((Float) safeParseTo(BasicDataType.FLOAT));
	}
	
	/**
	 * @return The value casted to localDate
	 */
	public LocalDate toLocalDate()
	{
		return (LocalDate) parseTo(BasicDataType.DATE);
	}
	
	/**
	 * @return The value casted to local date and wrapped to option
	 */
	public Option<LocalDate> toLocalDateOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((LocalDate) safeParseTo(BasicDataType.DATE));
	}
	
	/**
	 * @return The value casted to localDateTime
	 */
	public LocalDateTime toLocalDateTime()
	{
		return (LocalDateTime) parseTo(BasicDataType.DATETIME);
	}
	
	/**
	 * @return The value casted to local date time and wrapped to option
	 */
	public Option<LocalDateTime> toLocalDateTimeOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((LocalDateTime) safeParseTo(BasicDataType.DATETIME));
	}
	
	/**
	 * @return The value casted to local time
	 */
	public LocalTime toLocalTime()
	{
		return (LocalTime) parseTo(BasicDataType.TIME);
	}
	
	/**
	 * @return The value casted to local time and wrapped to option
	 */
	public Option<LocalTime> toLocalTimeOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((LocalTime) safeParseTo(BasicDataType.TIME));
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
	@SuppressWarnings("unchecked")
	public Model<Variable> toModel()
	{
		return (Model<Variable>) parseTo(BasicDataType.MODEL);
	}
	
	/**
	 * @return The value casted to variable declaration
	 */
	public VariableDeclaration toVariableDeclaration()
	{
		return (VariableDeclaration) parseTo(BasicDataType.VARIABLE_DECLARATION);
	}
	
	/**
	 * @return The value casted to model declaration
	 */
	public ModelDeclaration toModelDeclaration()
	{
		return (ModelDeclaration) parseTo(BasicDataType.MODEL_DECLARATION);
	}
	
	/**
	 * @return The value casted to a list. Never null.
	 */
	@SuppressWarnings("unchecked")
	public ImmutableList<Value> toList()
	{
		ImmutableList<Value> list = (ImmutableList<Value>) parseTo(BasicDataType.IMMUTABLE_LIST);
		if (list == null)
			return ImmutableList.empty();
		else
			return list;
	}
	
	/**
	 * @return The value casted to a list or None if casting failed / value was empty
	 */
	@SuppressWarnings("unchecked")
	public Option<ImmutableList<Value>> toListOption()
	{
		if (isNull())
			return Option.none();
		else
			return new Option<>((ImmutableList<Value>) safeParseTo(BasicDataType.IMMUTABLE_LIST));
	}
	
	/**
	 * @return This value as a rich string or none
	 */
	public Option<RichString> toRichStringOption()
	{
		return toStringOption().map(RichString::of);
	}
	
	/**
	 * @return This value as a rich string
	 */
	public RichString toRichString()
	{
		return toRichStringOption().getOrElse(RichString.EMPTY);
	}
	
	/**
	 * @return The value casted to list. If the casting failed, wraps the value into a list 
	 * instead
	 * @deprecated Please use {@link #toList()} instead
	 */
	public ValueList toValueList()
	{
		try
		{
			return (ValueList) parseTo(BasicDataType.LIST);
		}
		catch (DataTypeException e)
		{
			return wrapToList();
		}
	}
	
	/**
	 * @return A value list that contains this value
	 * @deprecated Please use {@link ImmutableList#withValue(Object)} instead
	 */
	public ValueList wrapToList()
	{
		return new ValueList(this);
	}
}
