package utopia.flow.parse;

import utopia.flow.generics.Model;
import utopia.flow.generics.Value;
import utopia.flow.generics.Variable;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Option;
import utopia.flow.structure.Pair;

/**
 * This class represents an xml element. the class is immutable and has value semantics. The class is designed to 
 * be used for reading only.
 * @author Mikko Hilpinen
 * @since 14.3.2018
 */
public class XmlElement
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
		this.text = text.filter(s -> !s.isEmpty());
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
		this.text = Option.some(text).filter(s -> !s.isEmpty());
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
		this.text = text.filter(s -> !s.isEmpty());
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
		this.text = value.toStringOption().filter(s -> !s.isEmpty());
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
	 * Creates an empty element with the specified name
	 * @param name The name of the element
	 * @return A new empty element
	 */
	public static XmlElement empty(String name)
	{
		return new XmlElement(name, Option.none(), ImmutableList.empty(), ImmutableMap.empty());
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
	
	
	// ACCESSORS	--------------------

	/**
	 * @return The name of this element
	 */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * @return The text inside this element (trimmed). An empty string if the element is empty.
	 */
	public String getText()
	{
		return this.text.getOrElse("");
	}
	
	/**
	 * @return The text inside this element (trimmed). None if the element is empty
	 */
	public Option<String> getTextOption()
	{
		return this.text;
	}
	
	/**
	 * @return the children under this element
	 */
	public ImmutableList<XmlElement> getChildren()
	{
		return this.children;
	}
	
	/**
	 * @return The value of the contents of this element
	 */
	public Value getValue()
	{
		return this.text.map(Value::of).getOrElse(Value.EMPTY);
	}
	
	/**
	 * @return The attributes of this xml element
	 */
	public ImmutableMap<String, String> getAttributes()
	{
		return this.attributes;
	}
	
	/**
	 * @return The attributes of this xml element with values
	 */
	public ImmutableMap<String, Value> getAttributesAsValues()
	{
		return this.attributes.mapValues(Value::of);
	}
	
	/**
	 * @return A model representing the attributes in this element
	 */
	public Model<Variable> getAttributesModel()
	{
		return Model.fromMap(getAttributes());
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return The string value of the attribute
	 */
	public Option<String> getAttribute(String attributeName)
	{
		return this.attributes.getOption(attributeName);
	}
	
	/**
	 * @param attributeName The name of the attribute
	 * @return The value of the attribute (may be empty)
	 */
	public Value getAttributeValue(String attributeName)
	{
		return getAttribute(attributeName).map(Value::of).getOrElse(Value.EMPTY);
	}
	
	/**
	 * Checks whether this element defines the specified attribute
	 * @param attName The name of the attribute
	 * @return Whether this element contains an attribute with the provided name
	 */
	public boolean hasAttribute(String attName)
	{
		return this.attributes.containsKey(attName);
	}
	
	
	// OTHER METHODS	----------------
	
	/**
	 * @return Whether this element is completely empty (no text and no children)
	 */
	public boolean isEmpty()
	{
		return this.text.isEmpty() && !hasChildren();
	}
	
	/**
	 * @return Whether this element contains attributes
	 */
	public boolean hasAttributes()
	{
		return !this.attributes.isEmpty();
	}
	
	/**
	 * @return Whether this element has any children
	 */
	public boolean hasChildren()
	{
		return !this.children.isEmpty();
	}
	
	/**
	 * @return Whether this element contains any text
	 */
	public boolean hasText()
	{
		return this.text.isDefined();
	}
	
	/**
	 * @return A map of the values of the children under this element. Children with children are converted to 
	 * model type values.
	 */
	public ImmutableMap<String, Value> toMap()
	{
		ImmutableMap<String, ImmutableList<Value>> attributeListMap = this.children.toListMap(
				c -> new Pair<>(c.getName(), c.hasChildren() ? Value.Model(c.toModel()) : c.getValue()));
		ImmutableMap<String, Value> attributes = attributeListMap.mapValues(
				values -> values.size() == 1 ? values.head() : Value.of(values));
		
		return attributes;
	}
	
	/**
	 * @return Converts this xml element into a model representation
	 */
	public Model<Variable> toModel()
	{
		Model<Variable> model = Model.createBasicModel();
		model.addAttributes(toMap().toList().map(p -> new Variable(p.getFirst(), p.getSecond())), true);
		
		return model;
	}
	
	/**
	 * Finds a child under this element
	 * @param childName The name of the child (case-insensitive)
	 * @return A child with the specified name
	 */
	public Option<XmlElement> getExistingChild(String childName)
	{
		return this.children.find(c -> c.getName().equalsIgnoreCase(childName));
	}
	
	/**
	 * @param name The name of the searched children (case-insensitive)
	 * @return The children with the specified name
	 */
	public ImmutableList<XmlElement> childrenWithName(String name)
	{
		return this.children.filter(c -> c.getName().equalsIgnoreCase(name));
	}
	
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
		return withChildren(this.children.plus(child));
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
		return withAttributes(this.attributes.plus(attName, attValue));
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
}
