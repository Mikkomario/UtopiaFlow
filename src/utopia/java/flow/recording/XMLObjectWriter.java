package utopia.java.flow.recording;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import utopia.java.flow.io.XMLIOAccessor;

/**
 * XMLObjectWriter writes objects into XML streams.
 * 
 * @author Mikko Hilpinen
 * @since 25.11.2014
 * @deprecated Replaced with new generic classes and xml element parsing
 */
public class XMLObjectWriter extends ObjectWriter
{
	// ATTRIBUTES	------------------------------------
	
	private boolean documentOpen, instructionOpen;
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new XMLObjectWriter. Please note that the document opening and closing 
	 * is done through this writer and not through XMLIOAccessor.
	 * @see #openDocument(String, XMLStreamWriter)
	 * @see #closeDocument(XMLStreamWriter)
	 */
	public XMLObjectWriter()
	{
		// Initializes attributes
		this.documentOpen = false;
		this.instructionOpen = false;
	}
	
	
	// OTHER METHODS	--------------------------------
	
	/**
	 * Writes an object into xml stream
	 * 
	 * @param content The object that will be written to the stream
	 * @param writer The writer that will write the object
	 * @throws XMLStreamException If the writing failed unexpectedly
	 * @see #closeDocument(XMLStreamWriter)
	 */
	public void writeInto(Writable content, XMLStreamWriter writer) throws XMLStreamException
	{
		// If the document isn't open, opens it first
		if (!this.documentOpen)
			openDocument("root", writer);
		
		// Starts the object element
		writer.writeStartElement(getIDForWritable(content));
		// Writes the attributes
		Map<String, String> attributes = content.getAttributes();
		for (String key : attributes.keySet())
		{
			XMLIOAccessor.writeElementWithData(key, attributes.get(key), writer);
		}
		// Writes the links
		Map<String, Writable> links = content.getLinks();
		for (String key : links.keySet())
		{
			XMLIOAccessor.writeElementWithData(key, getIDForWritable(links.get(key)), writer);
		}
		// Closes the elements
		writer.writeEndElement();
	}
	
	/**
	 * Writes the document introduction. Including an open root element
	 * @param documentName The name of the root element
	 * @param writer The writer that is used for the writing
	 * @throws XMLStreamException If the data couldn't be written
	 */
	public void openDocument(String documentName, XMLStreamWriter writer) throws XMLStreamException
	{
		if (this.documentOpen)
		{
			System.err.println(
					"XMLObjectWriter cannot open document while the previous one is open");
			return;
		}
		
		XMLIOAccessor.writeDocumentStart(documentName, writer);
		this.documentOpen = true;
	}
	
	/**
	 * Opens a new instruction element. Closes the previous one if it was still open
	 * @param instructionName The name of the new instruction
	 * @param writer The writer that will write the instruction
	 * @throws XMLStreamException If the writing failed
	 */
	public void openInstruction(String instructionName, XMLStreamWriter writer) throws XMLStreamException
	{
		// Closes the previous instruction if there is one
		if (this.instructionOpen)
			closeInstruction(writer);
		
		// And writes the new one
		writer.writeStartElement(instructionName);
		this.instructionOpen = true;
	}
	
	/**
	 * Closes a currently active instruction
	 * @param writer The writer that will write the change
	 * @throws XMLStreamException If the writing failed
	 */
	public void closeInstruction(XMLStreamWriter writer) throws XMLStreamException
	{
		if (this.instructionOpen)
		{
			this.instructionOpen = false;
			writer.writeEndElement();
		}
	}
	
	/**
	 * Closes the xml document, if it's open
	 * @param writer The writer that will write the change
	 * @throws XMLStreamException If the writing failed
	 */
	public void closeDocument(XMLStreamWriter writer) throws XMLStreamException
	{
		// If there's still an instruction open, closes it
		if (this.instructionOpen)
			closeInstruction(writer);
		
		// Closes the document (if necessary)
		if (this.documentOpen)
		{
			XMLIOAccessor.writeDocumentEnd(writer);
			this.documentOpen = false;
		}
	}
}
