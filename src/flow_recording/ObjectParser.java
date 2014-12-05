package flow_recording;

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
}
