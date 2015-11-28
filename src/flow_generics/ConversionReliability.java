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
	 * from an integer to a double number would be a perfect conversion.
	 */
	PERFECT(1),
	/**
	 * The data type cast will never fail, but the value may lose some of its meaning. 
	 * A conversion from a double number to an integer would be a reliable conversion.
	 */
	RELIABLE(5),
	/**
	 * The data type cast may fail, depending from the casted value. The meaning of the 
	 * value may also change. A conversion from a String to a double would be an 
	 * unreliable conversion. Also, a conversion from a string to a boolean would be an 
	 * unreliable conversion since the meaning may be lost ("a string" -> false).
	 */
	UNRELIABLE(50);
	
	
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
