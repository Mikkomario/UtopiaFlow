package flow_generics;

/**
 * These are the different levels of reliability a data type conversion process can have
 * @author Mikko Hilpinen
 * @since 27.11.2015
 */
public enum ConversionReliability
{
	/**
	 * The data type cast will never fail and preserves the value so that when the value 
	 * is cast back to the original data type, the value would stay equal. A conversion 
	 * from an integer to a double number would be a perfect conversion (1 -> 1.0).
	 */
	PERFECT(1),
	/**
	 * The data type cast will never fail, but the value may lose some of its data. The 
	 * remaining data preserves its meaning and will work properly, however.
	 * A conversion from a double number to an integer would be a reliable conversion 
	 * (1.23 -> 1).
	 */
	DATA_LOSS(7),
	/**
	 * The data type cast will never fail, but the meaning of the data may be lost. A conversion 
	 * from a String representing an integer 2015 to boolean would be this kind of conversion 
	 * ("2015" -> false)
	 */
	MEANING_LOSS(25),
	/**
	 * The data type cast may fail, depending from the casted value. The meaning of the 
	 * value may also be lost. A conversion from a String to a double would be this kind 
	 * of conversion conversion ("aaa" -> ).
	 */
	DANGEROUS(30);
	
	
	// ATTRIBUTES	-----------------
	
	private final int cost;
	
	
	// CONSTRUCTOR	-----------------
	
	private ConversionReliability(int cost)
	{
		this.cost = cost;
	}
	
	
	// ACCESSORS	-----------------
	
	int getCost()
	{
		return this.cost;
	}
	
	
	// OTHER METHODS	-------------
	
	/**
	 * This method compares this reliability with another
	 * @param other Another level of reliability
	 * @return Is this reliability higher than the other
	 */
	public boolean isBetterThan(ConversionReliability other)
	{
		if (other == null)
			return true;
		
		return this.cost < other.cost;
	}
}
