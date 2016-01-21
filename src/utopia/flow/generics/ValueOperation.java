package utopia.flow.generics;

/**
 * Value operations are used for differentiating different operations performed on values
 * @author Mikko Hilpinen
 * @since 5.12.2015
 */
public interface ValueOperation
{
	/**
	 * Checks whether the two operations should be considered identical.
	 * @param other Another value operation
	 * @return Are the two operations identical
	 */
	public boolean equals(ValueOperation other);
	
	
	// NESTED CLASSES	----------------
	
	// TODO: Add more detailed exception types (cast, unsupported operation, null pointer)
	
	/**
	 * These exceptions are thrown when value operations are misused or when they fail
	 * @author Mikko Hilpinen
	 * @since 6.12.2015
	 */
	public static class ValueOperationException extends DataTypeException
	{
		private static final long serialVersionUID = 304247002996123851L;
		
		// CONSTRUCTOR	----------------
		
		/**
		 * Creates a new exception
		 * @param operation The operation that was being used
		 * @param first The first value (may be null)
		 * @param second The second value (may be null)
		 */
		public ValueOperationException(ValueOperation operation, Value first, Value second)
		{
			super(parseMessage(operation, first, second));
		}
		
		/**
		 * Creates a new exception
		 * @param operation The operation that was being used
		 * @param first The first value (may be null)
		 * @param second The second value (may be null)
		 * @param cause The exception that caused this exception
		 */
		public ValueOperationException(ValueOperation operation, Value first, Value second, 
				Throwable cause)
		{
			super(parseMessage(operation, first, second), cause);
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent along with the exception
		 */
		public ValueOperationException(String message)
		{
			super(message);
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent along with the exception
		 * @param cause The exception that caused this exception
		 */
		public ValueOperationException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
		
		// OTHER METHODS	------------
		
		private static String parseMessage(ValueOperation operation, Value first, Value second)
		{
			StringBuilder s = new StringBuilder();
			
			s.append("Failed to perform operation ");
			s.append(operation);
			s.append(" on ");
			
			if (first == null)
				s.append("null");
			else
			{
				if (first.isNull())
					s.append("null");
				else
					s.append(first.toString());
				
				s.append(" (");
				s.append(first.getType());
				s.append(")");
			}
			
			s.append(" and ");
			
			if (second == null)
				s.append("null");
			else
			{
				if (second.isNull())
					s.append("null");
				else
					s.append(second.toString());
				
				s.append(" (");
				s.append(second.getType());
				s.append(")");
			}
			
			return s.toString();
		}
	}
}
