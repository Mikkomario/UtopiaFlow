package utopia.java.flow.structure;

import java.util.NoSuchElementException;

/**
 * This class is a non-runtime version of {@link NullPointerException} and / or {@link NoSuchElementException}
 * @author Mikko Hilpinen
 * @since 25.2.2019
 */
public class EmptyResultException extends Exception
{
	private static final long serialVersionUID = -9136381381186512480L;

	/**
	 * Creates a new exception
	 * @param message The exception message
	 */
	public EmptyResultException(String message)
	{
		super(message);
	}
	
	/**
	 * Creates a new exception
	 * @param message The exception message
	 * @param cause The cause of this exception
	 */
	public EmptyResultException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	/**
	 * Creates a new exception
	 * @param cause The cause of this exception
	 */
	public EmptyResultException(Throwable cause)
	{
		super(cause);
	}
}
