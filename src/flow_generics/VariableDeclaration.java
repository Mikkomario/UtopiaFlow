package flow_generics;

import java.util.ArrayList;
import java.util.List;

/**
 * A variableDeclaration declares a variable, but doesn't have a value. Variable declarations 
 * are immutable.
 * @author Mikko Hilpinen
 * @since 4.12.2015
 */
public class VariableDeclaration
{
	// ATTRIBUTES	-------------------
	
	private String name;
	private DataType type;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new variable declaration
	 * @param name The name of the variable
	 * @param dataType The data type of the variable
	 */
	public VariableDeclaration(String name, DataType dataType)
	{
		this.name = name;
		this.type = dataType;
	}

	
	// IMPLEMENTED METHODS	-----------------
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariableDeclaration))
			return false;
		
		VariableDeclaration other = (VariableDeclaration) obj;
		
		if (getName() == null)
		{
			if (other.getName() != null)
				return false;
		}
		else if (!getName().equals(other.getName()))
			return false;
		
		if (getType() == null)
		{
			if (other.getType() != null)
				return false;
		}
		else if (!getType().equals(other.getType()))
			return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		return getName() + " (" + getType() + ")";
	}

	
	// ACCESSORS	------------------

	/**
	 * @return The name of the declared variable
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * @return The data type of the declared variable
	 */
	public DataType getType()
	{
		return this.type;
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Creates a new variable from the declaration
	 * @param value The value assigned to the variable
	 * @return A variable with the provided value. The value will be cast to the correct 
	 * data type.
	 */
	public Variable assignValue(Value value)
	{
		return new Variable(getName(), getType(), value);
	}
	
	/**
	 * Creates a new variable from the declaration
	 * @param value The value assigned to the variable. Should reflect the declaration's type
	 * @return A variable with the provided value.
	 */
	public Variable assignValue(Object value)
	{
		return new Variable(getName(), getType(), value);
	}
	
	/**
	 * Creates a new variable from the declaration
	 * @param value The value assigned to the variable
	 * @param valueType The data type of the provided object value
	 * @return A variable with the provided value. The value will be cast to the correct 
	 * data type.
	 */
	public Variable assignValue(Object value, DataType valueType)
	{
		return new Variable(getName(), getType(), value, valueType);
	}
	
	/**
	 * Creates a new variable from the declaration
	 * @return A variable with the provided value. The variable will have a null value
	 */
	public Variable assignNullValue()
	{
		return new Variable(getName(), getType());
	}
	
	/**
	 * Wraps this variable declaration into a model declaration.
	 * @return A model declaration with just this one declaration.
	 */
	public ModelDeclaration wrapToModelDeclaration()
	{
		List<VariableDeclaration> declarations = new ArrayList<>();
		declarations.add(this);
		return new ModelDeclaration(declarations);
	}
}
