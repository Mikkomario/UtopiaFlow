package utopia.flow.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.DataType;
import utopia.flow.generics.DataTypeException;
import utopia.flow.generics.DataTypes;
import utopia.flow.generics.Value;
import utopia.flow.io.ElementValueParser.ElementValueParsingFailedException;
import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;
import utopia.flow.util.Filter;

/**
 * Xml element reader works like a hybrid of sax and dom readers, allowing the user to 
 * read the contents one by one or multiple elements at once.
 * @author Mikko Hilpinen
 * @since 29.4.2016
 */
public class XmlElementReader
{
	// TODO: Create a common interface that can be used in json reading as well
	
	// ATTRIBUTES	-----------------
	
	private XMLStreamReader reader;
	private boolean decodeValues;
	private int depth = 0, lastDepth = 0;
	
	private String currentElementName = null;
	private Map<String, String> currentElementAttributes = null;
	private DataType currentElementType = null;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new element reader
	 * @param stream The xml stream the reader reads
	 * @param decodeElementContents Should the element contents be decoded
	 * @throws XMLStreamException If the reader couldn't be opened
	 */
	public XmlElementReader(InputStream stream, boolean decodeElementContents) throws XMLStreamException
	{
		this.reader = XMLIOAccessor.createReader(stream);
		this.decodeValues = decodeElementContents;
		
		// Moves the reader to the start of the first element
		while (hasNext())
		{
			this.reader.next();
			if (this.reader.isStartElement())
				break;
		}
	}

	
	// OTHER METHODS	------------
	
	/**
	 * Closes the reader, leaving the stream open
	 * @throws XMLStreamException If the closing failed
	 */
	public void close() throws XMLStreamException
	{
		this.reader.close();
	}
	
	/**
	 * Closes the reader, leaving the stream open. Any exceptions are catched and ignored.
	 */
	public void closeQuietly()
	{
		try
		{
			close();
		}
		catch (XMLStreamException e)
		{
			// Ignored
		}
	}
	
	/**
	 * @return The name of the current element
	 */
	public String getCurrentElementName()
	{
		if (this.currentElementName == null)
			this.currentElementName = this.reader.getLocalName();
		
		return this.currentElementName;
	}
	
	/**
	 * @return The attribute map read from the current element
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If the end of the stream was reached and no element 
	 * data could be read
	 */
	public Map<String, String> getCurrentElementAttributes() throws 
			EndOfStreamReachedException, XMLStreamException
	{
		if (this.currentElementAttributes == null)
			readElementAttributes();
		
		return this.currentElementAttributes;
	}
	
	// TODO: These two methods can't be used with json, create some other way of determining 
	// element data type
	// For example: data types are specified in a separate stream 
	/*
	 * Name1:
	 * {
	 * 		attName1: STRING;
	 * 		attName2: INT;
	 * 		attName3:
	 * 		{
	 * 			...
	 * 		}
	 * }
	 * 
	 * but as you can see, hierarchical data type recording gets difficult
	 * -> Json reading will not be implemented
	 */
	
	/**
	 * @return The data type of the currently open element's content
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If the end of the stream was reached and no element 
	 * data could be read
	 */
	public DataType getCurrentElementContentType() throws EndOfStreamReachedException, XMLStreamException
	{
		if (this.currentElementType == null)
			readElementAttributes();
		
		return this.currentElementType;
	}
	
	/**
	 * @return Whether the reader has any remaining data to read
	 * @throws XMLStreamException If the operation failed
	 */
	public boolean hasNext() throws XMLStreamException
	{
		return this.reader.hasNext();
	}
	
	/**
	 * @return The current depth of the reader's cursor. 0 would be at the root element / end 
	 * of document.
	 */
	public int getCurrentDepth()
	{
		return this.depth;
	}
	
	/**
	 * @return How much the reader's depth was increased by the last operation. The number 
	 * is negative when traversing upwards.
	 */
	public int getLastDepthChange()
	{
		return this.depth - this.lastDepth;
	}
	
	/**
	 * Moves the reader to the start of the next element, which may be one of the current 
	 * element's children. Doesn't parse any content in between.
	 * @return Whether the next element was found
	 * @throws XMLStreamException If the reading failed
	 */
	public boolean skipToNextElement() throws XMLStreamException
	{
		try
		{
			toNextElementStart(false, false);
		}
		catch (EndOfStreamReachedException | ElementParseException e)
		{
			// The exception doesn't occur when skipping
		}
		return hasNext();
	}
	
	/**
	 * Moves the reader to the start of the next element, which may be one of the current 
	 * element's children. The current element data is parsed.
	 * @return The current element, unless the end of the stream was reached
	 * @throws XMLStreamException If the reading failed
	 * @throws EndOfStreamReachedException If the end of the stream was reached and no element 
	 * could be parsed
	 * @throws ElementParseException If element value parsing failed
	 */
	public Element toNextElement() throws XMLStreamException, EndOfStreamReachedException, ElementParseException
	{
		return toNextElementStart(true, false);
	}
	
	/**
	 * Moves the element to the next sibling of this element. If there are no more siblings 
	 * left for this element, moves to the parent element
	 * @return Was there any more siblings for this element. If true, the cursor is now at 
	 * the next sibling. if false, the cursor is now at the parent element or at the 
	 * end of the stream.
	 * @throws XMLStreamException If read failed
	 */
	public boolean skipToNextSibling() throws XMLStreamException
	{
		try
		{
			toNextSiblingOrHigher(false, 0);
		}
		catch (EndOfStreamReachedException | ElementParseException e)
		{
			// Only thrown on read
		}
		return getLastDepthChange() == 0;
	}
	
	/**
	 * Reads this element and moves to the next sibling / parent element, whichever comes first. 
	 * You can use {@link #getLastDepthChange()} to check whether the new element is actually 
	 * a sibling element
	 * @return The element that was read
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If the cursor was at the end of the stream and no element 
	 * data could be read
	 * @throws ElementParseException If element value parsing failed
	 */
	public Element toNextSibling() throws XMLStreamException, EndOfStreamReachedException, ElementParseException
	{
		return toNextSiblingOrHigher(true, 0);
	}
	
	/**
	 * Moves the cursor to the next higher level element
	 * @return Was the next element found
	 * @throws XMLStreamException If read failed
	 */
	public boolean skipTocloseParent() throws XMLStreamException
	{
		try
		{
			toNextSiblingOrHigher(false, 1);
		}
		catch (EndOfStreamReachedException | ElementParseException e)
		{
			// Ignored on skip
		}
		return hasNext();
	}
	
	/**
	 * Reads the current element and moves the cursor to the next higher level element
	 * @return The current element
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If end of the stream was reached and element data 
	 * couldn't be found
	 * @throws ElementParseException If element value couldn't be parsed
	 */
	public Element toCloseParent() throws XMLStreamException, EndOfStreamReachedException, ElementParseException
	{
		return toNextSiblingOrHigher(true, 1);
	}
	
	/**
	 * Skips through elements until reaching an element with the provided name
	 * @param skipChildren whether the child elements of the current element should be skipped
	 * @param nameFilter The filter that decides which name is acceptable
	 * @return Was the element found from the stream
	 * @throws XMLStreamException If the reading failed
	 */
	public boolean skipToNextElementWithName(boolean skipChildren, 
			Filter<String> nameFilter) throws XMLStreamException
	{
		int startDepth = getCurrentDepth();
		try
		{
			if (skipChildren)
			{
				skipToNextSibling();
				if (!hasNext())
					return false;
			}
			else
			{
				if (!skipToNextElement())
					return false;
			}
			
			while (hasNext() && !nameFilter.includes(getCurrentElementName()))
			{
				if (!skipToNextElement())
					return false;
			}
			
			return true;
		}
		finally
		{
			this.lastDepth = startDepth;
		}
	}
	

	/**
	 * Reads the current element, then skips until reaching an element with the provided name
	 * @param skipChildren Should the children of the current element be skipped
	 * @param nameFilter The filter that decides which name is acceptable
	 * @return The current element, unless the end of the stream was reached
	 * @throws XMLStreamException If reading failed
	 * @throws EndOfStreamReachedException If the cursor was at the end of the stream
	 * @throws ElementParseException If element value parsing failed
	 */
	public Element toNextElementWithName(boolean skipChildren, Filter<String> nameFilter) throws 
			XMLStreamException, EndOfStreamReachedException, ElementParseException
	{
		Element element;
		if (skipChildren)
			element = toNextSibling();
		else
			element = toNextElement();
		
		while (hasNext() && !nameFilter.includes(getCurrentElementName()))
		{
			skipToNextElement();
		}
		return element;
	}
	
	/**
	 * Parses the currently selected element into a tree that contains the elements data plus 
	 * the data of each element below that. The cursor is moved to the next sibling / parent 
	 * element.
	 * @return The parsed element tree
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If the end of the stream was reached and no element 
	 * data could be found
	 * @throws ElementParseException If element value parsing failed
	 */
	public TreeNode<Element> parseCurrentElement() throws XMLStreamException, 
			EndOfStreamReachedException, ElementParseException
	{
		int startDepth = getCurrentDepth();
		
		TreeNode<Element> element = new TreeNode<>(toNextElement());
		while (getCurrentDepth() > startDepth)
		{
			element.addChild(parseCurrentElement());
		}
		
		this.lastDepth = startDepth;
		return element;
	}
	
	/**
	 * Reads the whole stream and parses its contents into a tree
	 * @param stream The stream that is read
	 * @param decodeElementContents Should the element contents be decoded from UTF-8
	 * @return The element structure of the stream
	 * @throws XMLStreamException If read failed
	 * @throws ElementParseException If element value parsing failed
	 */
	public static TreeNode<Element> parseStream(InputStream stream, 
			boolean decodeElementContents) throws XMLStreamException, ElementParseException
	{
		XmlElementReader reader = new XmlElementReader(stream, decodeElementContents);
		try
		{
			return reader.parseCurrentElement();
		}
		catch (EndOfStreamReachedException e)
		{
			return null;
		}
		finally
		{
			reader.close();
		}
	}
	
	/**
	 * Parses the contents of a file into a hierarchical element
	 * @param file The file that is read
	 * @param decodeElementContents Should the element contents be decoded from UTF-8
	 * @return The element parsed from the file
	 * @throws IOException If the file couldn't be opened / created / closed
	 * @throws XMLStreamException If read failed
	 * @throws ElementParseException If element value parsing failed
	 */
	public static TreeNode<Element> parseFile(File file, boolean decodeElementContents) throws 
			IOException, XMLStreamException, ElementParseException
	{
		InputStream stream = new FileInputStream(file);
		try
		{
			return parseStream(stream, decodeElementContents);
		}
		finally
		{
			stream.close();
		}
	}
	
	/**
	 * Moves to the start of the next element
	 * @param readElement Should the current element be read
	 * @return The element that was read. Null if reading was skipped
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If end of stream was reached and no element data 
	 * could be read (on read only)
	 * @throws ElementParseException If element value parsing failed (on read only)
	 */
	private Element toNextElementStart(boolean readElement, boolean skippingLowerElements) 
			throws XMLStreamException, EndOfStreamReachedException, ElementParseException
	{
		try
		{
			// Goes inside the element first -> depth increases
			this.lastDepth = getCurrentDepth();
			this.depth ++;
			
			DataType type = null;
			
			// May check the element data type
			if (readElement || !skippingLowerElements)
			{
				// Can't read element data if at the end of stream
				if (!hasNext())
					throw new EndOfStreamReachedException();
				
				// Reads the current element data type and uses that to determine whether 
				// the next child element needs to be parsed into this element as value or 
				// skipped altogether
				type = getCurrentElementContentType();
			}
			
			Element element = null;
			// Initialises the element data if need be
			if (readElement)
			{
				element = new Element(getCurrentElementName(), Value.NullValue(type));
				element.addAttributes(getCurrentElementAttributes());
			}
			
			// The next element will be reached, current element data is no longer valid
			this.currentElementName = null;
			this.currentElementType = null;
			if (this.currentElementAttributes != null)
			{
				this.currentElementAttributes.clear();
				this.currentElementAttributes = null;
			}
			
			while (hasNext())
			{
				this.reader.next();
				if (this.reader.isStartElement())
					break;
				// Each time exiting an element, the depth decreases
				else if (this.reader.isEndElement())
					this.depth --;
				// If character content is found, adds it to the element as content
				else if (this.reader.isCharacters() && readElement)
				{
					String textContent = this.reader.getText();
					if (textContent != null)
					{
						if (this.decodeValues)
							textContent = URLDecoder.decode(textContent, "UTF-8");
						try
						{
							// If text content is found, object type is cast into string instead
							if (type.equals(BasicDataType.OBJECT))
								type = BasicDataType.STRING;
							element.setContent(Value.String(textContent).castTo(type));
						}
						catch (DataTypeException e)
						{
							throw new ElementParseException(element.getName(), type, e);
						}
					}
				}
			}
			
			// If a special data type was found, and the element has a child
			// parses that into element content or skips it
			if (type != null && getLastDepthChange() > 0 && 
					DataTypes.getInstance().isSpecialElementParsingCase(type))
			{
				if (readElement)
				{
					try
					{
					element.setContent(DataTypes.getInstance().getSpecialParserFor(type).readValue(
							parseCurrentElement(), type));
					}
					catch (ElementValueParsingFailedException e)
					{
						throw new ElementParseException(element.getName(), type, e);
					}
				}
				else
					skipToNextSibling();
			}
			
			return element;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Utf8EncodingNotSupportedException(e);
		}
	}
	
	/**
	 * Skips to the next element at the same level (or higher) than this element
	 * @param depthDecreaseRequirement The max depth requirement
	 * @return Whether The element was found
	 * @throws XMLStreamException If read failed
	 * @throws EndOfStreamReachedException If the end of stream was reached and the element 
	 * couldn't be parsed (only on read)
	 * @throws ElementParseException If element value parsing failed (only on read)
	 */
	private Element toNextSiblingOrHigher(boolean readElement, int depthDecreaseRequirement) 
			throws XMLStreamException, EndOfStreamReachedException, ElementParseException
	{
		// Keeps track of the starting depth, parses the current element if necessary
		int startDepth = getCurrentDepth();
		Element element = toNextElementStart(readElement, true);
		
		// Keeps skipping until finding suitable element or end of stream
		while (hasNext() && getCurrentDepth() > startDepth - depthDecreaseRequirement)
		{
			toNextElementStart(false, true);
		}
		
		// Updates last depth data (since it's not the result of toNextElementStart())
		this.lastDepth = startDepth;
		return element;
	}
	
	private void readElementAttributes() throws EndOfStreamReachedException, XMLStreamException
	{
		// Can't read attributes if at the end of stream
		if (!hasNext())
			throw new EndOfStreamReachedException(
					"Can't read element attributes after reaching end of stream");
		
		try
		{
			this.currentElementAttributes = new HashMap<>();
			
			int attributeAmount = this.reader.getAttributeCount();
			for (int i = 0; i < attributeAmount; i++)
			{
				String attName = this.reader.getAttributeLocalName(i);
				if (attName.equalsIgnoreCase(XmlElementWriter.DATATYPE_ATTNAME))
					this.currentElementType = DataTypes.parseType(this.reader.getAttributeValue(i));
				else
				{
					String attributeValue = this.reader.getAttributeValue(i);
					if (this.decodeValues)
						attributeValue = URLDecoder.decode(attributeValue, "UTF-8");
					
					this.currentElementAttributes.put(attName, attributeValue);
				}
			}
			// Object is the default placeholder type, although string is used if there is content
			if (this.currentElementType == null)
				this.currentElementType = BasicDataType.OBJECT;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Utf8EncodingNotSupportedException(e);
		}
	}
	
	
	// NESTED CLASSES	------------------
	
	/**
	 * These exceptions are thrown when the reader reaches the end of the stream and can't 
	 * function properly
	 * @author Mikko Hilpinen
	 * @since 29.4.2016
	 */
	public static class EndOfStreamReachedException extends Exception
	{
		private static final long serialVersionUID = 317080985402142020L;
		
		/**
		 * Creates a new exception
		 * @param message The message sent with the exception
		 */
		public EndOfStreamReachedException(String message)
		{
			super(message);
		}
		
		/**
		 * Creates a new exception
		 */
		public EndOfStreamReachedException()
		{
			// Empty constructor
		}
	}
	
	/**
	 * These exceptions are thrown when element content cannot be parsed into correct format
	 * @author Mikko Hilpinen
	 * @since 5.5.2016
	 */
	public static class ElementParseException extends Exception
	{
		private static final long serialVersionUID = -9034168889079052418L;
		
		/**
		 * Creates a new exception
		 * @param elementName The name of the parsed element
		 * @param elementType The desired data type of the element
		 * @param cause The cause of failure
		 */
		public ElementParseException(String elementName, DataType elementType, Throwable cause)
		{
			super("Failed to parse element " + elementName + " of type " + elementType, cause);
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent along with the exception
		 */
		public ElementParseException(String message)
		{
			super(message);
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent along with the exception
		 * @param cause The cause of the exception
		 */
		public ElementParseException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
}
