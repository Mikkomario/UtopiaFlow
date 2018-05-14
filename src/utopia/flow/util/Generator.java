package utopia.flow.util;

import java.util.function.Function;

import utopia.flow.structure.RichIterator;

/**
 * Generators are used for generating possibly infinite amount of values
 * @author Mikko Hilpinen
 * @since 14.5.2018
 * @param <T> The type of object generated by this generator
 */
public class Generator<T> implements RichIterator<T>
{
	// ATTRIBUTES	---------------------
	
	private Lazy<? extends T> firstItem;
	private Option<T> lastItem = Option.none();
	private Function<? super T, ? extends Option<T>> increase;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new generator
	 * @param firstItem The first item that will be generated
	 * @param increase The increment function
	 */
	public Generator(Lazy<? extends T> firstItem, Function<? super T, ? extends Option<T>> increase)
	{
		this.firstItem = firstItem;
		this.increase = increase;
	}
	
	
	// IMPLEMENTED METHODS	------------

	@Override
	public boolean hasNext()
	{
		// Will repeat infinitely
		return true;
	}

	@Override
	public T next()
	{
		T newItem = this.increase.apply(this.lastItem.getOrElse(this.firstItem::get)).getOrElse(this.firstItem::get);
		this.lastItem = Option.some(newItem);
		
		return newItem;
	}
}
