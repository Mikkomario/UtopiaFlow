package utopia.java.flow.structure;

/**
 * GraphEdges connect two nodes together
 * @author Mikko Hilpinen
 * @since 21.11.2015
 * @param <NodeContent> The type of object stored in the node connected to the edge
 * @param <EdgeContent> The type of object stored in the edge
 */
public class GraphEdge<NodeContent, EdgeContent> implements Node<EdgeContent>
{
	// ATTRIBUTES	------------------
	
	private EdgeContent content;
	private GraphNode<NodeContent, EdgeContent> end;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new edge
	 * @param content The contents of the edge
	 * @param end The end node of the edge
	 */
	public GraphEdge(EdgeContent content, GraphNode<NodeContent, EdgeContent> end)
	{
		this.content = content;
		this.end = end;
	}
	
	/**
	 * Creates a new edge by copying another
	 * @param other The edge that is copied
	 */
	public GraphEdge(GraphEdge<NodeContent, EdgeContent> other)
	{
		this.content = other.getContent();
		this.end = other.getEndNode();
	}
	
	
	// IMPLEMENTED METHODS	----------
	
	@Override
	public EdgeContent getContent()
	{
		return this.content;
	}
	

	// ACCESSORS	------------------
	
	/**
	 * Updates the contents of this edge
	 * @param content The new content of this edge
	 */
	public void setContent(EdgeContent content)
	{
		this.content = content;
	}
	
	/**
	 * @return The node the edge points towards
	 */
	public GraphNode<NodeContent, EdgeContent> getEndNode()
	{
		return this.end;
	}
	
	/**
	 * Changes the node this edge points towards
	 * @param endNode The node this edge points towards
	 */
	public void setEndNode(GraphNode<NodeContent, EdgeContent> endNode)
	{
		this.end = endNode;
	}
}
