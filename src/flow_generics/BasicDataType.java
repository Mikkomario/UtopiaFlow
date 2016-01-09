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
	 * Object is the super type for (at least) all the basic data types
	 */
	OBJECT,
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
	MODEL_DECLARATION,
	/**
	 * @see ValueList
	 */
	LIST;
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public String getName()
	{
		return toString();
	}
}
