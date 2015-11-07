package flow_util;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * These are the basic data types already existing in java and Flow
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public enum BasicDataTypes implements DataType
{
	/**
	 * @see String
	 */
	STRING(String.class),
	/**
	 * Includes all numbers, including decimal numbers.
	 * @see Number
	 */
	NUMBER(Number.class),
	/**
	 * @see Long
	 */
	LONG(Long.class),
	/**
	 * @see Integer
	 */
	INTEGER(Integer.class),
	/**
	 * @see Double
	 */
	DOUBLE(Double.class),
	/**
	 * @see Boolean
	 */
	BOOLEAN(Boolean.class),
	/**
	 * DateTime is considered to be hierarchically under date since it contains the same 
	 * information
	 * @see LocalDateTime
	 */
	DATETIME(LocalDateTime.class),
	/**
	 * @see LocalDate
	 */
	DATE(LocalDate.class),
	/**
	 * Extra boolean is hierarchically under boolean since it contains the same information
	 * @see ExtraBoolean
	 */
	EXTRA_BOOLEAN(ExtraBoolean.class);
	
	
	// ATTRIBUTES	---------------------
	
	private final Class<?> valueClass;
	
	
	// CONSTRUCTOR	--------------------
	
	private BasicDataTypes(Class<?> valueClass)
	{
		this.valueClass = valueClass;
	}
	
	
	// IMPLEMENTED METHODS	-------------

	@Override
	public boolean isSameTypeAs(DataType other)
	{
		return equals(other);
	}

	@Override
	public String getName()
	{
		return toString();
	}

	@Override
	public Class<?> getValueClass()
	{
		return this.valueClass;
	}
}
