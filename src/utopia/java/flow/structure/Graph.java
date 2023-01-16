package utopia.java.flow.structure;

/**
 * Graphs are constructed from multiple nodes, linked together with edges
 * @author Mikko Hilpinen
 * @param <NodeContent> The type of content stored in the nodes
 * @param <EdgeContent> The type of content stored in the edges
 * @since 22.11.2015
 */
public class Graph<NodeContent, EdgeContent>
{
	// ATTRIBUTES	----------------------
	
	private ImmutableList<GraphNode<NodeContent, EdgeContent>> nodes;
	
	
	// CONSTRUCTOR	----------------------
	
	/**
	 * Creates a new empty graph
	 */
	public Graph()
	{
		this.nodes = ImmutableList.empty();
	}
	
	
	// ACCESSORS	-----------------------
	
	/**
	 * @return The nodes currently stored in this graph.
	 */
	public ImmutableList<GraphNode<NodeContent, EdgeContent>> getNodes()
	{
		return this.nodes;
	}
	
	
	// OTHER METHODS	-------------------
	
	/**
	 * @return All the edges from each node in this graph
	 */
	public ImmutableList<GraphEdge<NodeContent, EdgeContent>> getEdges()
	{
		return nodes.flatMap(GraphNode::getLeavingEdges);
	}
	
	/**
	 * Adds a new node to the graph
	 * @param node The node that will be added to the graph
	 */
	public void addNode(GraphNode<NodeContent, EdgeContent> node) {
		if (node != null && !nodes.contains(node))
			nodes = nodes.plus(node);
	}
	
	/**
	 * Finds the nodes in this graph that have edges pointing towards the provided node.
	 * @param endNode The node the edges point towards
	 * @return The nodes the connecting edges leave from
	 */
	public ImmutableList<GraphNode<NodeContent, EdgeContent>> findLeadingNodes(GraphNode<?, ?> endNode) {
		return nodes.filter(n -> n.hasEdgeTowards(endNode));
	}
	
	/**
	 * Finds all the nodes in this graph that are connected to the provided node. That includes 
	 * all nodes the node has edges towards.
	 * @param node A node
	 * @return Each node in this graph connected to the provided node.
	 */
	public ImmutableList<GraphNode<NodeContent, EdgeContent>> findConnectedNodes(GraphNode<NodeContent,
			EdgeContent> node)
	{
		return node.getEndNodes().plus(findLeadingNodes(node));
	}
	
	/**
	 * Finds all the nodes in this graph that have content equal to the one provided
	 * @param content The content of the searched nodes
	 * @return All nodes in this graph that have the provided content
	 */
	public ImmutableList<GraphNode<NodeContent, EdgeContent>> findNodes(NodeContent content)
	{
		return nodes.filter(n -> n.getContent().equals(content));
	}
	
	/**
	 * Finds all edges in this graph that have content equal to the one provided
	 * @param content The content of the searched edges
	 * @return All edges which have the provided content
	 */
	public ImmutableList<GraphEdge<NodeContent, EdgeContent>> findEdges(EdgeContent content)
	{
		return nodes.flatMap(n -> n.findEdges(content));
	}
}
