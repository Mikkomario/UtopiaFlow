package utopia.java.flow.structure.range;

import utopia.java.flow.structure.View;
import utopia.java.flow.structure.Viewable;

/**
 * A range that only contains a single item
 * @author Mikko Hilpinen
 * @since 11.9.2019
 * @param <A> Type of item in this range
 */
public class SingleItemRange<A extends Comparable<? super A>> implements DefinedRange<A>, Viewable<A>
{
	// ATTRIBUTES	------------------
	
	private A item;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * @param item The only item in this range
	 */
	public SingleItemRange(A item)
	{
		this.item = item;
	}
	
	
	// IMPLEMENTED	-----------------
	
	@Override
	public A first()
	{
		return item;
	}

	@Override
	public A last()
	{
		return item;
	}
	
	@Override
	public View<A> view()
	{
		return View.wrap(item);
	}
	
	@Override
	public String toString()
	{
		return item.toString();
	}
}
