package utopia.flow.io;

import utopia.flow.generics.DataType;
import utopia.flow.generics.Value;
import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;

/**
 * Most times a value is simple parsed into / from a single element by casting it into a String. 
 * Sometimes this is not possible, however. Sometimes a value needs to be parsed into / from 
 * multiple (xml) elements. These parsers are used for that purpose.<br>Remember that you 
 * have to introduce these parsers to the appropriate readers / writers before they can 
 * be automatically used.
 * @author Mikko Hilpinen
 * @since 3.5.2016
 */
public interface ElementValueParser
{
	/**
	 * @return The data types this parser supports. 
	 * {@link #writeValue(Value)} and {@link #readValue(TreeNode, DataType)} should only be called 
	 * for these data types
	 */
	public DataType[] getParsedTypes();
	
	/**
	 * Writes a value into one or more elements of different data types
	 * @param value The value that is being written
	 * @return The value in multiple elements with different data types
	 * @throws ElementValueParsingFailedException If the writing failed
	 */
	public TreeNode<Element> writeValue(Value value) throws ElementValueParsingFailedException;
	
	/**
	 * Parses a value from multiple elements. Calling {@link #readValue(TreeNode, DataType)} for elements 
	 * created with {@link #writeValue(Value)} should result in a value equal to the 
	 * original written value
	 * @param element The element(s) that need to be parsed into a value
	 * @param targetType The data type the return value should have
	 * @return The value read from the elements
	 * @throws ElementValueParsingFailedException If the parsing failed
	 */
	public Value readValue(TreeNode<Element> element, DataType targetType) throws 
			ElementValueParsingFailedException;
	
	
	// NESTED CLASSES	---------------
	
	/**
	 * These exceptions are thrown when element value parsing fails for some reason
	 * @author Mikko Hilpinen
	 * @since 3.5.2016
	 */
	public static class ElementValueParsingFailedException extends Exception
	{
		private static final long serialVersionUID = 5001810465073844051L;

		/**
		 * Creates a new exception
		 * @param message The message describing the exception
		 */
		public ElementValueParsingFailedException(String message)
		{
			super(message);
		}
		
		/**
		 * Creates a new exception
		 * @param message The message describing the exception
		 * @param cause The cause of the exception
		 */
		public ElementValueParsingFailedException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}
}
