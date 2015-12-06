package flow_generics;

/**
 * These are the value operations introduced in the flow project. The operations can be 
 * performed on generic values.
 * @author Mikko Hilpinen
 * @since 5.12.2015
 */
public enum BasicValueOperation implements ValueOperation
{
	/**
	 * Used for combining values with each other. 2 + 2 = 4 would be a plus operation.
	 */
	PLUS,
	/**
	 * Used for separating values. 2 - 2 = 0 would be a minus operation.
	 */
	MINUS,
	/**
	 * Used for multiplying values. 2 * 2 = 4 would be a multiply operation.
	 */
	MULTIPLY,
	/**
	 * Used for dividing values. 2 / 2 = 1 would be a divide operation.
	 */
	DIVIDE;

	
	// IMPLEMENTED METHODS	-------------
	
	@Override
	public boolean equals(ValueOperation other)
	{
		return this == other;
	}
}
