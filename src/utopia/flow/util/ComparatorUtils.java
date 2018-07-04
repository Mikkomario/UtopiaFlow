package utopia.flow.util;

/**
 * This is a static collection of methods to be used with comparables
 * @author Mikko Hilpinen
 * @since 4.7.2018
 */
public class ComparatorUtils
{
	// CONSTRUCTOR	-------------------
	
	private ComparatorUtils() { }

	
	// OHTER	-----------------------
	
	/**
	 * @param first The first value
	 * @param second The second value
	 * @return The bigger of the two values
	 */
	public static <T extends Comparable<? super T>> T max(T first, T second)
	{
		if (first.compareTo(second) < 0)
			return second;
		else
			return first;
	}
	
	/**
	 * @param first The first value
	 * @param second The second value
	 * @return The smaller of the two values
	 */
	public static <T extends Comparable<? super T>> T min(T first, T second)
	{
		if (first.compareTo(second) > 0)
			return second;
		else
			return first;
	}
}
