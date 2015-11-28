package flow_generics;

/**
 * These exceptions are thrown when dealing with data types. They are usually caused by 
 * invalid data type use.
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public class DataTypeException extends RuntimeException
{
	// ATTIRIBUTES	------------------
	
	private static final long serialVersionUID = -868729120876227165L;
	private DataType dataType;
	
	
	// CONSTRUCTOR	------------------

	/**
	 * Creates a new exception
	 * @param type The data type that was misused
	 */
	public DataTypeException(DataType type)
	{
		super("Invalid use of data type " + type.getName());
		
		this.dataType = type;
	}

	/**
	 * Creates a new exception
	 * @param type The data type that was misused
	 * @param message The message sent along with the exception
	 */
	public DataTypeException(DataType type, String message)
	{
		super(message);
		
		this.dataType = type;
	}

	/**
	 * Creates a new exception
	 * @param type The data type that was misused
	 * @param cause The exception that caused this exception
	 */
	public DataTypeException(DataType type, Throwable cause)
	{
		super(cause);
		
		this.dataType = type;
	}

	/**
	 * Creates a new exception
	 * @param type The data type that was misused
	 * @param message The message sent along with the exception
	 * @param cause The exception that caused this exception
	 */
	public DataTypeException(DataType type, String message, Throwable cause)
	{
		super(message, cause);
		
		this.dataType = type;
	}
	
	
	// ACCESSORS	---------------
	
	/**
	 * @return The data type that was misused
	 */
	public DataType getDataType()
	{
		return this.dataType;
	}
}
