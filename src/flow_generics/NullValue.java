package flow_generics;

/**
 * This value only has a data type and is cosidered null.
 * @author Mikko Hilpinen
 * @since 10.11.2015
 */
public class NullValue implements Value
{
	// ATTRIBUTES	-----------------
	
	private DataType dataType;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new null value
	 * @param dataType The data type the value represents
	 */
	public NullValue(DataType dataType)
	{
		this.dataType = dataType;
	}
	
	
	// IMPLEMENTED METHODS	---------

	@Override
	public Object getObjectValue()
	{
		return null;
	}

	@Override
	public DataType getType()
	{
		return this.dataType;
	}

	@Override
	public boolean isNull()
	{
		return true;
	}
}
