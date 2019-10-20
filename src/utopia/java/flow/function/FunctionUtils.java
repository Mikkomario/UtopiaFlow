package utopia.java.flow.function;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a static collection of some utility functions
 * @author Mikko Hilpinen
 * @since 21.6.2018
 */
public class FunctionUtils
{
	// ATTRIBUTES	-------------------------
	
	/**
	 * A function for converting objects to strings
	 */
	public static final Function<Object, String> TO_STRING = t -> t.toString();
	/**
	 * A consumer for ignoring a value
	 */
	public static final Consumer<Object> IGNORE = t -> {};
	/**
	 * A runnable that does nothing
	 */
	public static final Runnable NO_OPERATION = () -> {};
	
	/**
	 * This function is used for checking equality between two objects in a null safe manner
	 */
	public static final BiPredicate<Object, Object> SAFE_EQUALS = (a, b) -> a == null ? b == null : a.equals(b);
	
	
	// CONSTRUCTOR	-------------------------
	
	private FunctionUtils() { }
	
	
	// OTHER	-----------------------------
	
	/**
	 * @return A function for preserving an instance
	 */
	public static <T> Function<T, T> identity()
	{
		return t -> t;
	}
	
	/**
	 * @param item The item that will be always supplied
	 * @return A function that will always supply the provided value
	 */
	public static <T> Supplier<T> supply(T item)
	{
		return () -> item;
	}
}
