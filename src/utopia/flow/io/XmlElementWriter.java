package utopia.flow.io;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;

/**
 * This writer is able to write elements in xml format
 * @author Mikko Hilpinen
 * @since 29.4.2016
 */
public class XmlElementWriter
{
	// ATTRIBUTES	------------------
	
	static final String DATATYPE_ATTNAME = "dataType";
	
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
	
	/*
	public XmlElementWriter(File file) throws FileNotFoundException
	{
		this.stream = new FileOutputStream(file);
	}*/
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Closes the writer, but leaves the underlying stream open
	 * @throws XMLStreamException If the writer couldn't be closed
	 * before closing
	 */
	public void close() throws XMLStreamException
	{
		// Writes the document end before closing
		this.writer.writeEndDocument();
		this.writer.close();
	}
	
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
	 * Writes element data but leaves the element open. The following elements will be written 
	 * inside this element until {@link #closeElement()} is called
	 * @param element The element that is written
	 * @throws XMLStreamException If the writing failed
	 */
	public void startElement(Element element) throws XMLStreamException
	{
		try
		{
			this.writer.writeStartElement(element.getName());
			
			// Writes the element attributes
			for (String attName : element.getAttributeNames())
			{
				String attributeValue = element.getAttributeValue(attName);
				if (this.encodeValues)
					attributeValue = URLEncoder.encode(attributeValue, "UTF-8");
			}
			
			// Writes the element content as well
			if (element.hasContent())
			{
				this.writer.writeAttribute(DATATYPE_ATTNAME, element.getContent().getType().toString());
				String textContent = element.getContent().toString();
				if (this.encodeValues)
					textContent = URLEncoder.encode(textContent, "UTF-8");
				this.writer.writeCharacters(textContent);
			}
			
			this.elementsOpen ++;
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
		for (TreeNode<Element> child : element.getChildren())
		{
			writeElement(child);
		}
		closeElement();
	}
}
