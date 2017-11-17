package utopia.flow.generics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import utopia.flow.util.ExtraBoolean;

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
	 * @see Float
	 */
	FLOAT,
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
	 * The variables represented by this data type are so called basic variables represented 
	 * by the base Variable class.
	 * @see Variable
	 */
	VARIABLE,
	/**
	 * The models represented by this data type are so called basic models. They use the 
	 * Variable class.
	 * @See {@link Model}
	 */
	MODEL,
	/**
	 * @see VariableDeclaration
	 */
	VARIABLE_DECLARATION,
	/**
	 * The model declarations are considered to be simple instances of 
	 * ModelDeclaration<VariableDeclaration>
	 * @see ModelDeclaration
	 */
	MODEL_DECLARATION,
	/**
	 * @see ValueList
	 * @deprecated Replaced with {@link #IMMUTABLE_LIST}
	 */
	LIST, 
	/**
	 * Specifically instances of ImmutableList<Value>. Other types are not allowed
	 */
	IMMUTABLE_LIST;
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public String getName()
	{
		return toString();
	}
}
