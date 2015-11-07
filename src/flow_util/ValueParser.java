package flow_util;

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
	 * @param from The type of the provided value 
	 * @param to The type of the return value
	 * @return An object parsed from the provided value to the desired type
	 */
	public Object parse(Object value, DataType from, DataType to);
	
	/**
	 * @return The value types the parser is willing to take
	 */
	public DataType[] getSupportedInputTypes();
	
	/**
	 * @return The value types the parser is willing to produce
	 */
	public DataType[] getSupportedOutputTypes();
	
	
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
			super(from, parseMessage(value, from, null));
		}
		
		/**
		 * Creates a new exception
		 * @param value The value that was being parsed
		 * @param from The data type the value was being parsed from
		 * @param to The data type the value was being parsed to
		 */
		public ValueParseException(Object value, DataType from, DataType to)
		{
			super(from, parseMessage(value, from, to));
		}
		
		/**
		 * Creates a new exception
		 * @param value The value that was being parsed
		 * @param from The data type the value was being parsed from
		 * @param cause The cause of the failure
		 */
		public ValueParseException(Object value, DataType from, Throwable cause)
		{
			super(from, parseMessage(value, from, null), cause);
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
			super(from, parseMessage(value, from, to), cause);
		}
		
		
		// OTHER METHODS	----------
		
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
