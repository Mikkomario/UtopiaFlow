package utopia.flow.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.DataType;
import utopia.flow.generics.DataTypes;
import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;

/**
 * This writer is able to write elements in xml format
 * @author Mikko Hilpinen
 * @since 29.4.2016
 */
public class XmlElementWriter implements AutoCloseable
{
	// ATTRIBUTES	------------------
	
	// TODO: Add a common interface for other writers as well (json), also, add support for 
	// other character sets

	static final String DATATYPE_ATTNAME = "dataType";
	static final String ELEMENT_INDEX_ATTNAME = "element";
	
	private XMLStreamWriter writer;
	private boolean encodeValues;
	private int elementsOpen = 0;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new xml element writer that operates on the provided stream
	 * @param stream The stream the writer writes into
	 * @param encodeElementContent Should the element data be encoded in UTF-8
	 * @throws XMLStreamException If the writer initialisation failed
	 */
	public XmlElementWriter(OutputStream stream, boolean encodeElementContent) throws 
			XMLStreamException
	{
		this.writer = XMLIOAccessor.createWriter(stream);
		this.encodeValues = encodeElementContent;
		
		// Writes the document start first
		this.writer.writeStartDocument();
	}
	
	
	// IMPLEMENTED METHODS	------------
	
	/**
	 * Closes the writer, but leaves the underlying stream open
	 * @throws XMLStreamException If the writer couldn't be closed
	 * before closing
	 */
	@Override
	public void close() throws XMLStreamException
	{
		// Writes the document end before closing
		this.writer.writeEndDocument();
		this.writer.close();
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Closes the writer but leaves the stream open. Any exceptions are catched quietly.
	 */
	public void closeQuietly()
	{
		try
		{
			close();
		}
		catch (XMLStreamException e)
		{
			XMLIOAccessor.closeWriter(this.writer);
		}
	}
	
	/**
	 * Writes a new element with no content or attributes and leaves the element open
	 * @param elementName The name of the element
	 * @throws XMLStreamException If writing failed
	 */
	public void startElement(String elementName) throws XMLStreamException
	{
		this.writer.writeStartElement(elementName);
		this.elementsOpen ++;
	}
	
	/**
	 * Writes element data but leaves the element open. The following elements will be written 
	 * inside this element until {@link #closeElement()} is called
	 * @param element The element that is written
	 * @throws XMLStreamException If the writing failed
	 */
	public void startElement(Element element) throws XMLStreamException
	{
		try
		{
			// Writes the element name / start
			startElement(element.getName());
			
			// Writes the element attributes
			for (String attName : element.getAttributeNames())
			{
				String attributeValue = element.getAttributeValue(attName);
				if (this.encodeValues)
					attributeValue = URLEncoder.encode(attributeValue, "UTF-8");
				
				this.writer.writeAttribute(attName, attributeValue);
			}
			
			// And the data type
			DataType type = element.getContentType();
			
			if (!type.equals(BasicDataType.OBJECT))
				this.writer.writeAttribute(DATATYPE_ATTNAME, type.toString());
			
			// Writes the element content as well
			if (element.hasContent())
			{
				// There are some special cases where the content is written as a separate 
				// element
				if (DataTypes.getInstance().isSpecialElementParsingCase(type))
					writeElement(DataTypes.getInstance().getSpecialParserFor(type).writeValue(
							element.getContent()));
				else
				{
					String textContent = element.getContent().toString();
					if (this.encodeValues)
						textContent = URLEncoder.encode(textContent, "UTF-8");
					this.writer.writeCharacters(textContent);
				}
			}
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Utf8EncodingNotSupportedException(e);
		}
	}
	
	/**
	 * Closes the last element left open by {@link #startElement(Element)}
	 * @throws XMLStreamException If the writing failed
	 */
	public void closeElement() throws XMLStreamException
	{
		if (this.elementsOpen > 0)
		{
			this.writer.writeEndElement();
			this.elementsOpen --;
		}
	}
	
	/**
	 * Writes the element into the stream, closing it right away. The next element will be 
	 * written outside the provided element
	 * @param element The element that is written
	 * @throws XMLStreamException If the writing failed
	 */
	public void writeElement(Element element) throws XMLStreamException
	{
		startElement(element);
		closeElement();
	}
	
	/**
	 * Writes the element tree into the stream. The node and each node below that will be 
	 * written. The element will be closed in the end and the next element will be written 
	 * outside the element(s).
	 * @param element The element tree that is written
	 * @throws XMLStreamException If the writing failed
	 */
	public void writeElement(TreeNode<Element> element) throws XMLStreamException
	{
		// Writes the element and its children
		startElement(element.getContent());
		// TODO: This method may be static part of the common interface?
		// At least in json mode, child nodes sharing names should be written under an 
		// array instead
		for (TreeNode<Element> child : element.getChildren())
		{
			writeElement(child);
		}
		closeElement();
	}
	
	/**
	 * Writes an xml element tree into a stream
	 * @param element The element that is written
	 * @param stream The stream the element is written into
	 * @param encodeElementContent Should the element contents be encoded in UTF-8
	 * @throws XMLStreamException If the xml writing failed
	 */
	public static void writeElementIntoStream(TreeNode<Element> element, OutputStream stream, 
			boolean encodeElementContent) throws XMLStreamException
	{
		XmlElementWriter writer = new XmlElementWriter(stream, encodeElementContent);
		try
		{
			writer.writeElement(element);
		}
		finally
		{
			writer.close();
		}
	}
	
	/**
	 * Writes an element into a file
	 * @param element The element that is written
	 * @param file The file to which the element is written into
	 * @param encodeElementContent Should the element contents be encoded in UTF-8
	 * @throws IOException If the file couldn't be opened / created / closed
	 * @throws XMLStreamException If the xml writing failed
	 */
	public static void writeElementIntoFile(TreeNode<Element> element, File file, 
			boolean encodeElementContent) throws IOException, XMLStreamException
	{
		OutputStream stream = new FileOutputStream(file);
		try
		{
			writeElementIntoStream(element, stream, encodeElementContent);
		}
		finally
		{
			stream.close();
		}
	}
}
