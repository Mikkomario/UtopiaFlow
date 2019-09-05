package utopia.flow.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import utopia.flow.generics.VariableParser.VariableGenerationFailedException;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;
import utopia.flow.util.ExtraBoolean;

/**
 * Model is a collection of variables that works much like a case-insensitive map would
 * @author Mikko Hilpinen
 * @param <VariableType> The type of variable contained within this model
 * @since 11.11.2015
 */
public class Model<VariableType extends Variable>
{
	// ATTRIBUTES	--------------------
	
	private static final BiPredicate<Variable, Variable> VARNAMES_EQUAL  = (a, b) -> a.getName().equalsIgnoreCase(b.getName());
	
	private ImmutableList<VariableType> attributes;
	private VariableParser<? extends VariableType> generator;
	
	
	// CONSTRUCTOR	--------------------
	
	/**
	 * Creates a new empty model
	 * @param variableGenerator The generator that is used for generating new attributes to the model
	 */
	public Model(VariableParser<? extends VariableType> variableGenerator)
	{
		this.generator = variableGenerator;
		this.attributes = ImmutableList.empty();
	}

	/**
	 * Creates a new model with predefined variables
	 * @param variableGenerator The generator that is used for generating new attributes to the model
	 * @param variables The variables the model will have
	 */
	public Model(VariableParser<? extends VariableType> variableGenerator, Collection<? extends VariableType> variables)
	{
		this.generator = variableGenerator;
		this.attributes = ImmutableList.of(variables);
	}
	
	/**
	 * Creates a new model with predefined variables
	 * @param variableGenerator The generator that is used for generating new attributes to the model
	 * @param variables The variables the model will have
	 */
	public Model(VariableParser<? extends VariableType> variableGenerator, ImmutableList<VariableType> variables)
	{
		this.generator = variableGenerator;
		this.attributes = variables;
	}
	
	/**
	 * Creates a new model by copying another
	 * @param other A model that will be copied
	 */
	public Model(Model<? extends VariableType> other)
	{
		this.generator = other.generator;
		this.attributes = ImmutableList.empty();
		
		if (other != null)
			addAttributes(other.getAttributes(), true);
	}
	
	/**
	 * Creates a new model that will use the basic variable parser
	 * @return The generated model
	 */
	public static Model<Variable> createBasicModel()
	{
		return new Model<>(new BasicVariableParser());
	}
	
	/**
	 * Parses a model from string map. The mode's default data type will be string.
	 * @param map a map that contains string values
	 * @return A model based on the string map.
	 * @since v3.2.0.0
	 */
	public static Model<Variable> fromMap(Map<String, String> map)
	{
		Model<Variable> model = new Model<>(new BasicVariableParser(Value.NullValue(BasicDataType.STRING)));
		for (String key : map.keySet())
		{
			model.set(key, Value.of(map.get(key)));
		}
		
		return model;
	}
	
	/**
	 * Parses a model from string map. The mode's default data type will be string.
	 * @param map a map that contains string values
	 * @return A model based on the string map
	 */
	public static Model<Variable> fromMap(ImmutableMap<String, String> map)
	{
		Model<Variable> model = new Model<>(new BasicVariableParser(Value.NullValue(BasicDataType.STRING)));
		map.forEach((name, value) -> model.set(name, Value.of(value)));
		return model;
	}
	
	
	// IMPLEMENTED METHODS	-------------
	
	@Override
	public String toString()
	{
		return ImmutableMap.of(this.attributes.map(att -> new Pair<>(att.getName(), att.getValue()))).toString();
		/*
		StringBuilder s = new StringBuilder();
		
		s.append("Model");
		for (Variable attribute : getAttributes())
		{
			s.append("\n");
			s.append(attribute);
		}
		
		return s.toString();*/
	}
	
	
	// ACCESSORS	---------------------
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.attributes == null) ? 0 : this.attributes.hashCode());
		result = prime * result + ((this.generator == null) ? 0 : this.generator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Model))
			return false;
		Model<?> other = (Model<?>) obj;
		if (this.attributes == null)
		{
			if (other.attributes != null)
				return false;
		}
		else if (!this.attributes.equals(other.attributes))
			return false;
		if (this.generator == null)
		{
			if (other.generator != null)
				return false;
		}
		else if (!this.generator.equals(other.generator))
			return false;
		return true;
	}

	/**
	 * @return The attributes stored into this model. Editing these attributes' values still affects this model
	 */
	public ImmutableList<VariableType> getAttributes()
	{
		return this.attributes;
	}
	
	/**
	 * Overwrites existing attributes with new ones
	 * @param attributes The new attributes for the model
	 */
	public void setAttributes(ImmutableList<VariableType> attributes)
	{
		this.attributes = attributes;
	}
	
	/**
	 * @return The parser used for generating new model attributes
	 */
	public VariableParser<? extends VariableType> getVariableParser()
	{
		return this.generator;
	}
	
	/**
	 * Changes the model's variable parser parser
	 * @param parser The new variable parser for the model
	 */
	protected void setVariableParser(VariableParser<? extends VariableType> parser)
	{
		this.generator = parser;
	}
	
	
	// OTHER METHODS	-----------------
	
	/**
	 * @param lowerCaseNames Should the map contain only lower case attribute names
	 * @return A map with attribute names as keys and attributes as values
	 */
	public ImmutableMap<String, VariableType> getAttributeMap(boolean lowerCaseNames)
	{
		return ImmutableMap.of(getAttributes().map(att -> new Pair<>(lowerCaseNames ? att.getName().toLowerCase() : att.getName(), att)));
	}
	
	/**
	 * @param lowerCaseNames Should the map contain only lower case attribute names
	 * @return A map with attribute names as keys and attribute values as values
	 */
	public ImmutableMap<String, Value> getValuesMap(boolean lowerCaseNames)
	{
		return ImmutableMap.of(getAttributes().map(att -> new Pair<>(lowerCaseNames ? att.getName().toLowerCase() : att.getName(), att.getValue())));
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return A value for the attribute or an empty value if no such attribute exists
	 * @since v3.3.0.0
	 */
	public Value get(String attributeName)
	{
		return find(attributeName).getOrElse(Value.EMPTY);
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return A value for the attribute or none if no such attribute exists
	 * @since v3.3.0.0
	 */
	public Option<Value> find(String attributeName)
	{
		return findAttribute(attributeName).map(att -> att.getValue());
	}
	
	/**
	 * Sets a new value for an attribute. Same as using {@link #setAttributeValue(String, Value)}
	 * @param attributeName The name of the attribute
	 * @param value The value for the attribute (not null)
	 */
	public void set(String attributeName, Value value)
	{
		setAttributeValue(attributeName, value);
	}
	
	/**
	 * Sets a new value for an attribute. NB: Doesn't set any value if setting none to a non-existing attribute
	 * @param attributeName The name of the attribute
	 * @param value The new value for the attribute. None if the value should be set to empty
	 */
	public void set(String attributeName, Option<Value> value)
	{
		Option<VariableType> attribute = findAttribute(attributeName);
		if (attribute.isDefined())
			attribute.get().setValue(value.getOrElse(() -> Value.NullValue(attribute.get().getType())));
		else if (value.isDefined())
		{
			try
			{
				generateAttribute(attributeName, value.get());
			}
			catch (VariableGenerationFailedException e)
			{
				throw new NoSuchAttributeException(attributeName, this);
			}
		}
		else
		{
			// In case an empty value is set and the attribute doesn't exist yet, tries to generate a new attribute 
			// but doesn't throw if it doesn't work
			try
			{
				generateAttribute(attributeName);
			}
			catch  (VariableGenerationFailedException e)
			{
				// Ignored
			}
		}
	}
	
	/**
	 * Finds an attribute with the provided name from this model (case-insensitive)
	 * @param attributeName The name of the attribute
	 * @return A model's attribute
	 * @throws NoSuchAttributeException If the model doesn't contain such an attribute and 
	 * one couldn't be generated either
	 */
	public VariableType getAttribute(String attributeName) throws NoSuchAttributeException
	{
		Option<VariableType> attribute = findAttribute(attributeName);
		if (attribute.isEmpty())
		{
			try
			{
				return generateAttribute(attributeName);
			}
			catch (VariableGenerationFailedException e)
			{
				throw new NoSuchAttributeException(attributeName, e);
			}
		}
		else
			return attribute.get();
	}
	
	/**
	 * Finds an attribute with the provided name from this model (case-insensitive). If no 
	 * such attribute exists, a new one will be generated from the provided value.
	 * @param attributeName The name of the attribute
	 * @param defaultValue The value given to a generated attribute if there didn't exist 
	 * any in the model already
	 * @return A model's attribute
	 * @throws VariableGenerationFailedException If the attribute couldn't be found nor generated
	 */
	public VariableType getAttribute(String attributeName, Value defaultValue) throws 
			VariableGenerationFailedException
	{
		Option<VariableType> attribute = findAttribute(attributeName);
		if (attribute.isEmpty())
			return generateAttribute(attributeName, defaultValue);
		else
			return attribute.get();
	}
	
	/**
	 * Finds the value of a single attribute in the model
	 * @param attributeName The name of the attribute
	 * @return The value assigned to the attribute
	 * @throws NoSuchAttributeException If the model didn't have an attribute with the 
	 * provided name and one couldn't be generated either
	 */
	public Value getAttributeValue(String attributeName) throws NoSuchAttributeException
	{
		return getAttribute(attributeName).getValue();
	}
	
	/**
	 * Finds an attribute with the provided name from this model (case-insensitive)
	 * @param attributeName The name of the attribute
	 * @return A model's attribute with the provided name or null if no such attribute exists.
	 */
	public Option<VariableType> findAttribute(String attributeName)
	{
		if (attributeName == null)
			return Option.none();
		
		return getAttributes().find(att -> att.getName().equalsIgnoreCase(attributeName));
	}
	
	/**
	 * Finds an attribute that matches the provided attribute (== has the same name)
	 * @param attribute An attribute
	 * @return The model's corresponding attribute or null if the model doesn't contain such 
	 * an attribute
	 */
	public Option<VariableType> findCorrespondingAttribute(Variable attribute)
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
	public ImmutableList<VariableType> findAttributes(String... attributeNames)
	{
		ImmutableList<String> attNamesList = ImmutableList.of(attributeNames);
		return getAttributes().filter(att -> attNamesList.exists(attName -> attName.equalsIgnoreCase(att.getName())));
	}
	
	/**
	 * Finds attributes based on the the attribute name
	 * @param nameFilter The filter used in the search
	 * @return The attributes included by the filter
	 * @deprecated Replaced with ImmutableList filter function
	 */
	public List<VariableType> findAttributesByName(utopia.flow.util.Filter<String> nameFilter)
	{
		List<VariableType> attributes = new ArrayList<>();
		for (VariableType attribute : getAttributes())
		{
			if (nameFilter.includes(attribute.getName()))
				attributes.add(attribute);
		}
		return attributes;
	}
	
	/**
	 * Finds attributes based on the the attribute value
	 * @param valueFilter The filter used in the search
	 * @return The attributes included by the filter
	 * @deprecated Replaced with ImmutableList filter function
	 */
	public List<VariableType> findAttributesByValue(utopia.flow.util.Filter<Value> valueFilter)
	{
		return utopia.flow.util.Filter.filterNodes(getAttributes().toMutableList(), valueFilter);
	}
	
	/**
	 * Finds attributes
	 * @param attributeFilter The filter used in the search
	 * @return The attributes included by the filter
	 * @deprecated Replaced with ImmutableList filter function
	 */
	public List<VariableType> findAttributes(utopia.flow.util.Filter<VariableType> attributeFilter)
	{
		return utopia.flow.util.Filter.filter(getAttributes().toMutableList(), attributeFilter);
	}
	
	/**
	 * @return The names of this model's attributes
	 */
	public ImmutableList<String> getAttributeNames()
	{	
		return getAttributes().map(att -> att.getName());
	}
	
	/**
	 * Adds a new attribute to the model
	 * @param attribute The attribute that will be added to the model
	 * @param replaceIfExists If there already exists an attribute with the same name, will 
	 * it be overwritten.
	 */
	public void addAttribute(VariableType attribute, boolean replaceIfExists)
	{
		if (replaceIfExists)
			this.attributes = this.attributes.overwrite(attribute, VARNAMES_EQUAL);
		else
			this.attributes = this.attributes.with(attribute, VARNAMES_EQUAL);
	}
	
	/**
	 * Adds a new attribute to the model. The attribute is generated based on the provided 
	 * value
	 * @param attributeName The name of the new attribute
	 * @param attributeValue The value assigned to the new attribute
	 * @param replaceIfExists Should an attribute be replaced if there is one with the same 
	 * name
	 */
	public void addAttribute(String attributeName, Value attributeValue, boolean replaceIfExists)
	{
		addAttribute(this.generator.generate(attributeName, attributeValue), replaceIfExists);
	}
	
	/**
	 * Adds multiple attributes to the model
	 * @param attributes The attributes that are added to the model
	 * @param replaceIfExists Should possible existing attributes with identical names be 
	 * replaced
	 */
	public void addAttributes(ImmutableList<? extends VariableType> attributes, boolean replaceIfExists)
	{
		if (replaceIfExists)
			this.attributes = this.attributes.overwriteAll(attributes, VARNAMES_EQUAL);
		else
			this.attributes = this.attributes.plusDistinct(attributes, VARNAMES_EQUAL);
	}
	
	/**
	 * Adds multiple attributes to the model
	 * @param attributes The attributes that are added to the model
	 * @param replaceIfExists Should possible existing attributes with identical names be 
	 * replaced
	 */
	public void addAttributes(Collection<? extends VariableType> attributes, boolean replaceIfExists)
	{
		addAttributes(ImmutableList.of(attributes), replaceIfExists);
	}
	
	/**
	 * Changes an attribute's value
	 * @param attributeName The name of the attribute
	 * @param newValue The new value given to the attribute
	 * @throws NoSuchAttributeException If there doesn't exist an attribute with the provided 
	 * name and one wasn't generated either
	 */
	public void setAttributeValue(String attributeName, Value newValue) throws NoSuchAttributeException
	{
		Option<VariableType> attribute = findAttribute(attributeName);
		if (attribute.isEmpty())
		{
			try
			{
				generateAttribute(attributeName, newValue);
			}
			catch (VariableGenerationFailedException e)
			{
				throw new NoSuchAttributeException(attributeName, this);
			}
		}
		else
			attribute.get().setValue(newValue);
	}
	
	/**
	 * Removes an attribute from the model. If the attribute didn't exist in the model, 
	 * no change is made.
	 * @param attributeName The name of the attribute that will be removed.
	 */
	public void removeAttribute(String attributeName)
	{
		this.attributes = this.attributes.filter(att -> !att.getName().equalsIgnoreCase(attributeName));
	}
	
	/**
	 * Removes an attribute from the model. If the attribute didn't exist in the model, 
	 * no change is made.
	 * @param attribute The attribute that will be removed.
	 */
	public void removeAttribute(Variable attribute)
	{
		if (attribute != null)
			this.attributes = this.attributes.without(attribute);
	}
	
	/**
	 * Checks if there exists an attribute with the provided name in this model
	 * @param attributeName The name of the attribute
	 * @return Does there exist and attribute with the provided name in this model
	 */
	public boolean containsAttribute(String attributeName)
	{
		return this.attributes.exists(att -> att.getName().equalsIgnoreCase(attributeName));
	}
	
	/**
	 * Checks whether the model contains an attribute declared by the declaration
	 * @param declaration A variable declaration
	 * @return EXTRA TRUE if the model contains an attribute with the same name and data type. 
	 * WEAK TRUE if the model contains an attribute with the same name but different data type. 
	 * EXTRA FALSE otherwise.
	 */
	public ExtraBoolean containsAttribute(VariableDeclaration declaration)
	{
		Option<VariableType> corresponding = findAttribute(declaration.getName());
		if (corresponding.isEmpty())
			return ExtraBoolean.EXTRA_FALSE;
		else if (corresponding.get().getType().equals(declaration.getType()))
			return ExtraBoolean.EXTRA_TRUE;
		else
			return ExtraBoolean.WEAK_TRUE;
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
		Option<VariableType> corresponding = findCorrespondingAttribute(attribute);
		if (corresponding.isEmpty())
			return ExtraBoolean.EXTRA_FALSE;
		else
		{
			if (attribute.equals(corresponding.get()))
				return ExtraBoolean.EXTRA_TRUE;
			else if (attribute.hasEqualValueWith(corresponding.get()))
				return ExtraBoolean.WEAK_TRUE;
			else
				return ExtraBoolean.WEAK_FALSE;
		}
	}
	
	/**
	 * @return A declaration for each of the attributes in this model
	 */
	public ModelDeclaration getDeclaration()
	{
		// TODO: Use immutable list in declaration
		List<VariableDeclaration> declarations = new ArrayList<>();
		for (Variable attribute : getAttributes())
		{
			declarations.add(attribute.getDeclaration());
		}
		
		return new ModelDeclaration(declarations);
	}
	
	/**
	 * Combines the two models into a new separate model. The model still contains the same 
	 * attributes the two models had, and changes to those attributes reflect to multiple 
	 * models.
	 * @param other Another model.
	 * @return A new model that is combined from the two models
	 */
	public Model<VariableType> plus(Model<? extends VariableType> other)
	{	
		Model<VariableType> model = new Model<>(this);
		
		if (other != null)
			model.addAttributes(other.getAttributes(), true);
		
		return model;
	}
	
	/**
	 * Creates a new model with the provided variable added. The attributes are shared between 
	 * the two models and changes made to one will affect the other.
	 * @param variable A variable
	 * @return A model containing each of this model's attributes plus the provided variable
	 */
	public Model<VariableType> plus(VariableType variable)
	{
		Model<VariableType> model = new Model<>(this);
		model.addAttribute(variable, true);
		
		return model;
	}
	
	/**
	 * Creates a new model that doesn't have the provided attribute
	 * @param variable an attribute
	 * @return A copy of this model that doesn't contain the specified attribute
	 */
	public Model<VariableType> minus(Variable variable)
	{
		Model<VariableType> model = new Model<>(this);
		model.removeAttribute(variable);
		return model;
	}
	
	/**
	 * Creates a new model that doesn't have an attribute that has the provided declaration
	 * @param declaration a variable declaration
	 * @return A copy of this model that doesn't contain an attribute that would have 
	 * the provided declaration
	 */
	public Model<VariableType> minus(VariableDeclaration declaration)
	{	
		Model<VariableType> model = new Model<>(this);

		if (declaration == null)
			return model;
		
		Option<VariableType> corresponding = model.findAttribute(declaration.getName());
		corresponding.filter(dec -> dec.getType().equals(declaration.getType())).forEach(model::removeAttribute);
		
		return model;
	}
	
	/**
	 * Creates a new model that doesn't contain any of the attributes declared in the 
	 * provided model declaration
	 * @param declaration a model declaration
	 * @return a copy of this model without any of the declared attributes
	 */
	public Model<VariableType> minus(ModelDeclaration declaration)
	{
		Model<VariableType> model = new Model<>(this);
		
		if (declaration == null)
			return model;
		
		for (VariableDeclaration varDec : declaration.getAttributeDeclarations())
		{
			Option<VariableType> corresponding = model.findAttribute(varDec.getName());
			corresponding.filter(dec -> dec.getType().equals(varDec.getType())).forEach(model::removeAttribute);
		}
		
		return model;
	}
	
	private VariableType generateAttribute(String attributeName) throws 
			VariableGenerationFailedException
	{
		if (this.generator == null)
			throw new VariableGenerationFailedException("Can't generate attributes without a variable parser");
		
		VariableType var = this.generator.generate(attributeName);
		addAttribute(var, true);
		return var;
	}
	
	private VariableType generateAttribute(String attributeName, Value value) throws 
			VariableGenerationFailedException
	{
		if (this.generator == null)
			throw new VariableGenerationFailedException("Can't generate attributes without a variable parser");
		else if (value == null)
			return generateAttribute(attributeName);
		else
		{
			VariableType var = this.generator.generate(attributeName, value);
			addAttribute(var, true);
			return var;
		}
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
		public NoSuchAttributeException(String attributeName, Model<? extends Variable> model)
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
		
		private static String parseMessage(String attributeName, Model<? extends Variable> model)
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
