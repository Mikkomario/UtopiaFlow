package flow_io;

/**
 * TextConstructor uses lines of text when constructing new constructables
 * 
 * @author Mikko Hilpinen
 * @since 24.11.2014
 * @param <T> The type of constructable created by this constructor
 */
public abstract class TextConstructor<T extends Constructable<T>> extends AbstractConstructor<T>
{
	// OTHER METHODS	-----------------------------------
	
	/**
	 * Makes constructs according an instruction on the given line
	 * @param line The line that instructs in constructing. The line should start with 
	 * ID_INDICATOR if it represents a start of an constructable. Otherwise the line 
	 * should contain a key and a value separated with a '='.
	 */
	public void onLine(String line)
	{
		// Checks if a new constructable is created
		if (line.startsWith(ID_INDICATOR))
			create(line);
		else
		{
			int splitIndex = line.indexOf('=');
			if (splitIndex < 0 || splitIndex == line.length() - 1)
				throw new ConstructorException("Attributes must have values");
			String key = line.substring(0, splitIndex);
			String value = line.substring(splitIndex + 1);
			
			// Otherwise checks if a link is created
			if (value.startsWith(ID_INDICATOR))
				addLink(key, value);
			// Otherwise creates an attribute
			else
				addAttribute(key, value);
		}
	}
}
