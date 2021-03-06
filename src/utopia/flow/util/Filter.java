package utopia.flow.util;

import java.util.ArrayList;
import java.util.Collection;

import utopia.flow.structure.Node;

/**
 * Filters are used for filtering different structures
 * @author Mikko Hilpinen
 * @since 27.4.2016
 * @param <T> The type of content filtered by this filter
 * @deprecated Please use Java 8 filters instead
 */
public interface Filter<T>
{
	// TODO: Inverted filter, multi filter and hold best algorithm
	
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
	 * Filters a node collection based on the node contents
	 * @param nodes The nodes that are filtered
	 * @param to The collection the filtered nodes are added to
	 * @param filter The filter applied to the nodes' content
	 */
	public static <ContentType, NodeType extends Node<ContentType>> void filterNodes(
			Collection<? extends NodeType> nodes, Collection<NodeType> to, 
			Filter<ContentType> filter)
	{
		for (NodeType node : nodes)
		{
			if (filter.includes(node.getContent()))
				to.add(node);
		}
	}
	
	/**
	 * Filters a node collection based on the node contents
	 * @param nodes The nodes that are filtered
	 * @param filter The filter applied to the nodes' content
	 * @return A list containing all nodes whose contents were included by the filter
	 */
	public static <ContentType, NodeType extends Node<ContentType>> ArrayList<NodeType> 
			filterNodes(Collection<? extends NodeType> nodes, Filter<ContentType> filter)
	{
		ArrayList<NodeType> filtered = new ArrayList<>();
		filterNodes(nodes, filtered, filter);
		return filtered;
	}
	
	/**
	 * Finds the first node that fulfils the provided condition
	 * @param nodes The nodes that are searched through
	 * @param filter The filter applied
	 * @return The first node which has content accepted by the filter
	 */
	public static <ContentType, NodeType extends Node<ContentType>> NodeType findFirstNode(
			Collection<? extends NodeType> nodes, Filter<ContentType> filter)
	{
		for (NodeType node : nodes)
		{
			if (filter.includes(node.getContent()))
				return node;
		}
		
		return null;
	}
}
