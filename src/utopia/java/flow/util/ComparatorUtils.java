package utopia.java.flow.util;

import utopia.java.flow.structure.Option;

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
	
	/**
	 * @param first first optional value
	 * @param second Second optional value
	 * @return Minimum between these values. None if no values were provided.
	 */
	public static <A extends Comparable<? super A>> Option<A> min(Option<A> first, Option<A> second)
	{
		if (first.isEmpty())
			return second;
		else if (second.isEmpty())
			return first;
		else
			return Option.some(min(first.get(), second.get()));
	}
	
	/**
	 * @param first First optional value
	 * @param second Second optional value
	 * @return Maximum between these values. None if no values were provided.
	 */
	public static <A extends Comparable<? super A>> Option<A> max(Option<A> first, Option<A> second)
	{
		if (first.isEmpty())
			return second;
		else if (second.isEmpty())
			return first;
		else
			return Option.some(max(first.get(), second.get()));
	}
}
