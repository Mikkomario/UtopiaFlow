package utopia.java.flow.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utopia.java.flow.generics.BasicDataType;
import utopia.java.flow.generics.DataType;
import utopia.java.flow.generics.Value;
import utopia.java.flow.parse.XmlElement;

/**
 * Element is a simple data struct used for storing xml element contents
 * @author Mikko Hilpinen
 * @since 27.4.2016
 * @deprecated Replaced with {@link XmlElement}
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
		this.value = Value.NullValue(BasicDataType.OBJECT);
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
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		s.append("<");
		s.append(getName());
		
		for (String attName : getAttributeNames())
		{
			s.append(" ");
			s.append(attName);
			s.append("=");
			s.append(getAttributeValue(attName));
		}
		
		if (hasContent())
		{
			s.append(">");
			s.append(getContent().getDescription());
			s.append("</");
			s.append(getName());
			s.append(">");
		}
		else
			s.append("/>");
			
		return s.toString();
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
	
	/**
	 * Adds multiple attributes to the element
	 * @param attributes The attributes added to the element
	 */
	public void addAttributes(Map<String, String> attributes)
	{
		for (String attName : attributes.keySet())
		{
			addAttribute(attName, attributes.get(attName));
		}
	}
	
	/**
	 * @return The names of the attributes inside this element
	 */
	public Set<String> getAttributeNames()
	{
		return this.attributes.keySet();
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * @return The data type of the element's content
	 */
	public DataType getContentType()
	{
		return getContent().getType();
	}
	
	/**
	 * @return Does this element have any content in it
	 */
	public boolean hasContent()
	{
		return !getContent().isNull();
	}
}
