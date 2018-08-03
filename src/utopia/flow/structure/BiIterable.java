package utopia.flow.structure;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * This is a simple utility interface for items that can be iterated as pairs
 * @author Mikko Hilpinen
 * @since 3.8.2018
 * @param <A> The first item type
 * @param <B> The second item type
 */
public interface BiIterable<A, B> extends RichIterable<Pair<A, B>>
{
	// OTHER	-------------------
	
	/**
	 * Performs an operation on each item
	 * @param f A function that accepts two items at once
	 */
	public default void forEach(BiConsumer<? super A, ? super B> f)
	{
		forEach(p -> f.accept(p.getFirst(), p.getSecond()));
	}
	
	/**
	 * Checks if a predicate is true for all key value pairs in this iterable
	 * @param f A predicate
	 * @return Whether the predicate is true for all key value pairs in this iterable. True if this iterable is empty.
	 */
	public default boolean forAll(BiPredicate<? super A, ? super B> f)
	{
		return forAll(p -> f.test(p.getFirst(), p.getSecond()));
	}
	
	/**
	 * Checks if this iterable contains a value pair that is accepted by the predicate
	 * @param f a predicate
	 * @return Whether this iterable contains a value pair accepted by the predicate
	 */
	public default boolean exists(BiPredicate<? super A, ? super B> f)
	{
		return exists(p -> f.test(p.getFirst(), p.getSecond()));
	}
}
