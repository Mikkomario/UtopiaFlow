package flow_io;

import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * XMLObjectWriter writes objects into XML streams.
 * 
 * @author Mikko Hilpinen
 * @since 25.11.2014
 */
public class XMLObjectWriter extends ObjectWriter
{
	// OTHER METHODS	--------------------------------
	
	/**
	 * Writes an object into xml stream
	 * 
	 * @param content The object that will be written to the stream
	 * @param writer The writer that will write the object
	 * @throws XMLStreamException If the writing failed unexpectedly
	 */
	public void writeInto(Writable content, XMLStreamWriter writer) throws XMLStreamException
	{
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
}
