package utopia.flow.generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A modelDeclaration works like a class, so that it declares which variables the model will 
 * have. Model declarations, like variable declarations are immutable once created.
 * @author Mikko Hilpinen
 * @since 4.12.2015
 */
public class ModelDeclaration
{
	// ATTRIBUTES	-------------------
	
	private Set<VariableDeclaration> attributeDeclarations;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new model declaration
	 * @param attributeDeclarations The attributes the declaration contains
	 */
	public ModelDeclaration(Collection<? extends VariableDeclaration> attributeDeclarations)
	{
		this.attributeDeclarations = new HashSet<>(attributeDeclarations);
	}
	
	
	// IMPLEMENTED METHODS	-----------
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		s.append("Model declaration");
		for (VariableDeclaration declaration : this.attributeDeclarations)
		{
			s.append("\n");
			s.append(declaration);
		}
		
		return s.toString();
	}
	
	
	// ACCESSORS	-------------------
	
	/**
	 * @return The attributes declared in this class / modelDeclaration. The returned set 
	 * is a copy and changes made to it won't affect this instance.
	 */
	public Set<VariableDeclaration> getAttributeDeclarations()
	{
		return new HashSet<>(this.attributeDeclarations);
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Checks whether this declaration contains the provided attribute (case-insensitive)
	 * @param attributeName The name of the attribute
	 * @return Does the declaration contain an attribute with the provided name
	 */
	public boolean containsAttribute(String attributeName)
	{
		return findAttributeDeclaration(attributeName) != null;
	}
	
	/**
	 * Checks whether the model declaration contains the provided attribute declaration 
	 * (case-sensitive)
	 * @param attribute An attribute declaration
	 * @return Does the model declaration contain the specified declaration
	 */
	public boolean containsAttribute(VariableDeclaration attribute)
	{
		for (VariableDeclaration declaration : this.attributeDeclarations)
		{
			if (declaration.equals(attribute))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Finds an attribute declaration for an attribute with the provided name (case-insensitive)
	 * @param attributeName The name of the attribute
	 * @return The declaration for the attribute with the provided name
	 * @throws Model.NoSuchAttributeException If this declaration doesn't contain such an 
	 * attribute
	 */
	public VariableDeclaration getAttributeDeclaration(String attributeName) throws Model.NoSuchAttributeException
	{
		VariableDeclaration declaration = findAttributeDeclaration(attributeName);
		if (declaration == null)
			throw new Model.NoSuchAttributeException(attributeName);
		return declaration;
	}
	
	/**
	 * Finds an attribute declaration with the specified name (case-insensitive), if one 
	 * exists in this declaration
	 * @param attributeName The name of the attribute
	 * @return The attribute with the provided name declared in this declaration. Null if no 
	 * such attribute could be found.
	 */
	public VariableDeclaration findAttributeDeclaration(String attributeName)
	{
		for (VariableDeclaration declaration : this.attributeDeclarations)
		{
			if (declaration.getName() == null)
			{
				if (attributeName == null)
					return declaration;
			}
			else if (attributeName != null && 
					declaration.getName().equalsIgnoreCase(attributeName))
				return declaration;
		}
		
		return null;
	}
	
	/**
	 * Finds a set of attribute declarations from this model
	 * @param attributeNames The names of the attribute that should be included in the returned 
	 * model
	 * @return A new model declaration that contains only the specified attributes. If all 
	 * of the attributes couldn't be found, they are not included
	 */
	public ModelDeclaration findAttributeDeclarations(String... attributeNames)
	{
		List<VariableDeclaration> declarations = new ArrayList<>();
		for (String attributeName : attributeNames)
		{
			VariableDeclaration declaration = findAttributeDeclaration(attributeName);
			if (declaration != null)
				declarations.add(declaration);
		}
		
		return new ModelDeclaration(declarations);
	}
	
	/**
	 * Instantialises the model declaration into a model with all default values
	 * @param parser The parser that is used for actually generating the variables / which 
	 * the model may use later
	 * @return A model based on this declaration. All of the model's attributes will be 
	 * initialised to default declaration values
	 */
	public <VariableType extends Variable> Model<VariableType> instantiate(
			VariableParser<VariableType> parser)
	{
		Model<VariableType> model = new Model<>(parser);
		for (VariableDeclaration declaration : this.attributeDeclarations)
		{
			model.addAttribute(declaration.assignDefaultValue(parser), true);
		}
		
		return model;
	}
	
	/**
	 * Instantiates the declaration into a model with all default values. Basic variables are 
	 * used and the model will receive a basic variable parser.
	 * @return A model based on this declaration
	 */
	public Model<Variable> instantiate()
	{
		return instantiate(new BasicVariableParser());
	}
	
	/**
	 * Combines the two model declarations to form a larger model declaration
	 * @param other Another model declaration
	 * @return A model declaration that contains the attributes from both declarations
	 */
	public ModelDeclaration plus(ModelDeclaration other)
	{
		if (other == null)
			return this;
		
		Set<VariableDeclaration> combinedDeclarations = new HashSet<>();
		combinedDeclarations.addAll(this.attributeDeclarations);
		combinedDeclarations.addAll(other.attributeDeclarations);
		
		return new ModelDeclaration(combinedDeclarations);
	}
	
	/**
	 * Combines this model declaration with an attribute declaration in order to form 
	 * a larger declaration
	 * @param attributeDeclaration An attribute declaration
	 * @return A model declaration that contains the provided declaration
	 */
	public ModelDeclaration plus(VariableDeclaration attributeDeclaration)
	{
		if (attributeDeclaration == null)
			return this;
		
		Set<VariableDeclaration> declarations = getAttributeDeclarations();
		declarations.add(attributeDeclaration);
		
		return new ModelDeclaration(declarations);
	}
	
	/**
	 * Creates a new model declaration based on this model declaration that doesn't share 
	 * any attributes with the other model declaration
	 * @param other Another model declaration
	 * @return a model declaration that has the attribute declarations of this model 
	 * declaration that are not shared between the two declarations.
	 */
	public ModelDeclaration minus(ModelDeclaration other)
	{
		if (other == null)
			return this;
		
		Set<VariableDeclaration> declarations = getAttributeDeclarations();
		if (declarations.removeAll(other.attributeDeclarations))
			return new ModelDeclaration(declarations);
		else
			return this;
	}
	
	/**
	 * Creates a new model declaration without the provided attribute
	 * @param attributeDeclaration An attribute declaration
	 * @return A model declaration that doesn't contain the provided declaration
	 */
	public ModelDeclaration minus(VariableDeclaration attributeDeclaration)
	{
		if (attributeDeclaration == null)
			return this;
		else if (containsAttribute(attributeDeclaration))
		{
			Set<VariableDeclaration> declarations = getAttributeDeclarations();
			declarations.remove(attributeDeclaration);
			return new ModelDeclaration(declarations);
		}
		else
			return this;
	}
}
