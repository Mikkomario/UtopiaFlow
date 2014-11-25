package flow_io;

import java.io.BufferedWriter;
import java.util.Map;

/**
 * TextObjectWriter writes objects into text format
 * 
 * @author Mikko Hilpinen
 * @since 25.11.2014
 */
public class TextObjectWriter extends ObjectWriter
{
	// OTHER METHODS	--------------------------------
	
	/**
	 * Writes the given object's data.
	 * 
	 * @param content The object that will be written
	 * @param writer The writer that will write the object's data
	 */
	public void writeInto(Writable content, BufferedWriter writer)
	{
		// Writes the object introduction
		FileOutputAccessor.writeLine(writer, getIDForWritable(content));
		// Writes the attributes
		Map<String, String> attributes = content.getAttributes();
		for (String key : attributes.keySet())
		{
			FileOutputAccessor.writeLine(writer, key + "=" + attributes.get(key));
		}
		// Writes the links to other objects
		Map<String, Writable> links = content.getLinks();
		for (String key : links.keySet())
		{
			FileOutputAccessor.writeLine(writer, key + "=" + getIDForWritable(links.get(key)));
		}
	}
}
