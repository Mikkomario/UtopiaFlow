package flow_generics;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This creator is able to wrap objects into basic values
 * @author Mikko Hilpinen
 * @since 10.11.2015
 */
public class BasicValueCreator implements ValueCreator
{
	// ATTRIBUTES	----------------
	
	private Collection<DataType> supportedTypes;
	
	
	// CONSTRUCTOR	----------------
	
	/**
	 * Creates a new value creator
	 */
	public BasicValueCreator()
	{
		this.supportedTypes = new ArrayList<>();
		for (DataType type : BasicDataType.values())
		{
			this.supportedTypes.add(type);
		}
	}
	
	
	// IMPLEMENTED METHODS	-------
	
	@Override
	public Value wrap(Object object, DataType to) throws DataTypeException
	{
		return new BasicValue(object, to);
	}

	@Override
	public Collection<? extends DataType> getSupportedDataTypes()
	{
		return this.supportedTypes;
	}
}
