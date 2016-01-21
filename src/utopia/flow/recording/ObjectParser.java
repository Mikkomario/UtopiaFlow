package utopia.flow.recording;

/**
 * Converters are able to convert certain types of objects into Strings and vice versa
 * 
 * @author Mikko Hilpinen
 * @param <T> The type of object this converter converts
 * @since 5.12.2014
 */
public interface ObjectParser<T>
{
	/**
	 * Parses an object into string format
	 * 
	 * @param object The object that will be parsed to string format
	 * @return A string parsed from the object
	 */
	public String parseToString(T object);
	
	/**
	 * Parses an object from a string
	 * 
	 * @param s The string that should be parsed into an object
	 * @return An object from the string
	 * @throws ObjectFormatException If the object can't be parsed from the string
	 */
	public T parseFromString(String s) throws ObjectFormatException;
	
	
	// SUBCLASSES	------------------------
	
	/**
	 * StringParser "parses" strings from strings
	 * 
	 * @author Mikko Hilpinen
	 * @since 5.12.2014
	 */
	public static class StringParser implements ObjectParser<String>
	{
		// IMPLEMENTED METHODS	------------
		
		@Override
		public String parseToString(String object)
		{
			return object;
		}

		@Override
		public String parseFromString(String s) throws ObjectFormatException
		{
			return s;
		}
	}
	
	/**
	 * IntegerParser parses strings to integers and vice versa
	 * 
	 * @author Mikko Hilpinen
	 * @since 5.12.2014
	 */
	public static class IntegerParser implements ObjectParser<Integer>
	{
		// IMPLEMENTED METHODS	--------------------
		
		@Override
		public String parseToString(Integer object)
		{
			return object.toString();
		}

		@Override
		public Integer parseFromString(String s) throws ObjectFormatException
		{
			try
			{
				return Integer.parseInt(s);
			}
			catch (NumberFormatException e)
			{
				throw new ObjectFormatException(s + " can't be parsed into an integer.");
			}
		}	
	}
	
	/**
	 * DoubleParser parses strings to doubles and vice versa
	 * 
	 * @author Mikko Hilpinen
	 * @since 5.12.2014
	 */
	public static class DoubleParser implements ObjectParser<Double>
	{
		// IMPLEMENTED METHODS	-----------------
		
		@Override
		public String parseToString(Double object)
		{
			return object.toString();
		}

		@Override
		public Double parseFromString(String s) throws ObjectFormatException
		{
			try
			{
				return Double.parseDouble(s);
			}
			catch (NumberFormatException e)
			{
				throw new ObjectFormatException(s + " can't be parsed into double");
			}
		}
	}
}
