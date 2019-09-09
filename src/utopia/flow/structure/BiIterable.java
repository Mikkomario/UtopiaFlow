package utopia.flow.structure;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

import utopia.flow.function.ThrowingBiFunction;

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
	
	/**
	 * Finds a pair from this collection
	 * @param f The function that tests pairs
	 * @return the first matching pair in this colelction or none if there were no matches
	 */
	public default Option<Pair<A, B>> find(BiPredicate<? super A, ? super B> f)
	{
		return find(p -> f.test(p.getFirst(), p.getSecond()));
	}
	
	/**
	 * Maps each item pair in this iterable
	 * @param f A mapping function
	 * @param makeBuilder A function for creating builder for final collection
	 * @return Mapped collection
	 */
	public default <R, C> R map(BiFunction<? super A, ? super B, ? extends C> f, 
			Function<? super Option<Integer>, ? extends Builder<? extends R, ?, ? super C>> makeBuilder)
	{
		return map(pair -> f.apply(pair.getFirst(), pair.getSecond()), makeBuilder);
	}
	
	/**
	 * Maps each item pair in this iterable into possibly multiple values
	 * @param f A mapping function. May return multiple values.
	 * @param makeBuilder A function for creating a builder for final collection
	 * @return Mapped collection
	 */
	public default <R, C> R flatMap(BiFunction<? super A, ? super B, ? extends RichIterable<? extends C>> f, 
			Supplier<? extends Builder<? extends R, ?, ? super C>> makeBuilder)
	{
		return flatMap(pair -> f.apply(pair.getFirst(), pair.getSecond()), makeBuilder);
	}
	
	/**
	 * Maps each item pair in this iterable. May throw.
	 * @param f A mapping function
	 * @param makeBuilder A function for creating builder for final collection
	 * @return Mapped collection
	 * @throws E If mapping failed
	 */
	public default <R, C, E extends Exception> R mapThrowing(
			ThrowingBiFunction<? super A, ? super B, ? extends C, ? extends E> f, 
			Function<? super Option<Integer>, ? extends Builder<? extends R, ?, ? super C>> makeBuilder) throws E
	{
		return mapThrowing(pair -> f.throwingApply(pair.getFirst(), pair.getSecond()), makeBuilder);
	}
	
	/**
	 * Maps each item pair in this iterable into possibly multiple values. May throw.
	 * @param f A mapping function. May return multiple values.
	 * @param makeBuilder A function for creating a builder for final collection
	 * @return Mapped collection
	 * @throws E If mapping failed
	 */
	public default <R, C, E extends Exception> R flatMapThrowing(
			ThrowingBiFunction<? super A, ? super B, ? extends RichIterable<? extends C>, ? extends E> f, 
			Supplier<? extends Builder<? extends R, ?, ? super C>> makeBuilder) throws E
	{
		return flatMapThrowing(pair -> f.throwingApply(pair.getFirst(), pair.getSecond()), makeBuilder);
	}
	
	/**
	 * Maps the pairs in this iterable into list format
	 * @param f A mapping function
	 * @return A list of mapped items
	 */
	public default <C> ImmutableList<C> mapToList(BiFunction<? super A, ? super B, ? extends C> f)
	{
		return map(f, ListBuilder::new);
		// return toList().map(p -> f.apply(p.getFirst(), p.getSecond()));
	}
	
	/**
	 * Maps the pairs in this collection into a one level deep list format
	 * @param f A mapping function
	 * @return A one level deep list of mapped items
	 */
	public default <C> ImmutableList<C> flatMapToList(BiFunction<? super A, ? super B, 
			? extends RichIterable<? extends C>> f)
	{
		return flatMap(f, ListBuilder::new);
		// return toList().flatMap(p -> f.apply(p.getFirst(), p.getSecond()));
	}
}
