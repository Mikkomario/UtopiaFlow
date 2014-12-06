package flow_structure;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Edges are used in graphs to connect nodes together. They may also contain 
 * data. Edges may be one way or two ways.
 * 
 * @author Mikko Hilpinen
 * @param <TNode> The type of data contained within the nodes of the Graph
 * @param <TEdge> The type of data contained within the edges of the Graph
 * @since 1.5.2014
 */
public class GraphEdge<TNode, TEdge>
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private GraphNode<TNode, TEdge> start, end;
	private String id;
	private TEdge data;
	private boolean twoWays;

	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new edge with the given data. The edge is connected to the 
	 * given nodes.
	 * 
	 * @param start The node the edge starts from
	 * @param end The node the edge ends to
	 * @param id The id that describes this edge. The id should be unique in a 
	 * group of edges connected to a single node
	 * @param data The data stored in the edge
	 * @param bothWays Should the edge connect the nodes both ways.
	 */
	protected GraphEdge(GraphNode<TNode, TEdge> start, GraphNode<TNode, TEdge> end, String id, 
			TEdge data, boolean bothWays)
	{
		// Initializes attributes
		this.start = start;
		this.end = end;
		this.data = data;
		this.twoWays = bothWays;
		this.id = id;
		
		// Adds the edge to the nodes
		this.start.addLeavingEdge(this);
		if (this.twoWays)
			this.end.addLeavingEdge(this);
	}

	
	// GETTERS & SETTERS	---------------------------------------------
	
	/**
	 * @return The data the edge contains
	 */
	public TEdge getData()
	{
		return this.data;
	}
	
	/**
	 * Changes the data held in this edge
	 * @param data The data held in this edge
	 */
	public void setData(TEdge data)
	{
		this.data = data;
	}
	
	/**
	 * @return The node the edge starts from. If the edge connects the nodes both ways, the 
	 * start node could be either one.
	 */
	public GraphNode<TNode, TEdge> getStartNode()
	{
		return this.start;
	}
	
	/**
	 * @return The node the edge ends to. If the edge connects the nodes both ways, the 
	 * end node could be either one.
	 */
	public GraphNode<TNode, TEdge> getEndNode()
	{
		return this.end;
	}
	
	/**
	 * @return Does the edge connect the nodes both ways
	 */
	public boolean isBothWays()
	{
		return this.twoWays;
	}
	
	/**
	 * @return The id that describes this edge and distiguishes it from other 
	 * edges that might be connected to a same node
	 */
	public String getID()
	{
		return this.id;
	}
	
	
	// OTHER METHODS	----------------------------
	
	/**
	 * Disconnects the edge from the both nodes, making it effectively useless
	 */
	public void remove()
	{
		if (getStartNode() != null)
			getStartNode().removeEdge(this);
		if (getEndNode() != null)
			getEndNode().removeEdge(this);
		
		this.start = null;
		this.end = null;
	}
	
	/**
	 * Makes a two way edge one way. Please make sure the start and end nodes are 
	 * correct before calling this.
	 */
	public void makeOneWay()
	{
		if (this.twoWays)
		{
			this.twoWays = false;
			if (getEndNode() != null)
				getEndNode().removeEdge(this);
		}
	}
	
	/**
	 * Makes the edge a two way edge instead of one way.
	 */
	public void makeBothWays()
	{
		if (!this.twoWays)
		{
			this.twoWays = true;
			if (getEndNode() != null)
				getEndNode().addLeavingEdge(this);
		}
	}
	
	/**
	 * Changes the direction of the edge. Making the start node the end node and vice versa. 
	 * Naturally this has more effect in one way edges.
	 */
	public void swapDirection()
	{
		// If the edge is one way, informs the nodes
		if (!this.twoWays)
		{
			if (getStartNode() != null)
				getStartNode().removeEdge(this);
			if (getEndNode() != null)
				getEndNode().addLeavingEdge(this);
		}
		
		// Swaps the start and end nodes
		GraphNode<TNode, TEdge> temp = getStartNode();
		this.start = getEndNode();
		this.end = temp;
	}
	
	/**
	 * Checks if the edge connects the two nodes
	 * @param node1 The first node
	 * @param node2 The second node
	 * @return Does the edge connect the nodes in any way
	 */
	public boolean connectsNodes(GraphNode<TNode, TEdge> node1, GraphNode<TNode, TEdge> node2)
	{
		Collection<GraphNode<TNode, TEdge>> nodes = getConnectedNodes();
		return nodes.contains(node1) && nodes.contains(node2);
	}
	
	/**
	 * Checks if the edge connects the two nodes
	 * @param node1ID The unique id of the first node
	 * @param node2ID The unique id of the second node
	 * @return Does the edge connect the nodes in any way
	 */
	/*
	public boolean connectsNodes(String node1ID, String node2ID)
	{
		return (getStartNode().getID() == node1ID && getEndNode().getID() == node2ID) || 
				(getStartNode().getID() == node2ID && getEndNode().getID() == node1ID);
	}
	*/
	
	/**
	 * Checks if the edge is connected to (starts from or ends to) the given node.
	 * @param node The node that may be connected to this edge
	 * @return Is the node connected to this edge
	 */
	public boolean isConnectedTo(GraphNode<TNode, TEdge> node)
	{
		return getConnectedNodes().contains(node);
	}
	
	/**
	 * @return The nodes connected to this edge
	 */
	public ArrayList<GraphNode<TNode, TEdge>> getConnectedNodes()
	{
		ArrayList<GraphNode<TNode, TEdge>> nodes = new ArrayList<>();
		if (getStartNode() != null)
			nodes.add(getStartNode());
		if (getEndNode() != null)
			nodes.add(getEndNode());
		return nodes;
	}
}
