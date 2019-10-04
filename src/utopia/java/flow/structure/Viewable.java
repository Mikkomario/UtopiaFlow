package utopia.java.flow.structure;

/**
 * A view can be formed from viewable items
 * @author Mikko Hilpinen
 * @param <A> Type of iterated item
 * @since 11.9.2019
 */
public interface Viewable<A>
{
	/**
	 * @return View of the item(s) in this structure
	 */
	public View<A> view();
}
