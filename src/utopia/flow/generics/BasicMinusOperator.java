package utopia.flow.generics;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import utopia.flow.generics.ValueOperation.ValueOperationException;
import utopia.flow.structure.Pair;

/**
 * This operator is able to perform minus operations on the basic data types
 * @author Mikko Hilpinen
 * @since 8.12.2015
 */
public class BasicMinusOperator implements ValueOperator
{
	// ATTRIBUTES	-----------------
	
	private Set<Pair<DataType, DataType>> parameterPairs;
	private static BasicMinusOperator instance;
	
	
	// CONSTRUCTOR	-----------------
	
	private BasicMinusOperator()
	{
		this.parameterPairs = new HashSet<>();
		
		addTypes(BasicDataType.STRING, BasicDataType.STRING);
		addTypes(BasicDataType.STRING, BasicDataType.INTEGER);
		addTypes(BasicDataType.INTEGER, BasicDataType.INTEGER);
		addTypes(BasicDataType.INTEGER, BasicDataType.NUMBER);
		addTypes(BasicDataType.DOUBLE, BasicDataType.DOUBLE);
		addTypes(BasicDataType.DOUBLE, BasicDataType.NUMBER);
		addTypes(BasicDataType.LONG, BasicDataType.LONG);
		addTypes(BasicDataType.LONG, BasicDataType.NUMBER);
		addTypes(BasicDataType.DATE, BasicDataType.LONG);
		addTypes(BasicDataType.DATETIME, BasicDataType.LONG);
		addTypes(BasicDataType.DATETIME, BasicDataType.TIME);
		addTypes(BasicDataType.TIME, BasicDataType.TIME);
		addTypes(BasicDataType.TIME, BasicDataType.LONG);
		addTypes(BasicDataType.MODEL, BasicDataType.VARIABLE);
		addTypes(BasicDataType.MODEL, BasicDataType.VARIABLE_DECLARATION);
		addTypes(BasicDataType.MODEL, BasicDataType.MODEL_DECLARATION);
		addTypes(BasicDataType.MODEL_DECLARATION, BasicDataType.MODEL_DECLARATION);
		addTypes(BasicDataType.MODEL_DECLARATION, BasicDataType.VARIABLE_DECLARATION);
		addTypes(BasicDataType.VARIABLE, BasicDataType.OBJECT);
	}
	
	/**
	 * @return The singular operator instance
	 */
	public static BasicMinusOperator getInstance()
	{
		if (instance == null)
			instance = new BasicMinusOperator();
		
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public ValueOperation getOperation()
	{
		return BasicValueOperation.MINUS;
	}

	@Override
	public Value operate(Value first, Value second)
			throws ValueOperationException
	{
		if (first == null)
			throw new ValueOperationException(getOperation(), first, second);
		if (first.isNull() || second == null || second.isNull())
			return first;
		
		DataType firstType = first.getType();
		DataType secondType = second.getType();
			
		// One can substract strings from strings (removes the substring)
		// But one can also substract an integer from a string to remove a number of 
		// characters from the string
		if (firstType.equals(BasicDataType.STRING))
		{
			if (secondType.equals(BasicDataType.STRING))
				return Value.String(first.toString().replaceAll(second.toString(), ""));
			else if (secondType.equals(BasicDataType.INTEGER))
			{
				String s = first.toString();
				int i = second.toInteger();
				
				if (i < 0)
					throw new ValueOperationException(getOperation(), first, second);
				else if (s.length() <= i)
					return Value.String("");
				else
					return Value.String(s.substring(0, s.length() - i));
			}
		}
		// Numbers can be substracted from each other
		else if (firstType.equals(BasicDataType.INTEGER))
		{
			if (secondType.equals(BasicDataType.INTEGER))
				return Value.Integer(first.toInteger() - second.toInteger());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Integer(first.toInteger() - second.toNumber().intValue());
		}
		else if (firstType.equals(BasicDataType.DOUBLE))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toDouble() - second.toDouble());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toDouble() - second.toNumber().doubleValue());
		}
		else if (firstType.equals(BasicDataType.LONG))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.Long(first.toLong() - second.toLong());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Long(first.toLong() - second.toNumber().longValue());
		}
		// One can subtract a long value from date, time and dateTime
		else if (firstType.equals(BasicDataType.DATE))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.Date(first.toLocalDate().minusDays(second.toLong()));
		}
		else if (firstType.equals(BasicDataType.DATETIME))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.DateTime(first.toLocalDateTime().minusSeconds(second.toLong()));
			// Also time from dateTime
			else if (secondType.equals(BasicDataType.TIME))
			{
				LocalDateTime dateTime = first.toLocalDateTime();
				LocalTime time = second.toLocalTime();
				return Value.DateTime(dateTime.minusNanos(time.toNanoOfDay()));
			}
		}
		else if (firstType.equals(BasicDataType.TIME))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.Time(first.toLocalTime().minusSeconds(second.toLong()));
			else if (secondType.equals(BasicDataType.TIME))
			{
				LocalTime time = first.toLocalTime();
				LocalTime other = second.toLocalTime();
				return Value.Time(time.minusNanos(other.toNanoOfDay()));
			}
		}
		// Variable values may be operatable
		else if (firstType.equals(BasicDataType.VARIABLE))
		{
			if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Variable(first.toVariable().minus(second.toVariable().getValue()));
			else
				return Value.Variable(first.toVariable().minus(second));
		}
		// Variables can be removed from models
		// Also declarations
		else if (firstType.equals(BasicDataType.MODEL))
		{
			if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Model(first.toModel().minus(second.toVariable()));
			else if (secondType.equals(BasicDataType.VARIABLE_DECLARATION))
				return Value.Model(first.toModel().minus(second.toVariableDeclaration()));
			else if (secondType.equals(BasicDataType.MODEL_DECLARATION))
				return Value.Model(first.toModel().minus(second.toModelDeclaration()));
		}
		// Variable declarations can be removed from model declarations
		else if (firstType.equals(BasicDataType.MODEL_DECLARATION))
		{
			if (secondType.equals(BasicDataType.MODEL_DECLARATION))
				return Value.ModelDeclaration(first.toModelDeclaration().minus(second.toModelDeclaration()));
			else if (secondType.equals(BasicDataType.VARIABLE_DECLARATION))
				return Value.ModelDeclaration(first.toModelDeclaration().minus(second.toVariableDeclaration()));
		}
		// Values can be taken away from lists
		else if (firstType.equals(BasicDataType.LIST))
		{
			if (secondType.equals(BasicDataType.LIST))
				return Value.List(first.toList().minus(second.toList()));
			else
				return Value.List(first.toList().minus(second));
		}
		
		throw new ValueOperationException(getOperation(), first, second);
	}

	@Override
	public Set<? extends Pair<DataType, DataType>> getPossibleParameterTypes()
	{
		return this.parameterPairs;
	}

	
	// OTHER METHODS	----------------
	
	private void addTypes(DataType firstType, DataType secondType)
	{
		this.parameterPairs.add(new Pair<>(firstType, secondType));
	}
}
