package flow_generics;

import java.util.Collection;

/**
 * This is a simple implementation of the abstract model class. The attributes are treated 
 * as basic variables.
 * @author Mikko Hilpinen
 * @since 9.1.2016
 */
public class SimpleModel extends Model<Variable, VariableDeclaration>
{
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new empty model
	 */
	public SimpleModel()
	{
		// Simple constructor
	}

	/**
	 * Creates a new model with predefined variables
	 * @param variables The variables the model will have
	 */
	public SimpleModel(Collection<? extends Variable> variables)
	{
		super(variables);
	}
	
	/**
	 * Creates a new model by copying another
	 * @param other A model that will be copied
	 */
	public SimpleModel(Model<? extends Variable, ? extends VariableDeclaration> other)
	{
		super(other);
	}
	
	
	// IMPLEMENTED METHODS	----------------
	
	@Override
	protected Variable generateAttribute(Variable attribute)
	{
		return new Variable(attribute);
	}

	@Override
	protected Variable generateAttribute(String attributeName, Value value)
	{
		return new Variable(attributeName, value);
	}
}
