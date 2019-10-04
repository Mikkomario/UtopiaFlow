package utopia.java.flow.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	private Set<GraphEdge<NodeContent, EdgeContent>> edges;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new node
	 * @param content The contents of the node
	 */
	public GraphNode(NodeContent content)
	{
		this.content = content;
		this.edges = new HashSet<>();
	}
	
	/**
	 * Creates a new node by copying another
	 * @param other The other node
	 */
	public GraphNode(GraphNode<NodeContent, EdgeContent> other)
	{
		this.content = other.getContent();
		this.edges = new HashSet<>();
		
		for(GraphEdge<NodeContent, EdgeContent> edge : other.getLeavingEdges())
		{
			this.edges.add(new GraphEdge<>(edge));
		}
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
	public Set<GraphEdge<NodeContent, EdgeContent>> getLeavingEdges()
	{
		return new HashSet<>(this.edges);
	}
	
	/**
	 * Adds a new (leaving) edge to the node
	 * @param edge The edge that starts from this node
	 */
	public void addEdge(GraphEdge<NodeContent, EdgeContent> edge)
	{
		if (edge != null)
			this.edges.add(edge);
	}
	
	/**
	 * Removes an existing edge from the node
	 * @param edge The edge that will be removed from the node
	 */
	public void removeEdge(GraphEdge<?, ?> edge)
	{
		if (edge != null)
			this.edges.remove(edge);
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
	public Set<GraphNode<NodeContent, EdgeContent>> getEndNodes()
	{
		Set<GraphNode<NodeContent, EdgeContent>> nodes = new HashSet<>();
		
		for (GraphEdge<NodeContent, EdgeContent> edge : getLeavingEdges())
		{
			if (edge.getEndNode() != null)
				nodes.add(edge.getEndNode());
		}
		
		return nodes;
	}
	
	/**
	 * Finds the edge that leaves from this node and points to the provided node.
	 * @param endNode The other node the edge is connected to
	 * @return An edge that leaves from this node and points towards the other. Null if no 
	 * such edge exists.
	 */
	public GraphEdge<NodeContent, EdgeContent> getConnectingEdge(GraphNode<?, ?> endNode)
	{
		for (GraphEdge<NodeContent, EdgeContent> edge : getLeavingEdges())
		{
			if (edge.getEndNode() != null && edge.getEndNode().equals(endNode))
				return edge;
		}
		
		return null;
	}
	
	/**
	 * Checks whether there is an edge leaving from this node that connects it to the 
	 * provided node
	 * @param endNode The node this one may be connected to
	 * @return Is there an edge leaving from this node, connecting the two nodes
	 */
	public boolean hasEdgeTowards(GraphNode<?, ?> endNode)
	{
		if (getConnectingEdge(endNode) == null)
			return false;
		else
			return true;
	}
	
	/**
	 * Finds all the edges leaving from this node that have the provided content
	 * @param content The content of the edges
	 * @return The edges leaving from this node that have the provided content
	 */
	public List<GraphEdge<NodeContent, EdgeContent>> findEdges(EdgeContent content)
	{
		List<GraphEdge<NodeContent, EdgeContent>> edges = new ArrayList<>();
		for (GraphEdge<NodeContent, EdgeContent> edge : getLeavingEdges())
		{
			if (edge.getContent() == null)
			{
				if (content == null)
					edges.add(edge);
			}
			else if (content != null && edge.getContent().equals(content))
				edges.add(edge);
		}
		
		return edges;
	}
	
	/**
	 * Finds all the routes that connect this node to another node. The routes may visit a 
	 * node only once, so they won't contain any loops.
	 * @param end Another node
	 * @return The routes leading to the other node. Null if the two nodes aren't connected. 
	 * An empty list if this is the target node.
	 */
	public List<LinkedList<GraphEdge<NodeContent, EdgeContent>>> findConnectingRoutes(
			GraphNode<NodeContent, EdgeContent> end)
	{
		List<GraphNode<NodeContent, EdgeContent>> pastNodes = new ArrayList<>();
		pastNodes.add(this);
		
		return findConnectingRoutes(end, pastNodes);
	}
	
	private List<LinkedList<GraphEdge<NodeContent, EdgeContent>>> findConnectingRoutes(
			GraphNode<NodeContent, EdgeContent> end, List<? extends GraphNode<NodeContent, 
			EdgeContent>> pastNodes)
	{
		// Collects all the found routes to a list
		List<LinkedList<GraphEdge<NodeContent, EdgeContent>>> routes = new ArrayList<>();
		
		if (this.equals(end))
			return routes;
		
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
					List<LinkedList<GraphEdge<NodeContent, EdgeContent>>> foundRoutes = 
							edge.getEndNode().findConnectingRoutes(end, newPastNodes);
					if (foundRoutes != null)
					{
						// Creates a new route for each route that lead to the target node. 
						// The route includes the used edge
						for (LinkedList<GraphEdge<NodeContent, EdgeContent>> route : foundRoutes)
						{
							route.addFirst(edge);
							routes.add(route);
						}
					}
				}
			}
		}
		
		if (routes.isEmpty())
			return null;
		else
			return routes;
	}
}
