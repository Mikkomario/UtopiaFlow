package utopia.flow.parse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import utopia.flow.structure.Try;

/**
 * This is a static collection of utility methods for xml file interaction
 * @author Mikko Hilpinen
 * @since 20.3.2018
 */
public class XmlFileUtils
{
	// CONSTRUCTOR	---------------------
	
	private XmlFileUtils() { }

	
	// OTHER METHODS	-----------------
	
	/**
	 * Parses an xml element from a file
	 * @param file The target file
	 * @param decode Whether the element contents are encoded and should be decoded
	 * @return An xml element parsed from the file, or a failure
	 */
	@SuppressWarnings("unused")
	public static Try<XmlElement> parseFile(File file, boolean decode)
	{
		// TODO: Implement
		return null;
		// return FileJdomUtils.parseFile(file).map(e -> XmlElement.wrap(e, decode));
	}
	
	/**
	 * Writes an xml element into a file
	 * @param root The xml element that will be written
	 * @param filePath The target file path
	 * @param encode Whether the xml element contents should be decoded before writing
	 * @param hide Whether the file should be hidden
	 * @throws IOException If write failed
	 */
	@SuppressWarnings("unused")
	public static void writeFile(XmlElement root, Path filePath, boolean encode, boolean hide) throws IOException
	{
		// TODO: Implement
		// FileJdomUtils.writeFile(root.toJdomElement(encode), filePath, hide);
	}
}
