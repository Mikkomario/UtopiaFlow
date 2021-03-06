package utopia.flow.generics;

import utopia.flow.structure.ImmutableList;

/**
 * An instance of this class can parse a value into an object
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public interface ValueParser
{
	/**
	 * Parses a value of the given type into a certain object type
	 * @param value The value that should be parsed
	 * @param to The type of the return value
	 * @return An object parsed from the provided value to the desired type
	 * @throws ValueParseException If the parsing failed
	 */
	public Value cast(Value value, DataType to) throws ValueParseException;
	
	/**
	 * @return The conversions the parser is able to make
	 */
	public ImmutableList<Conversion> getConversions();
	
	
	// SUBCLASSES	------------------
	
	/**
	 * These exceptions are thrown when the parsing process fails
	 * @author Mikko Hilpinen
	 * @since 7.11.2015
	 */
	public static class ValueParseException extends DataTypeException
	{
		private static final long serialVersionUID = 5837207703916768976L;
		
		// CONSTRUCTOR	--------------

		/**
		 * Creates a new exception
		 * @param value The value that was being parsed
		 * @param from The data type the value was being parsed from
		 */
		public ValueParseException(Object value, DataType from)
		{
			super(parseMessage(value, from, null));
		}
		
		/**
		 * Creates a new exception
		 * @param value The value that was being parsed
		 * @param from The data type the value was being parsed from
		 * @param to The data type the value was being parsed to
		 */
		public ValueParseException(Object value, DataType from, DataType to)
		{
			super(parseMessage(value, from, to));
		}
		
		/**
		 * Creates a new exception
		 * @param value The value that was being parsed
		 * @param from The data type the value was being parsed from
		 * @param cause The cause of the failure
		 */
		public ValueParseException(Object value, DataType from, Throwable cause)
		{
			super(parseMessage(value, from, null), cause);
		}
		
		/**
		 * Creates a new exception
		 * @param value The value that was being parsed
		 * @param from The data type the value was being parsed from
		 * @param to The data type the value was being parsed to
		 * @param cause The cause of the failure
		 */
		public ValueParseException(Object value, DataType from, DataType to, Throwable cause)
		{
			super(parseMessage(value, from, to), cause);
		}
		
		/**
		 * Creates a new value parse exception
		 * @param value The value that couldn't be parsed
		 * @param to The target data type that couldn't be parsed
		 */
		public ValueParseException(Value value, DataType to)
		{
			super(parseMessage(value, to, (String) null));
		}
		
		/**
		 * Creates a new value parse exception
		 * @param value The value that couldn't be parsed
		 * @param to The target data type that couldn't be parsed
		 * @param message The message sent along with the exception
		 */
		public ValueParseException(Value value, DataType to, String message)
		{
			super(parseMessage(value, to, message));
		}
		
		/**
		 * Creates a new value parse exception
		 * @param value The value that couldn't be parsed
		 * @param to The target data type that couldn't be parsed
		 * @param message The message sent along with the exception
		 * @param cause The cause of the exception
		 */
		public ValueParseException(Value value, DataType to, String message, Throwable cause)
		{
			super(parseMessage(value, to, message), cause);
		}
		
		/**
		 * Creates a new value parse exception
		 * @param value The value that couldn't be parsed
		 */
		public ValueParseException(Value value)
		{
			super(parseMessage(value == null ? null : value.getObjectValue(), 
					value == null ? null : value.getType(), null));
		}
		
		
		// OTHER METHODS	----------
		
		private static String parseMessage(Value value, DataType to, String extra)
		{
			StringBuilder s = new StringBuilder();
			
			s.append("Can't parse ");
			
			if (value != null)
				s.append(value.getDescription());
			else
				s.append("?");
			
			if (to != null)
			{
				s.append(" to ");
				s.append(to);
			}
			else
				s.append(" to ?");
			
			if (extra != null)
			{
				s.append("; ");
				s.append(extra);
			}
			
			return s.toString();
		}
		
		private static String parseMessage(Object value, DataType from, DataType to)
		{	
			StringBuilder s = new StringBuilder();
			s.append("Can't parse ");
			
			if (value == null)
				s.append("null");
			else
				s.append(value);
			
			if (from != null)
			{
				s.append(" (");
				s.append(from.getName());
				s.append(")");
			}
			if (to != null)
			{
				s.append(" to ");
				s.append(to.getName());
			}
			
			return s.toString();
		}
	}
}
