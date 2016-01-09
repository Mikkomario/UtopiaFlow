package flow_generics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A modelDeclaration works like a class, so that it declares which variables the model will 
 * have. Model declarations, like variable declarations are immutable once created.
 * @author Mikko Hilpinen
 * @param <DeclarationType> The type of declaration stored in this declaration
 * @since 4.12.2015
 */
public class ModelDeclaration<DeclarationType extends VariableDeclaration>
{
	// ATTRIBUTES	-------------------
	
	private Set<DeclarationType> attributeDeclarations;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new model declaration
	 * @param attributeDeclarations The attributes the declaration contains
	 */
	public ModelDeclaration(Collection<? extends DeclarationType> attributeDeclarations)
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
	public Set<DeclarationType> getAttributeDeclarations()
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
	public DeclarationType findAttributeDeclaration(String attributeName)
	{
		for (DeclarationType declaration : this.attributeDeclarations)
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
	public ModelDeclaration<DeclarationType> findAttributeDeclarations(String... attributeNames)
	{
		List<DeclarationType> declarations = new ArrayList<>();
		for (String attributeName : attributeNames)
		{
			DeclarationType declaration = findAttributeDeclaration(attributeName);
			if (declaration != null)
				declarations.add(declaration);
		}
		
		return new ModelDeclaration<DeclarationType>(declarations);
	}
	
	/**
	 * Instantialises the model declaration into a model with all null values
	 * @return A model based on this declaration. All of the model's attributes will be 
	 * initialised to null
	 */
	public SimpleModel instantiate()
	{
		SimpleModel model = new SimpleModel();
		for (VariableDeclaration declaration : this.attributeDeclarations)
		{
			model.addAttribute(declaration.assignNullValue(), true);
		}
		
		return model;
	}
	
	/**
	 * Combines the two model declarations to form a larger model declaration
	 * @param other Another model declaration
	 * @return A model declaration that contains the attributes from both declarations
	 */
	public ModelDeclaration<DeclarationType> plus(ModelDeclaration<? extends DeclarationType> other)
	{
		if (other == null)
			return this;
		
		Set<DeclarationType> combinedDeclarations = new HashSet<>();
		combinedDeclarations.addAll(this.attributeDeclarations);
		combinedDeclarations.addAll(other.attributeDeclarations);
		
		return new ModelDeclaration<DeclarationType>(combinedDeclarations);
	}
	
	/**
	 * Combines this model declaration with an attribute declaration in order to form 
	 * a larger declaration
	 * @param attributeDeclaration An attribute declaration
	 * @return A model declaration that contains the provided declaration
	 */
	public ModelDeclaration<DeclarationType> plus(DeclarationType attributeDeclaration)
	{
		if (attributeDeclaration == null)
			return this;
		
		Set<DeclarationType> declarations = getAttributeDeclarations();
		declarations.add(attributeDeclaration);
		
		return new ModelDeclaration<DeclarationType>(declarations);
	}
	
	/**
	 * Creates a new model declaration based on this model declaration that doesn't share 
	 * any attributes with the other model declaration
	 * @param other Another model declaration
	 * @return a model declaration that has the attribute declarations of this model 
	 * declaration that are not shared between the two declarations.
	 */
	public ModelDeclaration<DeclarationType> minus(ModelDeclaration<? extends VariableDeclaration> other)
	{
		if (other == null)
			return this;
		
		Set<DeclarationType> declarations = getAttributeDeclarations();
		if (declarations.removeAll(other.attributeDeclarations))
			return new ModelDeclaration<DeclarationType>(declarations);
		else
			return this;
	}
	
	/**
	 * Creates a new model declaration without the provided attribute
	 * @param attributeDeclaration An attribute declaration
	 * @return A model declaration that doesn't contain the provided declaration
	 */
	public ModelDeclaration<DeclarationType> minus(VariableDeclaration attributeDeclaration)
	{
		if (attributeDeclaration == null)
			return this;
		else if (containsAttribute(attributeDeclaration))
		{
			Set<DeclarationType> declarations = getAttributeDeclarations();
			declarations.remove(attributeDeclaration);
			return new ModelDeclaration<DeclarationType>(declarations);
		}
		else
			return this;
	}
}
