package flow_fileIO;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * ListFileReader reads the contents of the file into a list. This reader shouldn't be used 
 * with overly large files.
 * 
 * @author Mikko Hilpinen
 * @since 21.11.2014
 */
public class ListFileReader extends AbstractFileReader
{
	// ATTRIBUTES	------------------------------------
	
	private List<String> lines;
	
	
	// CONSTRUCTOR	------------------------------------
	
	/**
	 * Creates a new file reader
	 */
	public ListFileReader()
	{
		// Initializes attributes
		this.lines = new ArrayList<String>();
	}
	
	
	// IMPLEMENTED METHODS	----------------------------

	@Override
	protected void onLine(String line)
	{
		this.lines.add(line);
	}

	@Override
	public void readFile(String fileName, String commentIndicator) throws FileNotFoundException
	{
		// Empties the last read results first
		this.lines.clear();
		super.readFile(fileName, commentIndicator);
	}
	
	
	// OTHER METHODS	--------------------------------
	
	/**
	 * @return A list of non-comment lines in the previously read file
	 */
	public List<String> getLines()
	{
		return this.lines;
	}
}
