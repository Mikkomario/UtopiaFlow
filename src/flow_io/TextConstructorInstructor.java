package flow_io;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * TextConstructor uses lines of text when constructing new constructables
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of object constructed by this constructor
 * @since 24.11.2014
 */
public class TextConstructorInstructor<T extends Constructable<T>>
{
	// ATTRIBUTES	---------------------------------------
	
	private AbstractConstructor<T> constructor;
	
	
	// CONSTRUCTOR	---------------------------------------
	
	/**
	 * Creates a new constructor that uses the given constructor for creating the objects
	 * @param constructor The constructor that creates the objects
	 */
	public TextConstructorInstructor(AbstractConstructor<T> constructor)
	{
		this.constructor = constructor;
	}
	
	
	// OTHER METHODS	-----------------------------------
	
	/**
	 * Makes constructs according to an instruction on the given line
	 * @param line The line that instructs in constructing. The line should start with 
	 * ID_INDICATOR if it represents a start of an constructable. Otherwise the line 
	 * should contain a key and a value separated with a '='.
	 */
	public void onLine(String line)
	{
		// Checks if a new constructable is created
		if (line.startsWith(AbstractConstructor.ID_INDICATOR))
			this.constructor.create(line);
		else
		{
			int splitIndex = line.indexOf('=');
			if (splitIndex < 0 || splitIndex == line.length() - 1)
				throw new AbstractConstructor.ConstructorException("Attributes must have values");
			String key = line.substring(0, splitIndex);
			String value = line.substring(splitIndex + 1);
			
			// Otherwise checks if a link is created
			if (value.startsWith(AbstractConstructor.ID_INDICATOR))
				this.constructor.addLink(key, value);
			// Otherwise creates an attribute
			else
				this.constructor.addAttribute(key, value);
		}
	}
	
	/**
	 * Makes constructs according to instructions on the given lines
	 * @param lines The lines that instruct in constructing. A line should start with 
	 * ID_INDICATOR if it represents a start of an constructable. Otherwise the line 
	 * should contain a key and a value separated with a '='.
	 */
	public void onLines(List<String> lines)
	{
		for (String line : lines)
		{
			onLine(line);
		}
	}
	
	/**
	 * Constructs objects according to instructions in the given file
	 * 
	 * @param fileName The name of the file that will be read ("data/" automatically included)
	 * @param commentIndicator The indicates which lines are comments
	 * @throws FileNotFoundException If a file with the given name can't be found
	 */
	public void constructFromFile(String fileName, String commentIndicator) throws FileNotFoundException
	{
		TextFileReader reader = new TextFileReader();
		reader.readFile(fileName, commentIndicator);
	}
	
	
	// SUBCLASSES	--------------------------------------
	
	private class TextFileReader extends AbstractFileReader
	{
		@Override
		protected void onLine(String line)
		{
			TextConstructorInstructor.this.onLine(line);
		}	
	}
}
