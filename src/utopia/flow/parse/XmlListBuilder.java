package utopia.flow.parse;

import java.util.function.Consumer;

import utopia.flow.generics.Value;
import utopia.flow.structure.ListBuilder;
import utopia.flow.structure.Option;

/**
 * Xml builder is used for building a list of xml elements
 * @author Mikko Hilpinen
 * @since 6.8.2018
 */
public class XmlListBuilder extends ListBuilder<XmlElement>
{
	/**
	 * Adds a new element to this buffer
	 * @param elementName The name of the element
	 * @param text The text content in the element
	 */
	public void add(String elementName, Option<String> text)
	{
		add(new XmlElement(elementName, text));
	}
	
	/**
	 * Adds a new element to this buffer
	 * @param elementName The name of the element
	 * @param text The text content in the element
	 */
	public void add(String elementName, String text)
	{
		add(elementName, Option.some(text));
	}
	
	/**
	 * Adds a new element to this buffer
	 * @param elementName The name of the element
	 * @param value The value of the element
	 */
	public void add(String elementName, Value value)
	{
		add(new XmlElement(elementName, value));
	}
	
	/**
	 * Adds a new element to this buffer
	 * @param elementName The name of the element
	 * @param childName The name of the first child
	 * @param part1 The first part (either element name or text)
	 * @param moreParts The remaining parts (element names, ending with text)
	 */
	public void add(String elementName, String childName, String part1, String... moreParts)
	{
		add(new XmlElement(elementName, childName, part1, moreParts));
	}
	
	/**
	 * Adds a new element to this buffer
	 * @param elementName The name of the element
	 * @param fill A function that fills the element's children using a {@link XmlListBuilder}
	 */
	public void add(String elementName, Consumer<? super XmlListBuilder> fill)
	{
		add(XmlElement.build(elementName, fill));
	}
}
