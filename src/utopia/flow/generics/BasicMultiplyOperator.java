package utopia.flow.generics;

import java.util.HashSet;
import java.util.Set;

import utopia.flow.generics.ValueOperation.ValueOperationException;
import utopia.flow.structure.Pair;

/**
 * This class is able to perform multiplication between the basic data types
 * @author Mikko Hilpinen
 * @since 24.12.2015
 */
public class BasicMultiplyOperator implements ValueOperator
{
	// ATTRIBUTES	----------------
	
	private static BasicMultiplyOperator instance = null;
	private Set<Pair<DataType, DataType>> parameterTypes;
	
	
	// CONSTRUCTOR	----------------
	
	private BasicMultiplyOperator()
	{
		this.parameterTypes = new HashSet<>();
		
		addTypes(BasicDataType.STRING, BasicDataType.INTEGER);
		
		addTypes(BasicDataType.INTEGER, BasicDataType.DOUBLE);
		addTypes(BasicDataType.INTEGER, BasicDataType.INTEGER);
		addTypes(BasicDataType.INTEGER, BasicDataType.LONG);
		addTypes(BasicDataType.INTEGER, BasicDataType.NUMBER);
		
		addTypes(BasicDataType.DOUBLE, BasicDataType.DOUBLE);
		addTypes(BasicDataType.DOUBLE, BasicDataType.NUMBER);
		
		addTypes(BasicDataType.LONG, BasicDataType.DOUBLE);
		addTypes(BasicDataType.LONG, BasicDataType.LONG);
		addTypes(BasicDataType.LONG, BasicDataType.INTEGER);
		addTypes(BasicDataType.LONG, BasicDataType.NUMBER);
		
		addTypes(BasicDataType.VARIABLE, BasicDataType.OBJECT);
	}
	
	/**
	 * @return The static multiply operator instance
	 */
	public static BasicMultiplyOperator getInstance()
	{
		if (instance == null)
			instance = new BasicMultiplyOperator();
		
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	--------

	@Override
	public ValueOperation getOperation()
	{
		return BasicValueOperation.MULTIPLY;
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
		
		// Strings can be multiplied with integer values
		if (firstType.equals(BasicDataType.STRING))
		{
			if (secondType.equals(BasicDataType.INTEGER))
			{
				int integer = second.toInteger();
				if (integer <= 0)
					return Value.String("");
				else
				{
					String string = first.toString();
					StringBuilder s = new StringBuilder();
					for (int i = 0; i < integer; i++)
					{
						s.append(string);
					}
					
					return Value.String(s.toString());
				}
			}
		}
		// Numbers can be multiplied with each other as well
		else if (firstType.equals(BasicDataType.INTEGER))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toInteger() * second.toDouble());
			else if (secondType.equals(BasicDataType.INTEGER))
				return Value.Integer(first.toInteger() * second.toInteger());
			else if (secondType.equals(BasicDataType.LONG))
				return Value.Long(first.toInteger() * second.toLong());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toInteger() * second.toNumber().doubleValue());
		}
		else if (firstType.equals(BasicDataType.DOUBLE))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toDouble() * second.toDouble());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toDouble() * second.toNumber().doubleValue());
		}
		else if (firstType.equals(BasicDataType.LONG))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toLong() * second.toDouble());
			else if (secondType.equals(BasicDataType.LONG))
				return Value.Long(first.toLong() * second.toLong());
			else if (secondType.equals(BasicDataType.INTEGER))
				return Value.Long(first.toLong() * second.toInteger());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toLong() * second.toNumber().doubleValue());
		}
		// Variable values may also be operatable
		else if (firstType.equals(BasicDataType.VARIABLE))
		{
			if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Variable(first.toVariable().times(second.toVariable().getValue()));
			else
				return Value.Variable(first.toVariable().times(second));
		}
		
		throw new ValueOperationException(getOperation(), first, second);
	}

	@Override
	public Set<? extends Pair<DataType, DataType>> getPossibleParameterTypes()
	{
		return this.parameterTypes;
	}
	
	
	// OTHER METHODS	-----------------
	
	private void addTypes(BasicDataType first, BasicDataType second)
	{
		this.parameterTypes.add(new Pair<>(first, second));
	}
}
