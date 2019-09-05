package utopia.flow.parse;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import utopia.flow.generics.Model;
import utopia.flow.generics.Value;
import utopia.flow.generics.Variable;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Option;
import utopia.flow.util.StringRepresentable;

/**
 * This class represents an xml element. the class is immutable and has value semantics. The class is designed to 
 * be used for reading only.
 * @author Mikko Hilpinen
 * @since 14.3.2018
 */
public class XmlElement implements XmlElementTemplate<XmlElement>, StringRepresentable
{
	// ATTRIBUTES	--------------------
	
	/**
	 * The name of the data type attribute when it is used
	 */
	public static final String TYPE_ATTNAME = "type";
	
	private final String name;
	private final Option<String> text;
	private final ImmutableList<XmlElement> children;
	private final ImmutableMap<String, String> attributes;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new element
	 * @param name The name of the element
	 * @param text The text contents of this element
	 * @param children The child elements under this element
	 * @param attributes The attributes of this xml element
	 */
	public XmlElement(String name, Option<String> text, ImmutableList<XmlElement> children, 
			ImmutableMap<String, String> attributes)
	{
		this.name = name;
		this.text = text.map(s -> s.trim()).filter(s -> !s.isEmpty());
		this.children = children;
		this.attributes = attributes;
	}
	
	/**
	 * Creates a new element with text content
	 * @param name The name of the element
	 * @param text The text inside the element
	 */
	public XmlElement(String name, String text)
	{
		this.name = name;
		this.text = Option.some(text.trim()).filter(s -> !s.isEmpty());
		this.children = ImmutableList.empty();
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Creates a new element with text content
	 * @param name The name of the element
	 * @param text The text inside the element
	 */
	public XmlElement(String name, Option<String> text)
	{
		this.name = name;
		this.text = text.map(s -> s.trim()).filter(s -> !s.isEmpty());
		this.children = ImmutableList.empty();
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Creates a new element with text content
	 * @param name The name of the element
	 * @param value The value stored in the element
	 */
	public XmlElement(String name, Value value)
	{
		this.name = name;
		this.text = value.toStringOption().map(s -> s.trim()).filter(s -> !s.isEmpty());
		this.children = ImmutableList.empty();
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Creates a new element with child content
	 * @param name The name of the element
	 * @param children The children under the element
	 */
	public XmlElement(String name, ImmutableList<XmlElement> children)
	{
		this.name = name;
		this.text = Option.none();
		this.children = children;
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Creates a new xml element with a single child
	 * @param name The name of the element
	 * @param child The child element
	 */
	public XmlElement(String name, XmlElement child)
	{
		this.name = name;
		this.text = Option.none();
		this.children = ImmutableList.withValue(child);
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Creates a new element with child content
	 * @param name The name of the element
	 * @param firstChild The first child element
	 * @param secondChild The second child element
	 * @param moreChildren More child elements
	 */
	public XmlElement(String name, XmlElement firstChild, XmlElement secondChild, XmlElement... moreChildren)
	{
		this.name = name;
		this.text = Option.none();
		this.children = ImmutableList.withValues(firstChild, secondChild, moreChildren);
		this.attributes = ImmutableMap.empty();
	}
	
	/**
	 * Creates a multiple levels deep xml element. The last part is read as element text while earlier parts are 
	 * considered element names
	 * @param name The name of the element
	 * @param firstPart The first part (element name)
	 * @param secondPart The second part (element name or text)
	 * @param moreParts More parts (element names or text)
	 */
	public XmlElement(String name, String firstPart, String secondPart, String... moreParts)
	{
		this.name = name;
		this.text = Option.none();
		this.attributes = ImmutableMap.empty();
		
		ImmutableList<String> parts = ImmutableList.withValues(firstPart, secondPart, moreParts);
		
		if (parts.size() == 2)
			this.children = ImmutableList.withValue(new XmlElement(parts.get(0), parts.get(1)));
		else
		{
			// Adds child elements from bottom to top until all parts have been used
			// The last 2 parts represent element name + text while earlier parts represent element names
			XmlElement lastElement = new XmlElement(parts.get(parts.size() - 2), parts.last().get());
			ImmutableList<String> remainingParts = parts.dropLast(2);
			
			while (!remainingParts.isEmpty())
			{
				lastElement = new XmlElement(remainingParts.last().get(), lastElement);
				remainingParts = remainingParts.dropLast(1);
			}
			
			this.children = ImmutableList.withValue(lastElement);
		}
	}
	
	/**
	 * Creates an empty element with the specified name
	 * @param name The name of the element
	 * @return A new empty element
	 */
	public static XmlElement empty(String name)
	{
		return new XmlElement(name, Option.none(), ImmutableList.empty(), ImmutableMap.empty());
	}
	
	/**
	 * Creates a new xml element by building the child elements with a mutable buffer
	 * @param name The name of the element
	 * @param fill A function that fills the child element buffer
	 * @return The new xml element
	 */
	public static XmlElement build(String name, Consumer<? super XmlListBuilder> fill)
	{
		XmlListBuilder buffer = new XmlListBuilder();
		fill.accept(buffer);
		return new XmlElement(name, buffer.build());
	}
	
	/*
	 * Wraps an element into a jdom element wrapper
	 * @param element The actual element
	 * @param decode Whether the element text should be decoded
	 * @return The wrapped element
	 */
	/*
	public static XmlElement wrap(Element element, boolean decode)
	{
		String text = element.getTextTrim();
		String finalText;
		if (decode)
			finalText = new Try<>(() -> URLDecoder.decode(text, "UTF-8")).success().getOrElse(text);
		else
			finalText = text;
		
		return new XmlElement(element.getName(), Option.some(finalText), 
				ImmutableList.of(element.getChildren()).map(c -> XmlElement.wrap(c, decode)), 
				ImmutableMap.of(ImmutableList.of(element.getAttributes()).map(a -> new Pair<>(a.getName(), a.getValue()))));
	}*/
	
	/*
	 * Wraps an element into a jdom element wrapper
	 * @param element The actual element
	 * @return The wrapped element
	 */
	/*
	public static XmlElement wrap(Element element)
	{
		return wrap(element, false);
	}*/
	
	/**
	 * Wraps an attribute into an xml element. Only creates a single level deep element
	 * @param attribute The attribute
	 * @return An xml element based on the attribute value
	 */
	public static XmlElement wrap(Variable attribute)
	{
		return new XmlElement(attribute.getName(), attribute.getValue()).withAttributeAdded(
				TYPE_ATTNAME, attribute.getType().toString());
	}
	
	/**
	 * Wraps a model into an xml element
	 * @param model a model
	 * @param elementName The name of the produced element
	 * @return An xml element based on the model
	 */
	public static XmlElement wrap(Model<? extends Variable> model, String elementName)
	{
		return new XmlElement(elementName, model.getAttributes().map(XmlElement::wrap));
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	/*
	@Override
	public Element toJdomElement()
	{
		return toJdomElement(false);
	}*/
	
	/*
	 * Converts this xml element to a jdom element
	 * @param encodeContents Whether the element contents should be encoded
	 * @return A jdom element based on this xml element
	 */
	/*
	public Element toJdomElement(boolean encodeContents)
	{
		Element element = new Element(this.name);
		
		// Sets the element text
		Option<String> text;
		if (encodeContents)
			text = this.text.map(t -> new Try<>(() -> URLEncoder.encode(t, "UTF-8")).success().getOrElse(t));
		else
			text = this.text;
		text.forEach(element::setText);
		
		// Adds attributes
		this.attributes.forEach(element::setAttribute);
		
		// Adds children
		this.children.map(c -> c.toJdomElement(encodeContents)).forEach(element::addContent);
		
		return element;
	}*/
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("<");
		
		s.append(getName());
		this.attributes.forEach((name, value) -> 
		{
			s.append(" ");
			s.append(name);
			s.append("=\"");
			s.append(value);
			s.append("\"");
		});
		
		if (hasChildren())
		{
			s.append(">");
			this.children.forEach(c -> s.append(c.toString()));
			s.append("</");
			s.append(getName());
			s.append(">");
		}
		else if (hasText())
		{
			s.append(">");
			s.append(getText());
			s.append("</");
			s.append(getName());
			s.append(">");
		}
		else
			s.append("/>");
		
		return s.toString();
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof XmlElement))
			return false;
		XmlElement other = (XmlElement) obj;
		if (attributes == null)
		{
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (children == null)
		{
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (text == null)
		{
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
	
	// ACCESSORS	--------------------

	/**
	 * @return The name of this element
	 */
	@Override
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * @return The text inside this element (trimmed). None if the element is empty
	 */
	@Override
	public Option<String> getTextOption()
	{
		return this.text;
	}
	
	/**
	 * @return the children under this element
	 */
	@Override
	public ImmutableList<XmlElement> getChildren()
	{
		return this.children;
	}
	
	/**
	 * @return The attributes of this xml element
	 */
	@Override
	public ImmutableMap<String, String> getAttributes()
	{
		return this.attributes;
	}
	
	
	// OTHER METHODS	----------------
	
	/**
	 * Finds a child under this xml element or if one is not found, makes a temporary replicate
	 * @param childName The name of the child
	 * @return A child with the provided name or a replicate element
	 */
	public XmlElement get(String childName)
	{
		return getExistingChild(childName).getOrElse(() -> XmlElement.empty(childName));
	}
	
	/**
	 * Searches for the first element with the specified name. The search is not limited to direct children.
	 * @param childName The name of the searched child
	 * @return An element under this element with the specified name. None if no such element could be found.
	 */
	public Option<XmlElement> searchFor(String childName)
	{
		return children.flatMapFirst(e -> 
		{
			if (e.getName().equalsIgnoreCase(childName))
				return Option.some(e);
			else
				return e.searchFor(childName);
		});
	}
	
	/**
	 * @param name The name of the new element
	 * @return A copy of this element with the specified name
	 */
	public XmlElement withName(String name)
	{
		return new XmlElement(name, this.text, this.children, this.attributes);
	}
	
	/**
	 * @param text The text of the new element
	 * @return A copy of this element with the specified text
	 */
	public XmlElement withText(Option<String> text)
	{
		return new XmlElement(this.name, text, this.children, this.attributes);
	}
	
	/**
	 * @param text The text of the new element
	 * @return A copy of this element with the specified text
	 */
	public XmlElement withText(String text)
	{
		return withText(Option.some(text));
	}
	
	/**
	 * @param children The new set of children
	 * @return A copy of this element with <b>only</b> the specified children
	 */
	public XmlElement withChildren(ImmutableList<XmlElement> children)
	{
		return new XmlElement(this.name, this.text, children, this.attributes);
	}
	
	/**
	 * @param value The value of the new element
	 * @return A copy of this element with the specified value
	 */
	public XmlElement withValue(Value value)
	{
		return withText(value.toStringOption());
	}
	
	/**
	 * @param newChildren The children added to the new element
	 * @return A copy of this element with the specified children added
	 */
	public XmlElement withAddedChildren(ImmutableList<? extends XmlElement> newChildren)
	{
		return withChildren(this.children.plus(newChildren));
	}
	
	/**
	 * @param child A child added to the new element
	 * @return A copy of this element with the specified child added
	 */
	public XmlElement withAddedChild(XmlElement child)
	{
		return withChildren(children.plus(child));
	}
	
	/**
	 * @param attributes The attributes for the new element
	 * @return A copy of this element with <b>only</b> the provided attributes
	 */
	public XmlElement withAttributes(ImmutableMap<String, String> attributes)
	{
		return new XmlElement(this.name, this.text, this.children, attributes);
	}
	
	/**
	 * @param attributes The attributes added to the new element
	 * @return A copy of this element with the provided attributes added
	 */
	public XmlElement withAttributesAdded(ImmutableMap<String, String> attributes)
	{
		return withAttributes(this.attributes.plus(attributes));
	}
	
	/**
	 * @param attName The name of the attribute
	 * @param attValue The value of the attribute
	 * @return A copy of this element with the specified attribute added
	 */
	public XmlElement withAttributeAdded(String attName, String attValue)
	{
		return withAttributes(attributes.plus(attName, attValue));
	}
	
	/**
	 * @param child A child element
	 * @return This element with the added child
	 */
	public XmlElement plus(XmlElement child)
	{
		return withAddedChild(child);
	}
	
	/**
	 * @param childName The name of the child element(s)
	 * @return This element with the child(ren) with the specified name removed
	 */
	public XmlElement minus(String childName)
	{
		return withChildren(getChildren().filter(c -> !c.getName().equalsIgnoreCase(childName)));
	}
	
	/**
	 * Finds specific children from anywhere under this element and transforms them
	 * @param childName The name of the child / children that will be transformed
	 * @param map A map function for transforming the found children
	 * @return A transformed version of this element
	 */
	public XmlElement findAndMap(String childName, Function<? super XmlElement, ? extends XmlElement> map)
	{
		return findAndMap(e -> e.getName().equalsIgnoreCase(childName), map);
	}
	
	/**
	 * Finds specific children from anywhere under this element and transforms them
	 * @param find A search function for finding target children
	 * @param map A map function for transforming the found children
	 * @return A transformed version of this element
	 */
	public XmlElement findAndMap(Predicate<? super XmlElement> find, Function<? super XmlElement, ? extends XmlElement> map)
	{
		return withChildren(children.map(c -> find.test(c) ? map.apply(c) : c.findAndMap(find, map)));
	}
	
	/**
	 * Maps or generates a child element under this element
	 * @param childName The name of the target child element
	 * @param map A mapping function
	 * @param previousElementName The name of the element previous to the target element (used for placing a generated 
	 * element if necessary)
	 * @return A copy of this element with mapped or generated child element
	 */
	public XmlElement mapOrGenerateChild(String childName, Function<? super XmlElement, ? extends XmlElement> map, 
			String previousElementName)
	{
		// First searches for an existing child and tries to map it
		Option<Integer> existingIndex = children.indexWhere(c -> c.getName().equalsIgnoreCase(childName));
		if (existingIndex.isDefined())
			return withChildren(children.mapIndex(existingIndex.get(), map));
		else
		{
			XmlElement newChild = map.apply(XmlElement.empty(childName));
			
			// Either adds the new child after specified element or at the end of the list
			ImmutableList<XmlElement> newChildren = children.indexWhere(
					c -> c.getName().equalsIgnoreCase(previousElementName)).handleMap(
					lastIndex -> children.plus(newChild, lastIndex + 1), () -> children.plus(newChild));
			
			return withChildren(newChildren);
		}
	}
	
	/**
	 * @return A mutable copy of this element
	 */
	public XmlElementBuilder mutableCopy()
	{
		return XmlElementBuilder.of(this);
	}
}
