package flow_generics;

import java.util.Set;

import flow_generics.ValueOperation.ValueOperationException;
import flow_structure.Pair;

/**
 * Value operators are used for performing value operations on different values
 * @author Mikko Hilpinen
 * @since 6.12.2015
 */
public interface ValueOperator
{
	/**
	 * @return The operation performed by the operator
	 */
	public ValueOperation getOperation();
	
	/**
	 * Performs the operation on the two values
	 * @param first The first value
	 * @param second The second value
	 * @return The result of the operation
	 * @throws ValueOperationException If the operation fails
	 */
	public Value operate(Value first, Value second) throws ValueOperationException;
	
	/**
	 * @return A set containing each data type combination the operator is able to operate 
	 * on. The first value in the pair represents the data type of the first parameter while 
	 * the second value represents the second parameter.
	 */
	public Set<? extends Pair<DataType, DataType>> getPossibleParameterTypes();
}
