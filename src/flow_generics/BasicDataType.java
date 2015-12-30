package flow_generics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
	 * @see LocalTime
	 */
	TIME,
	/**
	 * @see ExtraBoolean
	 */
	EXTRA_BOOLEAN,
	/**
	 * @see Variable
	 */
	VARIABLE,
	/**
	 * @See Model
	 */
	MODEL,
	/**
	 * @see VariableDeclaration
	 */
	VARIABLE_DECLARATION,
	/**
	 * @see ModelDeclaration
	 */
	MODEL_DECLARATION;
	
	// TODO: Implement time and list types
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public boolean equals(DataType other)
	{
		return this == other;
	}

	@Override
	public String getName()
	{
		return toString();
	}
}
