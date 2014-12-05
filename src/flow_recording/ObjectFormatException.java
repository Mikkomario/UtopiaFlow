package flow_recording;

/**
 * ObjectFormatExceptions are thrown when objects can't be parsed from strings. Like 
 * numberFormatExceptions, these exceptions happen at runtime and don't have to be cached.
 * 
 * @author Mikko Hilpinen
 * @since 5.12.2014
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
