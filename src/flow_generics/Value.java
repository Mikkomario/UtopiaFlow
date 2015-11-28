package flow_generics;

/**
 * A value is a an object value accompanied with a data type
 * @author Mikko Hilpinen
 * @since 8.11.2015
 */
public interface Value
{
	/**
	 * @return The value contained / wrapped by this object
	 */
	public Object getObjectValue();
	
	/**
	 * @return The data type of the value contained / wrapped by this object
	 */
	public DataType getType();
	
	/**
	 * @return Is the value null
	 */
	public boolean isNull();
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Checks if two values are equal. Equal values have the same data type and equal object 
	 * values
	 * @param first The first value
	 * @param second The second value
	 * @return Are the two values equal
	 */
	public static boolean valuesAreEqual(Value first, Value second)
	{
		if (!first.getType().equals(second.getType()))
			return false;
		
		if (first.isNull())
			return second.isNull();
		else if (second.isNull())
			return false;
		
		return first.getObjectValue().equals(second.getObjectValue());
	}
}
