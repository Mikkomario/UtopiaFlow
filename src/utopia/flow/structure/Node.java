package utopia.flow.structure;

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
}
