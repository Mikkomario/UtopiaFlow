package utopia.flow.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Filereader is an abstract class that allows the subclasses to read files 
 * and react to their content.
 *
 * @author Mikko Hilpinen.
 * @since 19.7.2013.
 */
public abstract class AbstractFileReader
{
	// ABSTRACT METHODS	--------------------------------------------------
	
	/**
	 * This method is called each time a line is read from a file with the 
	 * {@link #readFile(File, String)} method
	 * @param line The line read from the file
	 */
	protected abstract void onLine(String line);
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public String toString()
	{
		return getClass().getName();
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Reads a file and makes the object react to it somehow. Skips all the 
	 * empty lines in the file and calls the onLine method at each line read.
	 * @param file The file that will be read
	 * @param commentIndicator The lines starting with this string will be ignored as comments
	 * @throws FileNotFoundException If the file wasn't found
	 */
	public void readFile(File file, String commentIndicator) throws FileNotFoundException
	{
		Scanner scanner = null;
		
		// Tries to open the file
		scanner = new Scanner(file);
		try
		{
			String line = "";
			
			// Reads the file
			// Loops until the file ends
			while (scanner.hasNextLine())
			{	
				line = scanner.nextLine();
				
				// Skips the empty lines
				if (line.isEmpty())
					continue;
				
				// Skips the lines recognised as comments
				if (commentIndicator != null && line.startsWith(commentIndicator))
					continue;
				
				onLine(line);
			}
		}
		finally
		{
			// Closes the file in the end
			scanner.close();
		}
	}
}
