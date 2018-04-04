package utopia.flow.generics;

/**
 * This variable parser creates new variables, but may fail if trying to generate a variable 
 * without a value.
 * @author Mikko Hilpinen
 * @since 30.4.2016
 */
public class BasicVariableParser implements VariableParser<Variable>
{
	// ATTRIBUTES	---------------
	
	private Value defaultValue;
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new variable parser. This version will fail when trying to generate variables 
	 * without a provided value
	 */
	public BasicVariableParser()
	{
		this.defaultValue = null;
	}
	
	/**
	 * Creates a new variable parser. The provided default value will be used when generating 
	 * new attributes without an existing value.
	 * @param defaultValue The value (and data type) assigned to the newly generated attributes 
	 * when there's no alternative.
	 */
	public BasicVariableParser(Value defaultValue)
	{
		this.defaultValue = defaultValue;
	}
	
	
	// IMPLEMENTED METHODS	-------
	
	@Override
	public Variable generate(String variableName) throws VariableGenerationFailedException
	{
		// The basic parser can't generate new variables without data types
		if (this.defaultValue == null)
			throw new VariableGenerationFailedException("Can't generate a new variable without a data type");
		else
			return new Variable(variableName, this.defaultValue);
	}

	@Override
	public Variable generate(String variableName, Value value)
	{
		return new Variable(variableName, value);
	}

	@Override
	public Variable copy(Variable variable)
	{
		return new Variable(variable);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.defaultValue == null) ? 0 : this.defaultValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BasicVariableParser))
			return false;
		BasicVariableParser other = (BasicVariableParser) obj;
		if (this.defaultValue == null)
		{
			if (other.defaultValue != null)
				return false;
		}
		else if (!this.defaultValue.equals(other.defaultValue))
			return false;
		return true;
	}
}
