package utopia.java.flow.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Nodes contain data
 * @author Mikko Hilpinen
 * @since 27.4.2016
 * @param <T> The type of data contained within the nodes
 */
public interface Node<T>
{
	/**
	 * @return The data contained within this node
	 */
	public T getContent();
	
	/**
	 * Fetches the content of multiple nodes
	 * @param nodes a collection of nodes
	 * @return The content stored in the nodes
	 */
	public static <T> List<T> getNodeContent(Collection<? extends Node<T>> nodes)
	{
		List<T> content = new ArrayList<>();
		for (Node<T> node : nodes)
		{
			content.add(node.getContent());
		}
		
		return content;
	}
}
