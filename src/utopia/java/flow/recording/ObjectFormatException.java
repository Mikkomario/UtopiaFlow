package utopia.java.flow.recording;

/**
 * ObjectFormatExceptions are thrown when objects can't be parsed from strings. Like 
 * numberFormatExceptions, these exceptions happen at runtime and don't have to be cached.
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
 * @deprecated Replaced with new generic classes and xml element parsing
 */
public class ObjectFormatException extends IllegalArgumentException
{
	// ATTRIBUTES	------------------
	
	private static final long serialVersionUID = 6260886176449198339L;

	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new exception
	 * @param message the message sent along with the exception
	 */
	public ObjectFormatException(String message)
	{
		super(message);
	}

}
