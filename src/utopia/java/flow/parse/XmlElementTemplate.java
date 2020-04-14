package utopia.java.flow.parse;

import utopia.java.flow.generics.Model;
import utopia.java.flow.generics.Value;
import utopia.java.flow.generics.Variable;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.ImmutableMap;
import utopia.java.flow.structure.Option;
import utopia.java.flow.structure.Pair;

/**
 * This is a common interface for all xml element implementations (immutable & mutable)
 * @author Mikko Hilpinen
 * @param <Element> 
 * @since 30.10.2018
 */
public interface XmlElementTemplate<Element extends XmlElementTemplate<?>>
{
	// ABSTRACT	---------------------
	
	/**
	 * @return The name of this element
	 */
	String getName();
	
	/**
	 * @return The text inside this element (trimmed). None if the element is empty
	 */
	Option<String> getTextOption();
	
	/**
	 * @return the children under this element
	 */
	ImmutableList<Element> getChildren();
	
	/**
	 * @return The attributes of this xml element
	 */
	ImmutableMap<String, String> getAttributes();
	
	
	// OTHER	--------------------
	
	/**
	 * @return The text inside this element (trimmed). An empty string if the element is empty.
	 */
	default String getText()
	{
		return getTextOption().getOrElse("");
	}
	
	/**
	 * @return The value of the contents of this element
	 */
	default Value getValue()
	{
		return getTextOption().map(Value::of).getOrElse(Value.EMPTY);
	}
	
	/**
	 * @return The attributes of this xml element with values
	 */
	default ImmutableMap<String, Value> getAttributesAsValues()
	{
		return getAttributes().mapValues(Value::of);
	}
	
	/**
	 * @return A model representing the attributes in this element
	 */
	default Model<Variable> getAttributesModel()
	{
		return Model.fromMap(getAttributes());
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return The string value of the attribute
	 */
	default Option<String> getAttribute(String attributeName)
	{
		return getAttributes().getOption(attributeName);
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return The value of the attribute (may be empty)
	 */
	default Value getAttributeValue(String attributeName)
	{
		return getAttribute(attributeName).map(Value::of).getOrElse(Value.EMPTY);
	}
	
	/**
	 * Checks whether this element defines the specified attribute
	 * @param attName The name of the attribute
	 * @return Whether this element contains an attribute with the provided name
	 */
	default boolean hasAttribute(String attName)
	{
		return getAttributes().containsKey(attName);
	}
	
	/**
	 * @return Whether this element contains attributes
	 */
	default boolean hasAttributes()
	{
		return !getAttributes().isEmpty();
	}
	
	/**
	 * @return Whether this element has any children
	 */
	default boolean hasChildren()
	{
		return !getChildren().isEmpty();
	}
	
	/**
	 * @return Whether this element contains any text
	 */
	default boolean hasText()
	{
		return getTextOption().isDefined();
	}
	
	/**
	 * @return Whether this element is completely empty (no text and no children)
	 */
	default boolean isEmpty()
	{
		return !hasText() && !hasChildren();
	}
	
	/**
	 * Finds a child under this element
	 * @param childName The name of the child (case-insensitive)
	 * @return A child with the specified name
	 */
	default Option<Element> getExistingChild(String childName)
	{
		return getChildren().find(c -> c.getName().equalsIgnoreCase(childName));
	}
	
	/**
	 * @param name The name of the searched children (case-insensitive)
	 * @return The children with the specified name
	 */
	default ImmutableList<Element> childrenWithName(String name)
	{
		return getChildren().filter(c -> c.getName().equalsIgnoreCase(name));
	}
	
	/**
	 * @return Converts this xml element into a model representation
	 */
	default Model<Variable> toModel()
	{
		Model<Variable> model = Model.createBasicModel();
		model.addAttributes(toMap().toList().map(p -> new Variable(p.first(), p.second())), true);
		
		return model;
	}
	
	/**
	 * @return A map of the values of the children under this element. Children with children are converted to 
	 * model type values.
	 */
	default ImmutableMap<String, Value> toMap()
	{
		ImmutableMap<String, ImmutableList<Value>> attributeListMap = getChildren().toListMap(
				c -> new Pair<>(c.getName(), c.hasChildren() ? Value.Model(c.toModel()) : c.getValue()));

		return attributeListMap.mapValues(
				values -> values.size() == 1 ? values.head() : Value.of(values));
	}
}
