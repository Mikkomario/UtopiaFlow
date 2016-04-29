package utopia.flow.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.DataType;
import utopia.flow.generics.DataTypeException;
import utopia.flow.generics.DataTypes;
import utopia.flow.generics.Value;
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
	// ATTRIBUTES	-----------------
	
	private XMLStreamReader reader;
	private boolean decodeValues;
	private int depth = 0, lastDepth = 0;
	
	
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
		return this.reader.getLocalName();
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
			toNextElementStart(false);
		}
		catch (EndOfStreamReachedException e)
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
	 */
	public Element toNextElement() throws XMLStreamException, EndOfStreamReachedException
	{
		return toNextElementStart(true);
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
		catch (EndOfStreamReachedException e)
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
	 */
	public Element toNextSibling() throws XMLStreamException, EndOfStreamReachedException
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
		catch (EndOfStreamReachedException e)
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
	 */
	public Element toCloseParent() throws XMLStreamException, EndOfStreamReachedException
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
	 */
	public Element toNextElementWithName(boolean skipChildren, Filter<String> nameFilter) throws 
			XMLStreamException, EndOfStreamReachedException
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
	 */
	public TreeNode<Element> parseCurrentElement() throws XMLStreamException, EndOfStreamReachedException
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
	 */
	public static TreeNode<Element> parseStream(InputStream stream, 
			boolean decodeElementContents) throws XMLStreamException
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
	 */
	public static TreeNode<Element> parseFile(File file, boolean decodeElementContents) throws 
			IOException, XMLStreamException
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
	 */
	private Element toNextElementStart(boolean readElement) throws XMLStreamException, 
			EndOfStreamReachedException
	{
		try
		{
			// Goes inside the element first -> depth increases
			this.lastDepth = getCurrentDepth();
			this.depth ++;
			
			Element element = null;
			DataType type = null;
			
			// Initialises the element based on the current start element tag
			// Possible content is read during the process
			if (readElement)
			{
				// Can't read element data if at the end of stream
				if (!hasNext())
					throw new EndOfStreamReachedException();
				
				element = new Element(getCurrentElementName());
				int attributeAmount = this.reader.getAttributeCount();
				for (int i = 0; i < attributeAmount; i++)
				{
					String attName = this.reader.getAttributeLocalName(i);
					if (attName.equalsIgnoreCase(XmlElementWriter.DATATYPE_ATTNAME))
						type = DataTypes.parseType(this.reader.getAttributeValue(i));
					else
					{
						String attributeValue = this.reader.getAttributeValue(i);
						if (this.decodeValues)
							attributeValue = URLDecoder.decode(attributeValue, "UTF-8");
						
						element.addAttribute(attName, attributeValue);
					}
				}
				// String is the default data type used
				if (type == null)
					type = BasicDataType.STRING;
			}
			
			while (hasNext())
			{
				this.reader.next();
				if (this.reader.isStartElement())
					break;
				// Each time exiting an element, the depth decreases
				else if (this.reader.isEndElement())
					this.depth --;
				// If character content is found, adds it to the element
				else if (this.reader.isCharacters() && readElement)
				{
					String textContent = this.reader.getText();
					if (textContent != null)
					{
						if (this.decodeValues)
							textContent = URLDecoder.decode(textContent, "UTF-8");
						try
						{
							element.setContent(Value.String(textContent).castTo(type));
						}
						catch (DataTypeException e)
						{
							// Non-parseable content is ignored
						}
					}
				}
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
	 */
	private Element toNextSiblingOrHigher(boolean readElement, int depthDecreaseRequirement) 
			throws XMLStreamException, EndOfStreamReachedException
	{
		// Keeps track of the starting depth, parses the current element if necessary
		int startDepth = getCurrentDepth();
		Element element = toNextElementStart(readElement);
		
		// Keeps skipping until finding suitable element or end of stream
		while (hasNext() && getCurrentDepth() > startDepth - depthDecreaseRequirement)
		{
			toNextElementStart(false);
		}
		
		// Updates last depth data (since it's not the result of toNextElementStart())
		this.lastDepth = startDepth;
		return element;
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
}
