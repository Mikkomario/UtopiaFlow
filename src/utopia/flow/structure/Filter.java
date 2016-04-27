package utopia.flow.structure;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Filters are used for filtering different structures
 * @author Mikko Hilpinen
 * @since 27.4.2016
 * @param <T> The type of content filtered by this filter
 */
public interface Filter<T>
{
	/**
	 * This method will be called for each element in a structure and the return value 
	 * determines whether the element will be included in the filtered structure
	 * @param e An element
	 * @return Should the element be included in a filtered structure
	 */
	public boolean includes(T e);
	
	
	// OTHER METHODS	----------------
	
	/**
	 * Filters a collection
	 * @param from The collection that is filtered
	 * @param to A collection the filtered elements are added to
	 * @param filter The filter that is applied to the collection
	 */
	public static <T> void filter(Collection<? extends T> from, Collection<T> to, Filter<T> filter)
	{
		for (T e : from)
		{
			if (filter.includes(e))
				to.add(e);
		}
	}
	
	/**
	 * Filters a collection
	 * @param structure A collection
	 * @param filter The filter applied to the collection
	 * @return A list containing the filtered elements
	 */
	public static <T> ArrayList<T> filter(Collection<? extends T> structure, Filter<T> filter)
	{
		ArrayList<T> to = new ArrayList<>();
		filter(structure, to, filter);
		return to;
	}
	
	/**
	 * Finds the first element in a structure that is accepted by the filter
	 * @param structure a collection
	 * @param filter The filter used for finding the element
	 * @return The first element in the collection accepted by the filter
	 */
	public static <T> T findFirst(Collection<? extends T> structure, Filter<T> filter)
	{
		for (T e : structure)
		{
			if (filter.includes(e))
				return e;
		}
		
		return null;
	}
	
	/**
	 * Filters a tree node collection based on the node contents
	 * @param nodes The nodes that are filtered
	 * @param filter The filter applied to the nodes' content
	 * @return A list containing all nodes whose contents were included by the filter
	 */
	public static <T> ArrayList<TreeNode<T>> filterTreeNodesByContent(
			Collection<? extends TreeNode<T>> nodes, Filter<T> filter)
	{
		ArrayList<TreeNode<T>> filtered = new ArrayList<>();
		for (TreeNode<T> node : nodes)
		{
			if (filter.includes(node.getContent()))
				filtered.add(node);
		}
		return filtered;
	}
}
