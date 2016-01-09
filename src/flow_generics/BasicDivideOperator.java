package flow_generics;

import java.util.HashSet;
import java.util.Set;

import flow_generics.ValueOperation.ValueOperationException;
import flow_structure.Pair;

/**
 * This class is able to perform divide operation on the basic value types
 * @author Mikko Hilpinen
 * @since 25.12.2015
 */
public class BasicDivideOperator implements ValueOperator
{
	// ATTRIBUTES	-------------------
	
	private static BasicDivideOperator instance = null;
	private Set<Pair<DataType, DataType>> parameterTypes;
	
	
	// CONSTRUCTOR	------------------
	
	private BasicDivideOperator()
	{
		this.parameterTypes = new HashSet<>();
		
		addTypes(BasicDataType.INTEGER, BasicDataType.DOUBLE);
		addTypes(BasicDataType.INTEGER, BasicDataType.LONG);
		addTypes(BasicDataType.INTEGER, BasicDataType.INTEGER);
		addTypes(BasicDataType.INTEGER, BasicDataType.NUMBER);
		
		addTypes(BasicDataType.LONG, BasicDataType.DOUBLE);
		addTypes(BasicDataType.LONG, BasicDataType.LONG);
		addTypes(BasicDataType.LONG, BasicDataType.INTEGER);
		addTypes(BasicDataType.LONG, BasicDataType.NUMBER);
		
		addTypes(BasicDataType.DOUBLE, BasicDataType.DOUBLE);
		addTypes(BasicDataType.DOUBLE, BasicDataType.NUMBER);
		
		addTypes(BasicDataType.VARIABLE, BasicDataType.OBJECT);
	}
	
	/**
	 * @return The static operator instance
	 */
	public static BasicDivideOperator getInstance()
	{
		if (instance == null)
			instance = new BasicDivideOperator();
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	--------

	@Override
	public ValueOperation getOperation()
	{
		return BasicValueOperation.DIVIDE;
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
		
		// Numbers can be divided with each other
		if (firstType.equals(BasicDataType.INTEGER))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toInteger() / second.toDouble());
			else if (secondType.equals(BasicDataType.INTEGER))
				return Value.Integer(first.toInteger() / second.toInteger());
			else if (secondType.equals(BasicDataType.LONG))
				return Value.Integer((int) (first.toInteger() / second.toLong()));
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toInteger() / second.toNumber().doubleValue());
		}
		else if (firstType.equals(BasicDataType.LONG))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toLong() / second.toDouble());
			else if (secondType.equals(BasicDataType.LONG))
				return Value.Long(first.toLong() / second.toLong());
			else if (secondType.equals(BasicDataType.INTEGER))
				return Value.Long(first.toLong() / second.toInteger());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toLong() / second.toNumber().doubleValue());
		}
		else if (firstType.equals(BasicDataType.DOUBLE))
		{
			if (secondType.equals(BasicDataType.DOUBLE))
				return Value.Double(first.toDouble() / second.toDouble());
			else if (secondType.equals(BasicDataType.NUMBER))
				return Value.Double(first.toDouble() / second.toNumber().doubleValue());
		}
		else if (firstType.equals(BasicDataType.VARIABLE))
		{
			if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Variable(first.toVariable().divided(second.toVariable().getValue()));
			else
				return Value.Variable(first.toVariable().divided(second));
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
