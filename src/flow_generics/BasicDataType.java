package flow_generics;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * These are the basic data types already existing in java and Flow
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public enum BasicDataType implements DataType
{
	/**
	 * @see String
	 */
	STRING,
	/**
	 * Includes all numbers, including decimal numbers.
	 * @see Number
	 */
	NUMBER,
	/**
	 * @see Long
	 */
	LONG,
	/**
	 * @see Integer
	 */
	INTEGER,
	/**
	 * @see Double
	 */
	DOUBLE,
	/**
	 * @see Boolean
	 */
	BOOLEAN,
	/**
	 * @see LocalDateTime
	 */
	DATETIME,
	/**
	 * @see LocalDate
	 */
	DATE,
	/**
	 * @see ExtraBoolean
	 */
	EXTRA_BOOLEAN;
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public boolean equals(DataType other)
	{
		return equals(other);
	}

	@Override
	public String getName()
	{
		return toString();
	}
}
