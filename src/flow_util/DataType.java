package flow_util;

/**
 * All data type classes / enumerations should implement this interface so that they can be 
 * used with generalised values.
 * @author Mikko Hilpinen
 * @since 25.10.2015
 */
public interface DataType
{
	/**
	 * Is this data type the same as the other
	 * @param other The other data type
	 * @return Are these data types the same
	 */
	public boolean isSameTypeAs(DataType other);
	
	/**
	 * @return The name of the data type
	 */
	public String getName();
	
	/**
	 * @return The class that instantiates the values of this data type
	 */
	public Class<?> getValueClass();
	// TODO: Remove this?
	
	/**
	 * Does the data type belong in another data type. For example, an integer is also a 
	 * numeric, but a numeric is not necessarily an integer.
	 * @param other The other data type
	 * @return Does this data type belong to the other data type
	 */
	//public boolean isOfType(DataType other);
}
