package utopia.flow.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * XMLIOAccessor is a class that provides static interfaces for creating and using 
 * xml writers and readers.
 * @author Mikko Hilpinen
 * @since 30.7.2014
 */
public class XMLIOAccessor
{
	// CONSTRUCTOR	-------------------------------------------
	
	private XMLIOAccessor()
	{
		// The constructor is hidden from others since the interface is static
	}

	
	// OTHER METHODS	------------------------------------------
	
	/**
	 * Creates a new XMLStreamWriter. The ownership of the writer moves to the object that 
	 * calls this method. The caller must close the writer after use.
	 * @param targetStream The output stream the writer will write the data into
	 * @return a new instance of an XMLStreamWriter.
	 * @throws XMLStreamException If something goes wrong during the creation of the writer
	 */
	public static XMLStreamWriter createWriter(OutputStream targetStream) 
			throws XMLStreamException
	{
		return createWriter(targetStream, StandardCharsets.UTF_8);
	}
	
	/**
	 * Creates a new XMLStreamWriter. The ownership of the writer moves to the object that 
	 * calls this method. The caller must close the writer after use.
	 * @param targetStream The output stream the writer will write the data into
	 * @param charset The character set used by the writer
	 * @return a new instance of an XMLStreamWriter.
	 * @throws XMLStreamException If something goes wrong during the creation of the writer
	 */
	public static XMLStreamWriter createWriter(OutputStream targetStream, Charset charset) 
			throws XMLStreamException
	{
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		return factory.createXMLStreamWriter(new OutputStreamWriter(targetStream, charset));
	}
	
	/**
	 * Closes a currently open xmlStreamWriter safely.
	 * 
	 * @param writer The writer that will be closed
	 */
	public static void closeWriter(XMLStreamWriter writer)
	{
		// Closes the writer
		if (writer != null)
		{
			try
			{
				writer.flush();
				writer.close();
			}
			catch (XMLStreamException e)
			{
				System.err.println("Failed to close an xmlWriter");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Writes an element that represents a simple attribute / key value pair
	 *  into the stream
	 * 
	 * @param elementName The name of the element
	 * @param elementContent The content written into the given element, parsed into a string
	 * @param writer The writer that will write the element into the xml stream
	 * @throws XMLStreamException In case something goes wrong during the write.
	 */
	public static void writeElementWithData(String elementName, String elementContent, 
			XMLStreamWriter writer) throws XMLStreamException
	{
		writer.writeStartElement(elementName);
		writer.writeCData(elementContent);
		writer.writeEndElement();
	}
	
	/**
	 * Writes a resource link with the given writer
	 * 
	 * @param linkUrl the whole url to the target
	 * @param writer The XMLStreamWriter that will write the link into a stream
	 * @param writeNamespace Should the namespace be introduced at this point (optional, 
	 * default = true)
	 * @throws XMLStreamException In case something goes wrong during the write
	 */
	public static void writeLinkAsAttribute(String linkUrl, XMLStreamWriter writer, 
			boolean writeNamespace) throws XMLStreamException
	{
		if (writeNamespace)
		{
			writeXLinkNamespaceIntroduction(writer);
			writer.writeAttribute("xlink", "http://www.w3.org/1999/xlink", "href", linkUrl);
		}
		else
			writer.writeAttribute("xlink:href", linkUrl);
	}
	
	/**
	 * Writes a resource link with the given writer
	 * 
	 * @param linkUrl the whole url to the target
	 * @param writer The XMLStreamWriter that will write the link into a stream
	 * @throws XMLStreamException In case something goes wrong during the write
	 */
	public static void writeLinkAsAttribute(String linkUrl, XMLStreamWriter writer) throws 
			XMLStreamException
	{
		writeLinkAsAttribute(linkUrl, writer, true);
	}
	
	/**
	 * Introduces the XLink namespace
	 * @param writer The writer that will write the namespace
	 * @throws XMLStreamException If the namespace couldn't be written
	 */
	public static void writeXLinkNamespaceIntroduction(XMLStreamWriter writer) throws XMLStreamException
	{
		writer.writeNamespace("xlink", "http://www.w3.org/1999/xlink");
	}
	
	/**
	 * Writes the start of the document
	 * @param rootElementName The name of the root element that contains all other elements
	 * @param writer The writer that does the writing
	 * @throws XMLStreamException If the writing failed
	 * @see #writeDocumentEnd(XMLStreamWriter)
	 */
	public static void writeDocumentStart(String rootElementName, XMLStreamWriter writer) throws XMLStreamException
	{
		writer.writeStartDocument();
		writer.writeStartElement(rootElementName);
	}
	
	/**
	 * Closes the root element and writer the end of the document.
	 * @param writer The writer that does the writing
	 * @throws XMLStreamException If the writing failed
	 */
	public static void writeDocumentEnd(XMLStreamWriter writer) throws XMLStreamException
	{
		writer.writeEndElement();
		writer.writeEndDocument();
	}
	
	/**
	 * Creates a new XMLStreamReader positioned to the start of the xml stream. The ownership 
	 * of the reader moves to the calling object and it has to make sure the reader is closed 
	 * after use.
	 * @param stream the xml data as a stream
	 * @return An xmlReader prepared to read the given xml data.
	 * @throws XMLStreamException if something goes wrong during the creation of the reader
	 */
	public static XMLStreamReader createReader(InputStream stream) 
			throws XMLStreamException
	{
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStreamReader reader = null;
		
		try
		{
			reader = new InputStreamReader(stream, "UTF-8");
			return factory.createXMLStreamReader(reader);
		}
		catch (XMLStreamException e)
		{
			try
			{
				reader.close();
			}
			catch (IOException e1)
			{
				// Ignored
			}
			throw e;
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Utf8EncodingNotSupportedException(e);
		}
	}
	
	/**
	 * Safely closes an xml reader
	 * 
	 * @param reader The reader that has been used for reading xml data
	 */
	public static void closeReader(XMLStreamReader reader)
	{
		if (reader != null)
		{
			try
			{
				reader.close();
			}
			catch (XMLStreamException e)
			{
				System.err.println("Failed to close an xml reader");
				e.printStackTrace();
			}
		}
	}
}
