package flow_util;

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
}
