package utopia.flow.structure;

import java.util.HashMap;
import java.util.Map;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.Value;

/**
 * Element is a simple data struct used for storing xml element contents
 * @author Mikko Hilpinen
 * @since 27.4.2016
 */
public class Element implements Node<Value>
{
	// ATTRIBUTES	----------------
	
	private String name;
	private Value value;
	private Map<String, String> attributes = new HashMap<>();
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new empty element
	 * @param name The name of the element
	 */
	public Element(String name)
	{
		this.name = name;
		this.value = Value.NullValue(BasicDataType.STRING);
	}

	/**
	 * Creates a new element
	 * @param name The name of the element
	 * @param content The content stored inside the element
	 */
	public Element(String name, Value content)
	{
		this.name = name;
		this.value = content;
	}
	
	
	// IMPLEMENTED METHODS	--------
	
	@Override
	public Value getContent()
	{
		return this.value;
	}
	
	
	// ACCESSORS	----------------
	
	/**
	 * @return The name of the element
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Changes the name of the element
	 * @param name The name of the element
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return The text stored inside the element
	 */
	public String getTextContent()
	{
		return getContent().toString();
	}
	
	/**
	 * Changes the content inside the element
	 * @param content The content stored inside the element
	 */
	public void setContent(Value content)
	{
		this.value = content;
	}
	
	/**
	 * Changes the text content of the element
	 * @param text The element contents
	 */
	public void setTextContent(String text)
	{
		setContent(Value.String(text));
	}
	
	/**
	 * Finds the value of an attribute in the element
	 * @param attributeName The name of the attribute
	 * @return The value of the attribute. Null if there is no such attribute
	 */
	public String getAttributeValue(String attributeName)
	{
		return this.attributes.get(attributeName.toLowerCase());
	}
	
	/**
	 * Adds a new attribute to the element
	 * @param attributeName The name of the attribute
	 * @param value The value of the attribute
	 */
	public void addAttribute(String attributeName, String value)
	{
		this.attributes.put(attributeName.toLowerCase(), value);
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return Does this element have any content in it
	 */
	public boolean hasContent()
	{
		return !getContent().isNull();
	}
}
