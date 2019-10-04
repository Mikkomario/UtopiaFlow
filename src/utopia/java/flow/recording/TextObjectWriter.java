package utopia.java.flow.recording;

import java.io.BufferedWriter;
import java.util.Map;

import utopia.java.flow.io.FileOutputAccessor;

/**
 * TextObjectWriter writes objects into text format
 * 
 * @author Mikko Hilpinen
 * @since 25.11.2014
 * @deprecated Replaced with new generic classes and xml element parsing
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
	
	/**
	 * Writes a new instruction that applies until new instruction is given
	 * @param instructionIndicator The indicator that identifies the instruction
	 * @param instruction The content of the instruction
	 * @param writer The writer that will write the instruction
	 */
	public static void writeInstruction(String instructionIndicator, String instruction, 
			BufferedWriter writer)
	{
		FileOutputAccessor.writeLine(writer, instructionIndicator + instruction);
	}
	
	/**
	 * Writes a new instruction that applies until new instruction is given. The default 
	 * instruction indicator "%CHECK:" is used.
	 * @param instruction The content of the instruction
	 * @param writer The writer that will write the instruction
	 */
	public static void writeInstruction(String instruction, BufferedWriter writer)
	{
		writeInstruction("%CHECK:", instruction, writer);
	}
}
