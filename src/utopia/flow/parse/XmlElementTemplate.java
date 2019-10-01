package utopia.flow.parse;

import utopia.flow.generics.Model;
import utopia.flow.generics.Value;
import utopia.flow.generics.Variable;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;

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
	public String getName();
	
	/**
	 * @return The text inside this element (trimmed). None if the element is empty
	 */
	public Option<String> getTextOption();
	
	/**
	 * @return the children under this element
	 */
	public ImmutableList<Element> getChildren();
	
	/**
	 * @return The attributes of this xml element
	 */
	public ImmutableMap<String, String> getAttributes();
	
	
	// OTHER	--------------------
	
	/**
	 * @return The text inside this element (trimmed). An empty string if the element is empty.
	 */
	public default String getText()
	{
		return getTextOption().getOrElse("");
	}
	
	/**
	 * @return The value of the contents of this element
	 */
	public default Value getValue()
	{
		return getTextOption().map(Value::of).getOrElse(Value.EMPTY);
	}
	
	/**
	 * @return The attributes of this xml element with values
	 */
	public default ImmutableMap<String, Value> getAttributesAsValues()
	{
		return getAttributes().mapValues(Value::of);
	}
	
	/**
	 * @return A model representing the attributes in this element
	 */
	public default Model<Variable> getAttributesModel()
	{
		return Model.fromMap(getAttributes());
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return The string value of the attribute
	 */
	public default Option<String> getAttribute(String attributeName)
	{
		return getAttributes().getOption(attributeName);
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return The value of the attribute (may be empty)
	 */
	public default Value getAttributeValue(String attributeName)
	{
		return getAttribute(attributeName).map(Value::of).getOrElse(Value.EMPTY);
	}
	
	/**
	 * Checks whether this element defines the specified attribute
	 * @param attName The name of the attribute
	 * @return Whether this element contains an attribute with the provided name
	 */
	public default boolean hasAttribute(String attName)
	{
		return getAttributes().containsKey(attName);
	}
	
	/**
	 * @return Whether this element contains attributes
	 */
	public default boolean hasAttributes()
	{
		return !getAttributes().isEmpty();
	}
	
	/**
	 * @return Whether this element has any children
	 */
	public default boolean hasChildren()
	{
		return !getChildren().isEmpty();
	}
	
	/**
	 * @return Whether this element contains any text
	 */
	public default boolean hasText()
	{
		return getTextOption().isDefined();
	}
	
	/**
	 * @return Whether this element is completely empty (no text and no children)
	 */
	public default boolean isEmpty()
	{
		return !hasText() && !hasChildren();
	}
	
	/**
	 * Finds a child under this element
	 * @param childName The name of the child (case-insensitive)
	 * @return A child with the specified name
	 */
	public default Option<Element> getExistingChild(String childName)
	{
		return getChildren().find(c -> c.getName().equalsIgnoreCase(childName));
	}
	
	/**
	 * @param name The name of the searched children (case-insensitive)
	 * @return The children with the specified name
	 */
	public default ImmutableList<Element> childrenWithName(String name)
	{
		return getChildren().filter(c -> c.getName().equalsIgnoreCase(name));
	}
	
	/**
	 * @return Converts this xml element into a model representation
	 */
	public default Model<Variable> toModel()
	{
		Model<Variable> model = Model.createBasicModel();
		model.addAttributes(toMap().toList().map(p -> new Variable(p.first(), p.second())), true);
		
		return model;
	}
	
	/**
	 * @return A map of the values of the children under this element. Children with children are converted to 
	 * model type values.
	 */
	public default ImmutableMap<String, Value> toMap()
	{
		@SuppressWarnings("unchecked")
		ImmutableMap<String, ImmutableList<Value>> attributeListMap = getChildren().toListMap(
				c -> new Pair<>(c.getName(), c.hasChildren() ? Value.Model(c.toModel()) : c.getValue()));
		ImmutableMap<String, Value> attributes = attributeListMap.mapValues(
				values -> values.size() == 1 ? values.head() : Value.of(values));
		
		return attributes;
	}
}
