package utopia.java.flow.structure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Graph nodes can be stored in graphs and connected via edges. Each node contains content 
 * of some description.
 * @author Mikko Hilpinen
 * @since 21.11.2015
 * @param <NodeContent> The type of content stored in this node
 * @param <EdgeContent> The type of content stored in the edges leaving from this node
 */
public class GraphNode<NodeContent, EdgeContent> implements Node<NodeContent>
{
	// ATTRIBUTES	-------------------
	
	private NodeContent content;
	private ImmutableList<GraphEdge<NodeContent, EdgeContent>> edges;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new node
	 * @param content The contents of the node
	 */
	public GraphNode(NodeContent content)
	{
		this.content = content;
		this.edges = ImmutableList.empty();
	}
	
	/**
	 * Creates a new node by copying another
	 * @param other The other node
	 */
	public GraphNode(GraphNode<NodeContent, EdgeContent> other)
	{
		this.content = other.getContent();
		this.edges = other.edges;
	}
	
	
	// IMPLEMENTED METHODS	----------

	@Override
	public NodeContent getContent()
	{
		return this.content;
	}
	
	
	// ACCESSORS	------------------
	
	/**
	 * Updates the node's contents
	 * @param content The node's new contents
	 */
	public void setContent(NodeContent content)
	{
		this.content = content;
	}
	
	/**
	 * @return The edges connected to this node. The returned set is a copy and changes made 
	 * to it won't affect the original.
	 */
	public ImmutableList<GraphEdge<NodeContent, EdgeContent>> getLeavingEdges()
	{
		return edges;
	}
	
	/**
	 * Adds a new (leaving) edge to the node
	 * @param edge The edge that starts from this node
	 */
	public void addEdge(GraphEdge<NodeContent, EdgeContent> edge)
	{
		if (edge != null && !edges.contains(edge))
			edges = edges.plus(edge);
	}
	
	/**
	 * Removes an existing edge from the node
	 * @param edge The edge that will be removed from the node
	 */
	public void removeEdge(GraphEdge<?, ?> edge)
	{
		if (edge != null)
			edges = edges.filter(e -> !e.equals(edge));
	}
	
	
	// OTHER METHODS	-----------------
	
	/**
	 * Checks whether the node contains the provided edge
	 * @param edge The edge that could leave from this node
	 * @return Does such an edge leave from this node
	 */
	public boolean containsEdge(GraphEdge<?, ?> edge)
	{
		return this.edges.contains(edge);
	}
	
	/**
	 * @return A set that contains each node this node has edges pointing towards
	 */
	public ImmutableList<GraphNode<NodeContent, EdgeContent>> getEndNodes()
	{
		return edges.map(GraphEdge::getEndNode).distinct();
	}
	
	/**
	 * Finds the edge that leaves from this node and points to the provided node.
	 * @param endNode The other node the edge is connected to
	 * @return An edge that leaves from this node and points towards the other.
	 * None if no such edge exists.
	 */
	public Option<GraphEdge<NodeContent, EdgeContent>> getConnectingEdge(GraphNode<?, ?> endNode)
	{
		return edges.find(e -> e.getEndNode().equals(endNode));
	}
	
	/**
	 * Checks whether there is an edge leaving from this node that connects it to the 
	 * provided node
	 * @param endNode The node this one may be connected to
	 * @return Is there an edge leaving from this node, connecting the two nodes
	 */
	public boolean hasEdgeTowards(GraphNode<?, ?> endNode)
	{
		return edges.exists(e -> e.getEndNode().equals(endNode));
	}
	
	/**
	 * Finds all the edges leaving from this node that have the provided content
	 * @param content The content of the edges
	 * @return The edges leaving from this node that have the provided content
	 */
	public ImmutableList<GraphEdge<NodeContent, EdgeContent>> findEdges(EdgeContent content)
	{
		return edges.filter(e -> e.getContent().equals(content));
	}
	
	/**
	 * Finds all the routes that connect this node to another node. The routes may visit a 
	 * node only once, so they won't contain any loops.
	 * @param end Another node
	 * @return The routes leading to the other node. None if the two nodes aren't connected.
	 * An empty list if this is the target node.
	 */
	public Option<ImmutableList<ImmutableList<GraphEdge<NodeContent, EdgeContent>>>> findConnectingRoutes(
			GraphNode<NodeContent, EdgeContent> end)
	{
		List<GraphNode<NodeContent, EdgeContent>> pastNodes = new ArrayList<>();
		pastNodes.add(this);
		
		return findConnectingRoutes(end, pastNodes).map(routes -> ImmutableList.of(routes).map(ImmutableList::of));
	}
	private Option<List<LinkedList<GraphEdge<NodeContent, EdgeContent>>>> findConnectingRoutes(
			GraphNode<NodeContent, EdgeContent> end,
			List<? extends GraphNode<NodeContent, EdgeContent>> pastNodes)
	{
		// Collects all the found routes to a list
		List<LinkedList<GraphEdge<NodeContent, EdgeContent>>> routes = new ArrayList<>();
		
		if (this.equals(end))
			return Option.some(routes);
		
		// Keeps track of the nodes that have already been visited, this included
		List<GraphNode<NodeContent, EdgeContent>> newPastNodes = new ArrayList<>(pastNodes);
		newPastNodes.add(this);
		
		// Each edge may contain a new route to the target node
		for (GraphEdge<NodeContent, EdgeContent> edge : getLeavingEdges())
		{
			// A node can only be visited once
			if (!pastNodes.contains(edge.getEndNode()))
			{
				// If a direct route is found, uses that
				if (end.equals(edge.getEndNode()))
				{
					LinkedList<GraphEdge<NodeContent, EdgeContent>> route = new LinkedList<>();
					route.add(edge);
					routes.add(route);
				}
				// Otherwise tries to find an indirect route
				else
				{
					// The routes are searched recirsively, but nodes will be visited only once
					Option<List<LinkedList<GraphEdge<NodeContent, EdgeContent>>>> foundRoutes =
							edge.getEndNode().findConnectingRoutes(end, newPastNodes);
					foundRoutes.forEach(fRoutes -> {
						// Creates a new route for each route that lead to the target node.
						// The route includes the used edge
						for (LinkedList<GraphEdge<NodeContent, EdgeContent>> route : fRoutes)
						{
							route.addFirst(edge);
							routes.add(route);
						}
					});
				}
			}
		}
		
		if (routes.isEmpty())
			return Option.none();
		else
			return Option.some(routes);
	}
}
