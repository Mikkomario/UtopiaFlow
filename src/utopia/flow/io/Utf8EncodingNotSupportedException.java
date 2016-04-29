package utopia.flow.io;

import java.io.UnsupportedEncodingException;

/**
 * These exceptions are thrown when the utf-8 encoding isn't supported for some reason
 * @author Mikko Hilpinen
 * @since 29.4.2016
 */
public class Utf8EncodingNotSupportedException extends RuntimeException
{
	private static final long serialVersionUID = 4794848627094134228L;

	/**
	 * Wraps a regular encoding exception into a runtime exception
	 * @param cause The encoding exception
	 */
	public Utf8EncodingNotSupportedException(UnsupportedEncodingException cause)
	{
		super(cause);
	}
}
