package utopia.flow.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Model is a collection of variables that works much like a case-insensitive map would
 * @author Mikko Hilpinen
 * @param <VariableType> The type of variable contained within this model
 * @param <DeclarationType> The type of variable declaration used with this model
 * @since 11.11.2015
 */
public abstract class Model<VariableType extends Variable, DeclarationType extends VariableDeclaration>
{
	// ATTRIBUTES	--------------------
	
	private Set<VariableType> attributes;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new empty model
	 */
	public Model()
	{
		this.attributes = new HashSet<>();
	}

	/**
	 * Creates a new model with predefined variables
	 * @param variables The variables the model will have
	 */
	public Model(Collection<? extends VariableType> variables)
	{
		this.attributes = new HashSet<>(variables);
	}
	
	/**
	 * Creates a new model by copying another
	 * @param other A model that will be copied
	 */
	public Model(Model<? extends VariableType, ? extends DeclarationType> other)
	{
		this.attributes = new HashSet<>();
		
		if (other != null)
			addAttributes(other.getAttributes(), true);
	}
	
	
	// ABSTRACT METHODS	-----------------
	
	/**
	 * This method generates a new attribute of the desired type by copying another
	 * @param attribute Another attribute
	 * @return A copy of the provided attribute
	 */
	protected abstract VariableType generateAttribute(VariableType attribute);
	
	/**
	 * This method generates a new attribute
	 * @param attributeName The desired name of the attribute
	 * @param value The value given to the attribute
	 * @return A new attribute
	 */
	protected abstract VariableType generateAttribute(String attributeName, Value value);
	
	
	// IMPLEMENTED METHODS	-------------
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		s.append("Model");
		for (Variable attribute : getAttributes())
		{
			s.append("\n");
			s.append(attribute);
		}
		
		return s.toString();
	}
	
	
	// ACCESSORS	---------------------
	
	/**
	 * @return The attributes stored into this model. The returned set is a copy of the 
	 * model's original set and changes made to it won't affect the model.
	 */
	public Set<VariableType> getAttributes()
	{
		return new HashSet<>(this.attributes);
	}
	
	
	// OTHER METHODS	-----------------
	
	/**
	 * Finds an attribute with the provided name from this model (case-insensitive)
	 * @param attributeName The name of the attribute
	 * @return A model's attribute
	 * @throws NoSuchAttributeException If the model doesn't contain such an attribute
	 */
	public VariableType getAttribute(String attributeName) throws NoSuchAttributeException
	{
		VariableType attribute = findAttribute(attributeName);
		if (attribute == null)
			throw new NoSuchAttributeException(attributeName, this);
		else
			return attribute;
	}
	
	/**
	 * Finds an attribute with the provided name from this model (case-insensitive). If no 
	 * such attribute exists, a new one will be generated from the provided value.
	 * @param attributeName The name of the attribute
	 * @param defaultValue The value given to a generated attribute if there didn't exist 
	 * any in the model already
	 * @return A model's attribute
	 */
	public VariableType getAttribute(String attributeName, Value defaultValue)
	{
		VariableType attribute = findAttribute(attributeName);
		if (attribute == null)
		{
			VariableType generated = generateAttribute(attributeName, defaultValue);
			addAttribute(generated, false);
			return generated;
		}
		else
			return attribute;
	}
	
	/**
	 * Finds the value of a single attribute in the model
	 * @param attributeName The name of the attribute
	 * @return The value assigned to the attribute
	 */
	public Value getAttributeValue(String attributeName)
	{
		return getAttribute(attributeName).getValue();
	}
	
	/**
	 * Finds an attribute with the provided name from this model (case-insensitive)
	 * @param attributeName The name of the attribute
	 * @return A model's attribute with the provided name or null if no such attribute exists.
	 */
	public VariableType findAttribute(String attributeName)
	{
		if (attributeName == null)
			return null;
		
		for (VariableType attribute : this.attributes)
		{
			if (attribute.getName().equalsIgnoreCase(attributeName))
				return attribute;
		}
		
		return null;
	}
	
	/**
	 * Finds an attribute that matches the provided attribute (== has the same name)
	 * @param attribute An attribute
	 * @return The model's corresponding attribute or null if the model doesn't contain such 
	 * an attribute
	 */
	public Variable findCorrespondingAttribute(Variable attribute)
	{
		return findAttribute(attribute.getName());
	}
	
	/**
	 * Finds the provided attributes and wraps them into a model format
	 * @param attributeNames The names of the attributes that should be included in the 
	 * returned model
	 * @return A model containing the provided attributes. Changes made to the attributes 
	 * will affect this model as well.
	 */
	public List<VariableType> findAttributes(String... attributeNames)
	{
		List<VariableType> attributes = new ArrayList<>();
		for (String attributeName : attributeNames)
		{
			VariableType attribute = findAttribute(attributeName);
			if (attribute != null)
				attributes.add(attribute);
		}
		
		return attributes;
	}
	
	/**
	 * Adds a new attribute to the model
	 * @param attribute The attribute that will be added to the model
	 * @param replaceIfExists If there already exists an attribute with the same name, will 
	 * it be overwritten.
	 */
	public void addAttribute(VariableType attribute, boolean replaceIfExists)
	{
		// Checks if there is a previous attribute in place
		Variable previous = findCorrespondingAttribute(attribute);
		
		if (previous == null)
			this.attributes.add(attribute);
		else if (replaceIfExists)
		{
			removeAttribute(previous);
			this.attributes.add(attribute);
		}
	}
	
	/**
	 * Adds multiple attributes to the model
	 * @param attributes The attributes that are added to the model
	 * @param replaceIfExists Should possible existing attributes with identical names be 
	 * replaced
	 */
	public void addAttributes(Collection<? extends VariableType> attributes, 
			boolean replaceIfExists)
	{
		for (VariableType var : attributes)
		{
			addAttribute(var, replaceIfExists);
		}
	}
	
	/**
	 * Changes an attribute's value
	 * @param attributeName The name of the attribute
	 * @param newValue The new value given to the attribute
	 * @param generateIfNotExists If there doesn't exist an attribute with the provided name, 
	 * should one be generated
	 * @throws NoSuchAttributeException If there doesn't exist an attribute with the provided 
	 * name and one wasn't generated either
	 */
	public void setAttributeValue(String attributeName, Value newValue, 
			boolean generateIfNotExists) throws NoSuchAttributeException
	{
		Variable attribute = findAttribute(attributeName);
		if (attribute == null)
		{
			if (generateIfNotExists)
				addAttribute(generateAttribute(attributeName, newValue), false);
			else
				throw new NoSuchAttributeException(attributeName, this);
		}
		else
			attribute.setValue(newValue);
	}
	
	/**
	 * Removes an attribute from the model. If the attribute didn't exist in the model, 
	 * no change is made.
	 * @param attributeName The name of the attribute that will be removed.
	 */
	public void removeAttribute(String attributeName)
	{
		removeAttribute(findAttribute(attributeName));
	}
	
	/**
	 * Removes an attribute from the model. If the attribute didn't exist in the model, 
	 * no change is made.
	 * @param attribute The attribute that will be removed.
	 */
	public void removeAttribute(Variable attribute)
	{
		if (attribute != null)
			this.attributes.remove(attribute);
	}
	
	/**
	 * Checks if there exists an attribute with the provided name in this model
	 * @param attributeName The name of the attribute
	 * @return Does there exist and attribute with the provided name in this model
	 */
	public boolean containsAttribute(String attributeName)
	{
		return findAttribute(attributeName) != null;
	}
	
	/**
	 * Checks if the model contains the provided attribute
	 * @param attribute An attribute
	 * @return EXTRA TRUE if the model contains an attribute that equals this attribute 
	 * (same name (case-sensitive), data type and equal value). WEAK TRUE if the model the 
	 * contains the attribute (case-insensitive). WEAK FALSE if the model contains an attribute 
	 * with the same name (case-insensitive). EXTRA FALSE otherwise.
	 */
	public ExtraBoolean containsAttribute(Variable attribute)
	{
		Variable corresponding = findCorrespondingAttribute(attribute);
		if (corresponding == null)
			return ExtraBoolean.EXTRA_FALSE;
		else
		{
			if (attribute.equals(corresponding))
				return ExtraBoolean.EXTRA_TRUE;
			else if (attribute.hasEqualValueWith(corresponding))
				return ExtraBoolean.WEAK_TRUE;
			else
				return ExtraBoolean.WEAK_FALSE;
		}
	}
	
	/**
	 * @return A declaration for each of the attributes in this model
	 */
	public ModelDeclaration<VariableDeclaration> getDeclaration()
	{
		List<VariableDeclaration> declarations = new ArrayList<>();
		for (Variable attribute : getAttributes())
		{
			declarations.add(attribute.getDeclaration());
		}
		
		return new ModelDeclaration<>(declarations);
	}
	
	/**
	 * Combines the two models into a new separate model. The model still contains the same 
	 * attributes the two models had, and changes to those attributes reflect to multiple 
	 * models.
	 * @param other Another model.
	 * @return A new model that is combined from the two models
	 */
	public SimpleModel plus(Model<? extends VariableType, ? extends DeclarationType> other)
	{	
		SimpleModel model = new SimpleModel();
		
		for (VariableType attribute : getAttributes())
		{
			model.addAttribute(attribute, true);
		}
		if (other != null)
		{
			for (VariableType attribute : other.getAttributes())
			{
				model.addAttribute(attribute, true);
			}
		}
		
		return model;
	}
	
	/**
	 * Creates a new model with the provided variable added. The attributes are shared between 
	 * the two models and changes made to one will affect the other.
	 * @param variable A variable
	 * @return A model containing each of this model's attributes plus the provided variable
	 */
	public SimpleModel plus(VariableType variable)
	{
		SimpleModel model = new SimpleModel(getAttributes());
		model.addAttribute(variable, true);
		
		return model;
	}
	
	/**
	 * Creates a new model that doesn't have the provided attribute
	 * @param variable an attribute
	 * @return A copy of this model that doesn't contain the specified attribute
	 */
	public SimpleModel minus(Variable variable)
	{
		SimpleModel model = new SimpleModel(getAttributes());
		model.removeAttribute(variable);
		return model;
	}
	
	/**
	 * Creates a new model that doesn't have an attribute that has the provided declaration
	 * @param declaration a variable declaration
	 * @return A copy of this model that doesn't contain an attribute that would have 
	 * the provided declaration
	 */
	public SimpleModel minus(VariableDeclaration declaration)
	{	
		SimpleModel model = new SimpleModel(getAttributes());

		if (declaration == null)
			return model;
		
		Variable corresponding = model.findAttribute(declaration.getName());
		if (corresponding != null && corresponding.getType().equals(declaration.getType()))
			model.removeAttribute(corresponding);
		
		return model;
	}
	
	/**
	 * Creates a new model that doesn't contain any of the attributes declared in the 
	 * provided model declaration
	 * @param declaration a model declaration
	 * @return a copy of this model without any of the declared attributes
	 */
	public SimpleModel minus(ModelDeclaration<VariableDeclaration> declaration)
	{
		SimpleModel model = new SimpleModel(getAttributes());
		
		if (declaration == null)
			return model;
		
		for (VariableDeclaration varDec : declaration.getAttributeDeclarations())
		{
			Variable corresponding = model.findAttribute(varDec.getName());
			if (corresponding != null && corresponding.getType().equals(varDec.getType()))
				model.removeAttribute(corresponding);
		}
		
		return model;
	}
	
	
	// SUBCLASSES	----------------------
	
	/**
	 * These exceptions are thrown when a model attribute can't be found / used
	 * @author Mikko Hilpinen
	 * @since 14.11.2015
	 */
	public static class NoSuchAttributeException extends RuntimeException
	{
		// ATTRIBUTES	------------------
		
		private static final long serialVersionUID = -3548699660829590020L;
		private String name;
		
		
		// CONSTRUCTOR	------------------
		
		/**
		 * Creates a new exception
		 * @param attributeName The name of the missing attribute
		 */
		public NoSuchAttributeException(String attributeName)
		{
			super(parseMessage(attributeName));
			
			this.name = attributeName;
		}
		
		/**
		 * Creates a new exception
		 * @param attributeName The name of the missing attribute
		 * @param message The message sent along with the exception
		 */
		public NoSuchAttributeException(String attributeName, String message)
		{
			super(message);
			
			this.name = attributeName;
		}
		
		/**
		 * Creates a new exception
		 * @param attributeName The name of the missing attribute
		 * @param cause The exception that caused this exception
		 */
		public NoSuchAttributeException(String attributeName, Throwable cause)
		{
			super(parseMessage(attributeName), cause);
			
			this.name = attributeName;
		}
		
		/**
		 * Creates a new exception
		 * @param attributeName The name of the missing attribute
		 * @param cause The exception that caused this exception
		 * @param message The message sent along with the exception
		 */
		public NoSuchAttributeException(String attributeName, String message, Throwable cause)
		{
			super(message, cause);
			
			this.name = attributeName;
		}
		
		/**
		 * Creates a new exception
		 * @param attributeName The name of the missing attribute
		 * @param model The model the attribute was missing from
		 */
		public NoSuchAttributeException(String attributeName, Model<? extends Variable, 
				? extends VariableDeclaration> model)
		{
			super(parseMessage(attributeName, model));
			
			this.name = attributeName;
		}
		
		
		// ACCESSORS	------------------
		
		/**
		 * @return The name of the attribute that couldn't be found
		 */
		public String getAttributeName()
		{
			return this.name;
		}
		
		
		// OTHER METHODS	--------------
		
		private static String parseMessage(String attributeName)
		{
			if (attributeName == null)
				return "Attribute name not provided (null)";
			else
				return "No attribute named '" + attributeName + "'";
		}
		
		private static String parseMessage(String attributeName, Model<? extends Variable, 
				? extends VariableDeclaration> model)
		{
			StringBuilder s = new StringBuilder(parseMessage(attributeName));
			s.append("\nModel contains attributes:");
			for (Variable attribute : model.getAttributes())
			{
				s.append(" ");
				s.append(attribute.getName());
			}
			
			return s.toString();
		}
	}
}