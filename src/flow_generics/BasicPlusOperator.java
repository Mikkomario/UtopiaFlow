package flow_generics;

import java.util.HashSet;
import java.util.Set;

import flow_generics.ValueOperation.ValueOperationException;
import flow_structure.Pair;

/**
 * This value operator is able to perform the plus operation on the applicable basic data types
 * @author Mikko Hilpinen
 * @since 6.12.2015
 */
public class BasicPlusOperator implements ValueOperator
{
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
		addTypes(BasicDataType.DATETIME, BasicDataType.LONG);
		addTypes(BasicDataType.VARIABLE, BasicDataType.VARIABLE);
		addTypes(BasicDataType.VARIABLE, BasicDataType.MODEL);
		addTypes(BasicDataType.MODEL, BasicDataType.MODEL);
		addTypes(BasicDataType.MODEL, BasicDataType.VARIABLE);
		addTypes(BasicDataType.VARIABLE_DECLARATION, BasicDataType.VARIABLE_DECLARATION);
		addTypes(BasicDataType.VARIABLE_DECLARATION, BasicDataType.MODEL_DECLARATION);
		addTypes(BasicDataType.MODEL_DECLARATION, BasicDataType.MODEL_DECLARATION);
		addTypes(BasicDataType.MODEL_DECLARATION, BasicDataType.VARIABLE_DECLARATION);
		
		// TODO: Add support for variable + any (tries to add the second value to the 
		// variable's value)
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
		if (second == null)
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
		}
		// Date times can add long values as seconds
		else if (firstType.equals(BasicDataType.DATETIME))
		{
			if (secondType.equals(BasicDataType.LONG))
				return Value.DateTime(first.toLocalDateTime().plusSeconds(second.toLong()));
		}
		// Variables can be combined with variables and models to create models
		else if (firstType.equals(BasicDataType.VARIABLE))
		{
			if (secondType.equals(BasicDataType.VARIABLE))
				return Value.Model(first.toVariable().plus(second.toVariable()));
			else if (secondType.equals(BasicDataType.MODEL))
				return Value.Model(second.toModel().plus(first.toVariable()));
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
}
