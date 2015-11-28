package flow_generics;

import java.util.Collection;

/**
 * Value creators are able to wrap objects into values. A different value creator can be 
 * used in different projects.
 * @author Mikko Hilpinen
 * @since 10.11.2015
 */
public interface ValueCreator
{
	/**
	 * This method wraps an object into a value with a data type.
	 * @param object The object that will be wrapped.
	 * @param to The data type that will be assigned to the value.
	 * @return A value with the provided object value and data type
	 * @throws DataTypeException If the wrapping failed
	 */
	public Value wrap(Object object, DataType to) throws DataTypeException;
	
	/**
	 * @return A collection containing all the data types this creator is able to use
	 */
	public Collection<? extends DataType> getSupportedDataTypes();
}
