package utopia.flow.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * ListFileReader reads the contents of the file into a list. This reader shouldn't be used 
 * with overly large files.
 * @author Mikko Hilpinen
 * @since 21.11.2014
 * @deprecated Please use {@link FileUtils} instead
 */
public class ListFileReader extends AbstractFileReader
{
	// ATTRIBUTES	------------------------------------
	
	private List<String> lines = new ArrayList<>();
	
	
	// IMPLEMENTED METHODS	----------------------------

	@Override
	protected void onLine(String line)
	{
		this.lines.add(line);
	}

	@Override
	public void readFile(File file, String commentIndicator) throws FileNotFoundException
	{
		// Empties the last read results first
		this.lines.clear();
		super.readFile(file, commentIndicator);
	}
	
	
	// OTHER METHODS	--------------------------------
	
	/**
	 * @return A list of non-comment lines in the previously read file
	 */
	public List<String> getLines()
	{
		return this.lines;
	}
	
	/**
	 * Reads the lines of a single file
	 * @param file a file
	 * @param commentIndicator A string indicating that the line should be skipped
	 * @return The lines read from the file
	 * @throws FileNotFoundException If the file didn't exist
	 */
	public static List<String> readLines(File file, String commentIndicator) throws FileNotFoundException
	{
		ListFileReader reader = new ListFileReader();
		reader.readFile(file, commentIndicator);
		return reader.getLines();
	}
}
