package utopia.flow.parse;

import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;

/**
 * This is a mutable version of the xml element class. Used for mutating xml element data
 * @author Mikko Hilpinen
 * @since 29.10.2018
 */
public class XmlElementBuilder implements XmlElementTemplate<XmlElementBuilder>
{
	// ATTRIBUTES	-------------------
	
	private String name;
	private Option<String> text;
	private ImmutableList<XmlElementBuilder> children;
	private ImmutableMap<String, String> attributes;
	
	
	// CONSTRUCTOR	------------------
	
	private XmlElementBuilder(String name, Option<String> text, ImmutableList<XmlElementBuilder> children, 
			ImmutableMap<String, String> attributes)
	{
		this.name = name;
		this.text = text.flatMap(s -> 
		{
			String trimmed = s.trim();
			return Option.takeIf(trimmed, !trimmed.isEmpty());
		});
		this.children = children;
		this.attributes = attributes;
	}
	
	/**
	 * Creates a new element builder based on the provided element
	 * @param element An element to be replicated
	 * @return A mutable copy of the specified element
	 */
	public static XmlElementBuilder of(XmlElement element)
	{
		return new XmlElementBuilder(element.getName(), element.getTextOption(), 
				element.getChildren().map(XmlElementBuilder::of), element.getAttributes());
	}
	
	/**
	 * Creates an empty element
	 * @param name The name of the element
	 * @return A new empty element with specified name
	 */
	public static XmlElementBuilder empty(String name)
	{
		return new XmlElementBuilder(name, Option.none(), ImmutableList.empty(), ImmutableMap.empty());
	}

	
	// ACCESSORS	------------------
	
	@Override
	public String getName()
	{
		return name;
	}
	
	/**
	 * Changes the name of this element
	 * @param name The new name
	 */
	public void setName(String name)
	{
		this.name = name.trim();
	}
	
	@Override
	public Option<String> getTextOption()
	{
		return text;
	}
	
	/**
	 * Updates element text
	 * @param text The new text in this element
	 */
	public void setText(String text)
	{
		String trimmed = text.trim();
		this.text = Option.takeIf(trimmed, !trimmed.isEmpty());
	}
	
	@Override
	public ImmutableList<XmlElementBuilder> getChildren()
	{
		return children;
	}
	
	@Override
	public ImmutableMap<String, String> getAttributes()
	{
		return attributes;
	}
	
	/**
	 * Updates element attributes
	 * @param attributes The new attributes for this element
	 */
	public void setAttributes(ImmutableMap<String, String> attributes)
	{
		this.attributes = attributes;
	}
	
	
	// OTHER	------------------
	
	/**
	 * @return An immutable version of the current state of this element
	 */
	public XmlElement build()
	{
		return new XmlElement(name, text, children.map(c -> c.build()), attributes);
	}
	
	/**
	 * Removes all children from this element
	 */
	public void clearChildren()
	{
		this.children = ImmutableList.empty();
	}
	
	/**
	 * Removes all text content from this element
	 */
	public void clearText()
	{
		this.text = Option.none();
	}
	
	/**
	 * Clears all attribute data from this element
	 */
	public void clearAttributes()
	{
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Removes all content from this element
	 */
	public void clear()
	{
		clearChildren();
		clearText();
		clearAttributes();
	}
	
	/**
	 * Adds more text to this element
	 * @param newText The new text to be added
	 */
	public void appendText(String newText)
	{
		setText(getText() + newText);
	}
	
	/**
	 * Adds a new child to this element
	 * @param childName The name of the new child
	 * @return The newly created child
	 */
	public XmlElementBuilder makeChild(String childName)
	{
		return makeChild(childName, Option.none());
	}
	
	/**
	 * Adds a new child to this element
	 * @param childName The name of the new child
	 * @param index The index of the new child (optional)
	 * @return The newly created child
	 */
	public XmlElementBuilder makeChild(String childName, int index)
	{
		return makeChild(childName, Option.some(index));
	}
	
	/**
	 * Adds a new child to this element
	 * @param childName The name of the new child
	 * @param index The index of the new child (optional)
	 * @return The newly created child
	 */
	public XmlElementBuilder makeChild(String childName, Option<? extends Integer> index)
	{
		return addChild(XmlElementBuilder.empty(childName), index);
	}
	
	/**
	 * Adds a new child to this element
	 * @param element An element that will be converted to a child
	 * @return The newly created child
	 */
	public XmlElementBuilder addChild(XmlElement element)
	{
		return addChild(element, Option.none());
	}
	
	/**
	 * Adds a new child to this element
	 * @param element An element that will be converted to a child
	 * @param index The index of the new child (optional)
	 * @return The newly created child
	 */
	public XmlElementBuilder addChild(XmlElement element, int index)
	{
		return addChild(element, Option.some(index));
	}
	
	/**
	 * Adds a new child to this element
	 * @param element An element that will be converted to a child
	 * @param index The index of the new child (optional)
	 * @return The newly created child
	 */
	public XmlElementBuilder addChild(XmlElement element, Option<? extends Integer> index)
	{
		return addChild(XmlElementBuilder.of(element), index);
	}
	
	/**
	 * Retrieves or generates a child element
	 * @param childName The name of the element
	 * @return Existing or generated child
	 */
	public XmlElementBuilder getOrMakeChild(String childName)
	{
		return getOrMakeChild(childName, Option.none());
	}
	
	/**
	 * Retrieves or generates a child element
	 * @param childName The name of the element
	 * @param previousElementName The name of the previous element (used for specifying generation location) (optional)
	 * @return Existing or generated child
	 */
	public XmlElementBuilder getOrMakeChild(String childName, String previousElementName)
	{
		return getOrMakeChild(childName, Option.some(previousElementName));
	}
	
	/**
	 * Retrieves or generates a child element
	 * @param childName The name of the element
	 * @param previousElementName The name of the previous element (used for specifying generation location) (optional)
	 * @return Existing or generated child
	 */
	public XmlElementBuilder getOrMakeChild(String childName, Option<String> previousElementName)
	{
		return getExistingChild(childName).getOrElse(() -> makeChild(childName, previousElementName.flatMap(
				s -> getChildren().indexWhere(c -> c.getName().equalsIgnoreCase(s)))));
	}
	
	private XmlElementBuilder addChild(XmlElementBuilder child, Option<? extends Integer> index)
	{
		index.handle(i -> children = children.plus(child, i), () -> children = children.plus(child));
		return child;
	}
	
	/**
	 * Adds a new attribute to this element (or replaces an existing attribute)
	 * @param attName The attribute name
	 * @param attValue The attribute value
	 */
	public void addAttribute(String attName, String attValue)
	{
		setAttributes(getAttributes().plus(attName, attValue));
	}
	
	/**
	 * removes a specific attribute from this element
	 * @param attName The name of the attribute to remove
	 */
	public void removeAttribute(String attName)
	{
		setAttributes(getAttributes().minus(attName));
	}
	
	/**
	 * Adds multiple attributes to this element
	 * @param atts The attributes to add
	 */
	public void addAttributes(Iterable<? extends Pair<String, String>> atts)
	{
		setAttributes(getAttributes().plus(atts));
	}
	
	/**
	 * Recursively searches for an element with specified name
	 * @param childName The name of the child
	 * @return A child with specified name or none
	 */
	public Option<XmlElementBuilder> searchFor(String childName)
	{
		return getChildren().flatMapFirst(c -> 
		{
			if (c.getName().equalsIgnoreCase(childName))
				return Option.some(c);
			else
				return c.searchFor(childName);
		});
	}
}
