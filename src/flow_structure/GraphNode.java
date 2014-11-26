package flow_structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Nodes are used in graphs and they contain data. Nodes can be connected via Edges
 * 
 * @author Mikko Hilpinen
 * @param <TNode> The type of data contained within the nodes in this Graph
 * @param <TEdge> The type of data contained within the edges in this Graph
 * @since 1.5.2014
 */
public class GraphNode<TNode, TEdge>
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private TNode data;
	private String id;
	private Map<String, GraphEdge<TNode, TEdge>> leavingEdges;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new node with the given unique id and data
	 * 
	 * @param id The name of the node unique to this specific instance
	 * @param data The data held in the node
	 */
	public GraphNode(String id, TNode data)
	{
		// Initializes attributes
		this.id = id;
		this.data = data;
		this.leavingEdges = new HashMap<>();
	}

	
	// GETTERS & SETTERS	--------------------------------------------
	
	/**
	 * @return The id unique to this specific node
	 */
	public String getID()
	{
		return this.id;
	}
	
	/**
	 * @return The data held in the node
	 */
	public TNode getData()
	{
		return this.data;
	}
	
	
	// OTHER METHODS	------------------------------------------------
	
	/**
	 * Checks if the node registers the edge as leaving from this node
	 * @param edge The edge that is checked
	 * @return Does the edge leave from this node
	 */
	public boolean contains(GraphEdge<?, ?> edge)
	{
		return this.leavingEdges.containsValue(edge);
	}
	
	/**
	 * Checks if the node registers the edge as leaving from this node
	 * @param edgeID The unique ID of edge that is checked
	 * @return Does the edge leave from this node
	 */
	public boolean contains(String edgeID)
	{
		return this.leavingEdges.containsKey(edgeID);
	}
	
	/**
	 * Registers the edge as an edge leaving from this node
	 * @param edge The new edge from this node
	 */
	protected void addLeavingEdge(GraphEdge<TNode, TEdge> edge)
	{
		if (!contains(edge))
			this.leavingEdges.put(edge.getID(), edge);
	}
	
	/**
	 * Removes the edge from the edges that leave from this node
	 * @param edge The edge that will be removed from this node
	 */
	protected void removeEdge(GraphEdge<TNode, TEdge> edge)
	{
		if (contains(edge))
			this.leavingEdges.remove(edge.getID());
	}
	
	/**
	 * @return The number of edges this node uses
	 */
	public int getLeavingEdgeAmount()
	{
		return this.leavingEdges.size();
	}
	
	/**
	 * Returns a connected edge with the given identifier
	 * @param edgeID The unique identifier the edge should have
	 * @return The edge with the given id or null if no such edge exists
	 */
	public GraphEdge<TNode, TEdge> getLeavingEdge(String edgeID)
	{
		return this.leavingEdges.get(edgeID);
	}
	
	/**
	 * @return A list containing all the edges leaving from this node
	 */
	public ArrayList<GraphEdge<TNode, TEdge>> getLeavingEdges()
	{
		ArrayList<GraphEdge<TNode, TEdge>> edges = new ArrayList<>();
		edges.addAll(this.leavingEdges.values());
		return edges;
	}
	
	/**
	 * @return A list containing the IDs of all the edges leaving from this node
	 */
	public ArrayList<String> getLeavingEdgeIDs()
	{
		ArrayList<String> edgeIDs = new ArrayList<>();
		edgeIDs.addAll(this.leavingEdges.keySet());
		return edgeIDs;
	}
	
	/**
	 * @return All nodes that this node has edges to
	 */
	public ArrayList<GraphNode<TNode, TEdge>> getEndNodes()
	{
		ArrayList<GraphNode<TNode, TEdge>> nodes = new ArrayList<>();
		for (GraphEdge<TNode, TEdge> edge : getLeavingEdges())
		{
			if (edge.getEndNode() == this)
				nodes.add(edge.getStartNode());
			else
				nodes.add(edge.getEndNode());
		}
		
		return nodes;
	}
}
