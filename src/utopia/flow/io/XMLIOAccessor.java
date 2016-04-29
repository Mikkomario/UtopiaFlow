package utopia.flow.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import utopia.flow.recording.ObjectParser;
import utopia.flow.structure.TreeNode;

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
	 * 
	 * @param targetStream The output stream the writer will write the data into
	 * @return a new instance of an XMLStreamWriter.
	 * @throws Utf8EncodingNotSupportedException If the stream doesn't support UTF-8 encoding
	 * @throws XMLStreamException If something goes wrong during the creation of the writer
	 */
	public static XMLStreamWriter createWriter(OutputStream targetStream) 
			throws Utf8EncodingNotSupportedException, XMLStreamException
	{
		try
		{
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			return factory.createXMLStreamWriter(new OutputStreamWriter(targetStream, "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new Utf8EncodingNotSupportedException(e);
		}
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
	 * Writes the tree into an xml stream
	 * @param tree The tree that will be written into the stream
	 * @param parser The parser that converts tree data into strings
	 * @param writer The writer that will do the writing
	 * @throws XMLStreamException if the writing fails
	 */
	public static <T> void writeTree(TreeNode<T> tree, ObjectParser<T> parser, 
			XMLStreamWriter writer) throws XMLStreamException
	{
		TreeNode<T> currentNode = tree;
		
		while (currentNode != null)
		{
			// Writes the latest element
			if (currentNode.hasChildren())
			{
				writer.writeStartElement(parser.parseToString(currentNode.getContent()));
				
				// If the current node has children left, handles the first one next
				currentNode = currentNode.getChild(0);
			}
			else
			{
				writer.writeEmptyElement(parser.parseToString(currentNode.getContent()));
			
				// Otherwise, if the node has a right sibling, handles that next
				TreeNode<T> sibling = currentNode.getRightSibling();
				if (sibling != null)
					currentNode = sibling;
				else
				{
					// Otherwise checks until finds a parents right sibling
					while (sibling == null && currentNode != null)
					{
						sibling = currentNode.getRightSibling();
						currentNode = currentNode.getParent();
						
						if (currentNode != null && sibling == null)
							writer.writeEndElement();
					}
					// Until all are written
					if (sibling != null)
						currentNode = sibling;
				}
			}
		}
	}
	
	/**
	 * Creates a new XMLStreamReader positioned to the start of the xml stream. The ownership 
	 * of the reader moves to the calling object and it has to make sure the reader is closed 
	 * after use.
	 * 
	 * @param stream the xml data as a stream
	 * @return An xmlReader prepared to read the given xml data.
	 * @throws UnsupportedEncodingException If the data doesn't support UTF-8 encoding
	 * @throws XMLStreamException if something goes wrong during the creation of the reader
	 */
	public static XMLStreamReader createReader(InputStream stream) 
			throws UnsupportedEncodingException, XMLStreamException
	{
		XMLInputFactory factory = XMLInputFactory.newInstance();
		InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
		
		try
		{
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
	
	/**
	 * Creates a tree from xml stream
	 * @param parent The parent node for the read tree
	 * @param parser A parser that is able to parse objects from strings
	 * @param stream The xml that contains the tree data
	 * @return A tree constructed from the xml data (same as the given parent node)
	 * @throws UnsupportedEncodingException If the stream doesn't support UTF-8
	 * @throws XMLStreamException If the reading failed
	 */
	public static <T> TreeNode<T> readTree(TreeNode<T> parent, ObjectParser<T> parser, 
			InputStream stream) throws UnsupportedEncodingException, XMLStreamException
	{
		XMLStreamReader reader = createReader(stream);
		TreeNode<T> tree = parent;
		
		try
		{
			TreeNode<T> lastNode = tree;
			boolean rootElementSkipped = false;
		
			while (reader.hasNext() && lastNode != null)
			{
				// On new element, creates a new node
				if (reader.isStartElement())
				{
					// Skips the root element
					if (!rootElementSkipped)
						rootElementSkipped = true;
					else
						lastNode = new TreeNode<T>(parser.parseFromString(reader.getLocalName()), 
								lastNode);
				}
				// On element end, moves upwards in the tree
				else if (reader.isEndElement())
					lastNode = lastNode.getParent();
				
				reader.next();
			}
		}
		finally
		{
			// Closes the reader as well
			closeReader(reader);
		}
		
		return tree;
	}
}
