package utopia.java.flow.generics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import utopia.java.flow.structure.Node;
import utopia.java.flow.util.ExtraBoolean;

/**
 * Variable's have an inmutable data type and name, but their values can change.
 * @author Mikko Hilpinen
 * @since 10.11.2015
 */
public class Variable implements Node<Value>
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
	 * Creates a new variable with a null value.
	 * @param name The name of the variable.
	 * @param type The data type of the variable.
	 */
	public Variable(String name, DataType type)
	{
		this.dataType = type;
		this.name = name;
		this.value = Value.NullValue(type);
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
		this.value = new Value(value, type);
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
		if (value != null)
			this.value = value.castTo(type);
		else
			this.value = Value.NullValue(type);
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
		this.value = new Value(value, valueType).castTo(type);
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
	
	/**
	 * @return Copies another variable
	 */
	public Variable copy()
	{
		return new Variable(this);
	}
	
	
	// IMPLEMENTED METHODS	----------------

	/**
	 * @return The value currently stored in this variable
	 */
	public Value getValue()
	{
		return this.value;
	}
	
	@Override
	public Value getContent()
	{
		return getValue();
	}

	/**
	 * @return The variable's data type
	 */
	public DataType getType()
	{
		return this.dataType;
	}

	/**
	 * @return Does the variable hold a null value
	 */
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
		result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
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
		
		Variable other = (Variable) obj;
		if (getName() == null)
		{
			if (other.getName() != null)
				return false;
		}
		else if (other.getName() == null)
			return false;
		else if (!getName().equals(other.getName()))
			return false;
		
		if (!hasEqualValueWith(other))
			return false;
		
		return true;
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
		this.value = value.castTo(getType());
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Wraps the variable to a model
	 * @return A model that contains only this one variable
	 */
	public Model<Variable> wrapToModel()
	{
		Model<Variable> model = Model.createBasicModel();
		model.addAttribute(this, true);
		return model;
	}
	
	/**
	 * @return A declaration for a variable similar to this
	 */
	public VariableDeclaration getDeclaration()
	{
		return new VariableDeclaration(getName(), getType());
	}
	
	/**
	 * Combines the two variables into a model
	 * @param other another variable
	 * @return A model containing the two variables
	 */
	public Model<Variable> plus(Variable other)
	{
		Model<Variable> model = Model.createBasicModel();
		model.addAttribute(this, true);
		
		if (other != null)
			model.addAttribute(other, true);
		return model;
	}
	
	/**
	 * Adds a new value to the variables existing value (+=)
	 * @param value A value
	 * @see #plus(Value)
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public void add(Value value)
	{
		setValue(getValue().plus(value));
	}
	
	/**
	 * Returns a combination of this variable and the value. This creates a new variable 
	 * entirely (+). This won't affect this variable instance.
	 * @param value The value that is combined with this variable.
	 * @return A variable that contains the combined values.
	 * @see #add(Value)
	 */
	public Variable plus(Value value)
	{
		Variable copy = new Variable(this);
		copy.add(value);
		return copy;
	}
	
	/**
	 * Subtracts a value from the variable's existing value (-=)
	 * @param value A value
	 * @see #minus(Value)
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public void subtract(Value value)
	{
		setValue(getValue().minus(value));
	}
	
	/**
	 * Returns a subtraction of this variable and the provided value. This creates a new 
	 * variable instance and won't affect this variable.
	 * @param value The value that is subtracted with this variable
	 * @return A new variable that contains the value after the operation
	 * @see #subtract(Value)
	 */
	public Variable minus(Value value)
	{
		Variable copy = new Variable(this);
		copy.subtract(value);
		return copy;
	}
	
	/**
	 * Multiplies the value in this variable
	 * @param value A value that is multiplied with the variable's value
	 * @see #times(Value)
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public void multiply(Value value)
	{
		setValue(getValue().times(value));
	}
	
	/**
	 * Returns a multiplication of this variable and the provided value. This creates a new 
	 * variable and won't affect this instance.
	 * @param value The value that is multiplied with this variable's value
	 * @return A new variable with the multiplied value
	 * @see #multiply(Value)
	 */
	public Variable times(Value value)
	{
		Variable copy = new Variable(this);
		copy.multiply(value);
		return copy;
	}
	
	/**
	 * Divides the value of this variable
	 * @param value The value that divides this variable's value
	 * @see #divided(Value)
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public void divide(Value value)
	{
		setValue(getValue().divided(value));
	}
	
	/**
	 * Returns a new variable with this variable's value divided with another value. This 
	 * method doesn't affect this variable instance.
	 * @param value A value that is used in the operation
	 * @return A new variable with the divided value
	 * @see #divide(Value)
	 */
	public Variable divided(Value value)
	{
		Variable copy = new Variable(this);
		copy.divide(value);
		return copy;
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
		
		if (getValue() == null)
			return other.getValue() == null;
		else if (other.getValue() == null)
			return false;
		else
			return getValue().equals(other.getValue());
	}
	
	/**
	 * Assigns a new object value to this variable. The provided value may be cast to the 
	 * correct type.
	 * @param value an object value
	 * @param type The data type that of the provided value
	 */
	public void setValue(Object value, DataType type)
	{
		this.value = new Value(value, type).castTo(getType());
	}
	
	/**
	 * Returns the object value of this variable. The value will be cast to the correct 
	 * data type.
	 * @param desiredType The data type the object value is required in
	 * @return The variable's object value in the correct data type
	 */
	public Object getObjectValue(DataType desiredType)
	{
		return getValue().parseTo(desiredType);
	}
	
	/**
	 * @return The variable's value as a string
	 */
	public String getStringValue()
	{
		return getValue().toString();
	}
	
	/**
	 * @return The variable's value as a number
	 */
	public Number getNumberValue()
	{
		return getValue().toNumber();
	}
	
	/**
	 * @return The variable's value as an integer
	 */
	public Integer getIntegerValue()
	{
		return getValue().toInteger();
	}
	
	/**
	 * @return The variable's value as a double
	 */
	public Double getDoubleValue()
	{
		return getValue().toDouble();
	}
	
	/**
	 * @return The variable's value as a long
	 */
	public Long getLongValue()
	{
		return getValue().toLong();
	}
	
	/**
	 * @return The variable's value as a boolean
	 */
	public Boolean getBooleanValue()
	{
		return getValue().toBoolean();
	}
	
	/**
	 * @return The variable's value as an extra boolean
	 */
	public ExtraBoolean getExtraBooleanValue()
	{
		return getValue().toExtraBoolean();
	}
	
	/**
	 * @return The variable's value as a localDate
	 */
	public LocalDate getDateValue()
	{
		return getValue().toLocalDate();
	}
	
	/**
	 * @return The variable's value as a localDateTime
	 */
	public LocalDateTime getDateTimeValue()
	{
		return getValue().toLocalDateTime();
	}
	
	/**
	 * @return The variable's value as a localTime
	 */
	public LocalTime getTimeValue()
	{
		return getValue().toLocalTime();
	}
	
	/**
	 * @return The variable's value as a variable
	 */
	public Variable getVariableValue()
	{
		return getValue().toVariable();
	}
	
	/**
	 * @return The variable's value as a model
	 */
	public Model<Variable> getModelValue()
	{
		return getValue().toModel();
	}
	
	/**
	 * @return The variable's value as a variable declaration
	 */
	public VariableDeclaration getVariableDeclarationValue()
	{
		return getValue().toVariableDeclaration();
	}
	
	/**
	 * @return The variable's value as a model declaration
	 */
	public ModelDeclaration getModelDeclarationValue()
	{
		return getValue().toModelDeclaration();
	}
	
	/**
	 * @return The variable's valuea as a value list
	 * @deprecated Please used {@link Value#toList()} instead
	 */
	public ValueList getListValue()
	{
		return getValue().toValueList();
	}
}
