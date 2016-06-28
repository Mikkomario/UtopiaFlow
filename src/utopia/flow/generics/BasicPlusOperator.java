package utopia.flow.generics;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import utopia.flow.generics.ValueOperation.ValueOperationException;
import utopia.flow.structure.Pair;

/**
 * This value operator is able to perform the plus operation on the applicable basic data types
 * @author Mikko Hilpinen
 * @since 6.12.2015
 * @deprecated Future support for operations has been (temporarily) dropped
 */
public class BasicPlusOperator implements ValueOperator
{
	// TODO: Add support for float
	
	// ATTRIBUTES	----------------------
	
	private static BasicPlusOperator instance = null;
	private Set<Pair<DataType, DataType>> possibleParameters;
	
	
	// CONSTRUCTOR	----------------------
	
	private BasicPlusOperator()
	{
		this.possibleParameters = new HashSet<>();
		
		addTypes(BasicDataType.STRING, BasicDataType.STRING);
		addTypes(BasicDataType.LONG, BasicDataType.LONG);
		addTypes(BasicDataType.LONG, BasicDataType.NUMBER);
		addTypes(BasicDataType.INTEGER, BasicDataType.INTEGER);
		addTypes(BasicDataType.INTEGER, BasicDataType.NUMBER);
		addTypes(BasicDataType.DOUBLE, BasicDataType.DOUBLE);
		addTypes(BasicDataType.DOUBLE, BasicDataType.NUMBER);
		addTypes(BasicDataType.DATE, BasicDataType.LONG);
		addTypes(BasicDataType.DATE, BasicDataType.TIME);
		addTypes(BasicDataType.DATETIME, BasicDataType.LONG);
		addTypes(BasicDataType.DATETIME, BasicDataType.TIME);
		addTypes(BasicDataType.TIME, BasicDataType.LONG);
		addTypes(BasicDataType.TIME, BasicDataType.TIME);
		addTypes(BasicDataType.TIME, BasicDataType.DATE);
		addTypes(BasicDataType.TIME, BasicDataType.DATETIME);
		//addTypes(BasicDataType.VARIABLE, BasicDataType.VARIABLE);
		//addTypes(BasicDataType.VARIABLE, BasicDataType.MODEL);
		addTypes(BasicDataType.MODEL, BasicDataType.MODEL);
		addTypes(BasicDataType.MODEL, BasicDataType.VARIABLE);
		addTypes(BasicDataType.VARIABLE_DECLARATION, BasicDataType.VARIABLE_DECLARATION);
		addTypes(BasicDataType.VARIABLE_DECLARATION, BasicDataType.MODEL_DECLARATION);
		addTypes(BasicDataType.MODEL_DECLARATION, BasicDataType.MODEL_DECLARATION);
		addTypes(BasicDataType.MODEL_DECLARATION, BasicDataType.VARIABLE_DECLARATION);
		addTypes(BasicDataType.VARIABLE, BasicDataType.OBJECT);
		addTypes(BasicDataType.LIST, BasicDataType.OBJECT);
	}
	
	/**
	 * @return The singular plus operator instance
	 */
	public static BasicPlusOperator getInstance()
	{
		if (instance == null)
			instance = new BasicPlusOperator();
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	--------------
	
	@Override
	public ValueOperation getOperation()
	{
		return BasicValueOperation.PLUS;
	}

	@Override
	public Value operate(Value first, Value second) throws ValueOperationException
	{
		if (first == null)
			throw new ValueOperation.ValueOperationException(getOperation(), first, second);
		if (first.isNull() || second == null || second.isNull())
			return first;
		
		DataType firstType = first.getType();
		DataType secondType = second.getType();
		
		// Strings can be combined with other strings
		if (firstType.equals(BasicDataType.STRING))
		{
			if (secondType.equals(BasicDataType.STRING))
				return Value.String(first.toString() + second.toString());
		}
		// Long can be combined with long and number values
		else if (firstType.equals(BasicDataType.LONG))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.Long(first.toLong() + second.toLong());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Long(first.toLong() + second.toNumber().longValue());
		}
		// Integer can be combined with Integer and number values
		else if (firstType.equals(BasicDataType.INTEGER))
		{
			if (secondType.equals(BasicDataType.INTEGER))
				return Value.Integer(first.toInteger() + second.toInteger());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Integer(first.toInteger() + second.toNumber().intValue());
		}
		// Same logic with double
		else if (firstType.equals(BasicDataType.DOUBLE))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toDouble() + second.toDouble());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toDouble() + second.toNumber().doubleValue());
		}
		// One can add long values to dates, with each increment in long representing a new day
		else if (firstType.equals(BasicDataType.DATE))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.Date(first.toLocalDate().plusDays(second.toLong()));
			// It is also possible to add a time to date to form a datetime
			else if (secondType.equals(BasicDataType.TIME))
				return Value.DateTime(first.toLocalDate().atTime(second.toLocalTime()));
		}
		// Date times can add long values as seconds
		else if (firstType.equals(BasicDataType.DATETIME))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.DateTime(first.toLocalDateTime().plusSeconds(second.toLong()));
			// One can also add times
			else if (secondType.equals(BasicDataType.TIME))
				return Value.DateTime(dateTimePlusTime(first.toLocalDateTime(), second.toLocalTime()));
		}
		// A date portion can be added to a time. One can also add long as seconds. A dateTime 
		// can also be added
		else if (firstType.equals(BasicDataType.TIME))
		{
			if (secondType.equals(BasicDataType.DATE))
				return Value.DateTime(second.toLocalDate().atTime(first.toLocalTime()));
			else if (secondType.equals(BasicDataType.DATETIME))
				return Value.DateTime(dateTimePlusTime(second.toLocalDateTime(), 
						first.toLocalTime()));
			else if (secondType.equals(BasicDataType.LONG))
				return Value.Time(first.toLocalTime().plusSeconds(second.toLong()));
			else if (secondType.equals(BasicDataType.TIME))
			{
				LocalTime time = first.toLocalTime();
				LocalTime other = second.toLocalTime();
				
				return Value.Time(time.plusNanos(other.toNanoOfDay()));
			}
		}
		// Variables can be combined with variables and models to create models
		// Also, variables can try to combine their values with the provided value
		else if (firstType.equals(BasicDataType.VARIABLE))
		{
			if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Model(first.toVariable().plus(second.toVariable()));
			else if (secondType.equals(BasicDataType.MODEL))
				return Value.Model(second.toModel().plus(first.toVariable()));
			else
				return Value.Variable(first.toVariable().plus(second));
		}
		// Models can be combined with variables and other models
		else if (firstType.equals(BasicDataType.MODEL))
		{
			if (secondType.equals(BasicDataType.MODEL))
				return Value.Model(first.toModel().plus(second.toModel()));
			else if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Model(first.toModel().plus(second.toVariable()));
		}
		// Variable declarations can be combined with each other and model declarations
		else if (firstType.equals(BasicDataType.VARIABLE_DECLARATION))
		{
			if (secondType.equals(BasicDataType.VARIABLE_DECLARATION))
				return Value.ModelDeclaration(first.toVariableDeclaration().plus(
						second.toVariableDeclaration()));
			else if (secondType.equals(BasicDataType.MODEL_DECLARATION))
				return Value.ModelDeclaration(second.toModelDeclaration().plus(
						first.toVariableDeclaration()));
		}
		// Model declarations can be combined with each other and variable declarations
		else if (firstType.equals(BasicDataType.MODEL_DECLARATION))
		{
			if (secondType.equals(BasicDataType.MODEL_DECLARATION))
				return Value.ModelDeclaration(first.toModelDeclaration().plus(
						second.toModelDeclaration()));
			else if (secondType.equals(BasicDataType.VARIABLE_DECLARATION))
				return Value.ModelDeclaration(first.toModelDeclaration().plus(
						second.toVariableDeclaration()));
		}
		// One can add values to lists or combine lists with each other
		else if (firstType.equals(BasicDataType.LIST))
		{
			if (secondType.equals(BasicDataType.LIST))
				return Value.List(first.toList().plus(second.toList()));
			else
				return Value.List(first.toList().plus(second));
		}
			
		// Other operations are impossible
		throw new ValueOperation.ValueOperationException(getOperation(), first, second);
	}

	@Override
	public Set<? extends Pair<DataType, DataType>> getPossibleParameterTypes()
	{
		return this.possibleParameters;
	}
	
	
	// OTHER METHODS	-----------------
	
	private void addTypes(DataType firstType, DataType secondType)
	{
		this.possibleParameters.add(new Pair<>(firstType, secondType));
	}
	
	private static LocalDateTime dateTimePlusTime(LocalDateTime dateTime, LocalTime time)
	{	
		return dateTime.plusNanos(time.toNanoOfDay());
	}
}
