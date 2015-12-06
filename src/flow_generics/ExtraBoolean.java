package flow_generics;

/**
 * This simple piece of data resembles Boolean, but has more states
 * @author Mikko Hilpinen
 * @since 
 */
public enum ExtraBoolean
{
	/**
	 * Used when something is completely and very much true
	 */
	EXTRA_TRUE(1.0),
	/**
	 * Used when something is almost false, but still true
	 */
	WEAK_TRUE(0.6),
	/**
	 * Used when something is almost true, but still false
	 */
	WEAK_FALSE(0.3),
	/**
	 * Used when something is completely and very much false
	 */
	EXTRA_FALSE(0.0);
	
	
	// ATTRIBUTES	---------------------
	
	private final double value;
	
	
	// CONSTRUCTOR	---------------------
	
	private ExtraBoolean(double value)
	{
		this.value = value;
	}
	
	
	// ACCESSORS	---------------------
	
	/**
	 * @return The double value of this extra boolean
	 */
	public double toDouble()
	{
		return this.value;
	}

	
	// OTHER METHODS	-----------------
	
	/**
	 * @return The boolean value of this extra boolean [0, 1]
	 */
	public boolean toBoolean()
	{
		return this.value >= 0.5;
	}
	
	/**
	 * @return An integer value representing this boolean [0, 1]
	 */
	public int toInteger()
	{
		if (toBoolean())
			return 1;
		else
			return 0;
	}
	
	/**
	 * Checks whether the value is the same or more true than the provided value
	 * @param other The value this one is compared to
	 * @return Is this value at least as true as the other
	 */
	public boolean isAtLeastAsTrueAs(ExtraBoolean other)
	{
		return this.value >= other.value;
	}
	
	/**
	 * Combines the two extra booleans with logical and. The result is EXTRA_TRUE only if 
	 * both of the values are EXTRA_TRUE.
	 * @param other Another extra boolean
	 * @return The one of the extra booleans that is more false.
	 */
	public ExtraBoolean and(ExtraBoolean other)
	{
		if (other == null)
			return this;
		
		if (!isAtLeastAsTrueAs(other))
			return this;
		else
			return other;
	}
	
	/**
	 * Combines the two extra booleans with logical or. The result is EXTRA_TRUE if either 
	 * of the values is EXTRA_TRUE.
	 * @param other Another extra boolean.
	 * @return The one of the extra booleans that is more true.
	 */
	public ExtraBoolean or(ExtraBoolean other)
	{
		if (other == null)
			return this;
		
		if (isAtLeastAsTrueAs(other))
			return this;
		else
			return other;
	}
	
	/**
	 * Checks if the two extra booleans have the same value
	 * @param other The other extra boolean value
	 * @return Extra true if the values are exactly equal, weak true if only the boolean 
	 * values are equal, extra false if the values are opposite (extra true vs. extra false), 
	 * otherwise weak false.
	 */
	public ExtraBoolean equals(ExtraBoolean other)
	{
		if (this == other)
			return EXTRA_TRUE;
		else if (toBoolean() == other.toBoolean())
			return WEAK_TRUE;
		else if (Math.abs(this.value - other.value) < 0.5)
			return WEAK_FALSE;
		else
			return EXTRA_FALSE;
	}
	
	/**
	 * Parses an extra boolean value from a string. In addition to enum values, "true" and 
	 * "false" are recognised
	 * @param s A string representing an (extra) boolean
	 * @return The extra boolean represented by the string
	 */
	public static ExtraBoolean parseFromString(String s)
	{
		if (s == null)
			return null;
		
		if ("true".equalsIgnoreCase(s))
			return EXTRA_TRUE;
		else if ("false".equalsIgnoreCase(s))
			return EXTRA_FALSE;
		
		for (ExtraBoolean b : values())
		{
			if (b.toString().equalsIgnoreCase(s))
				return b;
		}
		
		return null;
	}
	
	/**
	 * Parses an extra boolean from a double number. <= 0 is false, >= 1 is true. The values 
	 * in between may become weak false or weak true.
	 * @param d A double number
	 * @return An extra boolean based on the double number
	 */
	public static ExtraBoolean parseFromDouble(double d)
	{
		if (d >= EXTRA_TRUE.value)
			return EXTRA_TRUE;
		else if (d >= WEAK_TRUE.value)
			return WEAK_TRUE;
		else if (d >= WEAK_FALSE.value)
			return WEAK_FALSE;
		else
			return EXTRA_FALSE;
	}
	
	/**
	 * Parses a boolean value into an extra boolean
	 * @param b The boolean value
	 * @return An extra boolean value
	 */
	public static ExtraBoolean parseFromBoolean(boolean b)
	{
		if (b)
			return EXTRA_TRUE;
		else
			return EXTRA_FALSE;
	}
}
