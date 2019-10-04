package utopia.java.flow.generics;

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
	private Value defaultValue;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new variable declaration. Null values are assigned by default.
	 * @param name The name of the variable
	 * @param dataType The data type of the variable
	 */
	public VariableDeclaration(String name, DataType dataType)
	{
		this.name = name;
		this.type = dataType;
		this.defaultValue = Value.NullValue(getType());
	}
	
	/**
	 * Creates a new variable declaration
	 * @param name The name of the variable
	 * @param defaultValue The default value used by this declaration. This also determines 
	 * the declaration's data type
	 */
	public VariableDeclaration(String name, Value defaultValue)
	{
		this.name = name;
		this.type = defaultValue.getType();
		this.defaultValue = defaultValue;
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
	
	/**
	 * @return The default value assigned with this declaration
	 */
	public Value getDefaultValue()
	{
		return this.defaultValue;
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Creates a new variable from the declaration
	 * @param parser The parser that is used for actually generating the variable
	 * @param value The value assigned to the variable
	 * @return A variable with the provided value. The value will be cast to the correct 
	 * data type.
	 */
	public <VariableType extends Variable> VariableType assignValue(
			VariableParser<VariableType> parser, Value value)
	{
		return parser.generate(getName(), value.castTo(getType()));
	}
	
	/**
	 * Creates a new variable from the declaration
	 * @param parser The parser that is used for actually generating the variable
	 * @return A variable with the provided value. The variable will have a null value
	 */
	public <VariableType extends Variable> VariableType assignDefaultValue(
			VariableParser<VariableType> parser)
	{
		return assignValue(parser, getDefaultValue());
	}
	
	/**
	 * Assigns a value to the declaration, creating a basic variable
	 * @param value The value assigned to the declaration
	 * @return The variable that was generated
	 */
	public Variable assignValue(Value value)
	{
		return assignValue(new BasicVariableParser(), value);
	}
	
	/**
	 * Assigns the declaration's default value to a generated variable
	 * @return The variable that was generated
	 */
	public Variable assignDefaultValue()
	{
		return assignValue(getDefaultValue());
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
	
	/**
	 * Combines the two variable declarations into a model declaration
	 * @param other Another variable declaration
	 * @return A model declaration that contains the two variable declarations
	 */
	public ModelDeclaration plus(VariableDeclaration other)
	{
		List<VariableDeclaration> declarations = new ArrayList<>();
		declarations.add(this);
		if (other != null)
			declarations.add(other);
		
		return new ModelDeclaration(declarations);
	}
}
