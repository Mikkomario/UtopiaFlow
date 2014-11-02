package flow_structure;

import java.util.ArrayList;

/**
 * Nodes are used in graphs and they contain data. Nodes can be connected via Edges
 * 
 * @author Mikko Hilpinen
 * @since 1.5.2014
 */
public class GraphNode
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private String data, id;
	private ArrayList<GraphEdge> leavingEdges;
	
	
	// CONSTRUCTOR	-----------------------------------------------------
	
	/**
	 * Creates a new node with the given unique id and data
	 * 
	 * @param id The name of the node unique to this specific instance
	 * @param data The data held in the node
	 */
	public GraphNode(String id, String data)
	{
		// Initializes attributes
		this.id = id;
		this.data = data;
		this.leavingEdges = new ArrayList<GraphEdge>();
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
	public String getData()
	{
		return this.data;
	}
	
	
	// OTHER METHODS	------------------------------------------------
	
	/**
	 * Connects this node to another with an edge
	 * 
	 * @param endNode The node this node will be connected to
	 * @param edgeID The identifier given to the edge in order to distinguish 
	 * it from other edges connected to this node
	 * @param edgeData The data stored in the connecting edge
	 * @param connectBothWays Should the other node be connected to this node as well
	 */
	public void connectNode(GraphNode endNode, String edgeID, String edgeData, 
			boolean connectBothWays)
	{
		if (endNode == null)
			return;
		
		// Checks that there aren't already an edge connecting the nodes
		if (getEdgeIndex(endNode.getID()) == -1)
			// Connects the two nodes with an edge
			this.leavingEdges.add(new GraphEdge(this, endNode, edgeID, edgeData));
		
		if (connectBothWays)
			endNode.connectNode(this, edgeID, edgeData, false);
	}
	
	/**
	 * Disconnects the given node from this one.
	 * 
	 * @param endNode The node that will be disconnected from this node
	 * @param disconnectBothWays Should this node be disconnected from the 
	 * other node as well
	 */
	public void disconnectNode(GraphNode endNode, boolean disconnectBothWays)
	{
		if (endNode == null)
			return;
		
		int edgeIndex = getEdgeIndex(endNode.getID());
		
		if (edgeIndex != -1)
			this.leavingEdges.remove(edgeIndex);
		
		// May also disconnect this node from the other one
		if (disconnectBothWays)
			endNode.disconnectNode(this, false);
	}
	
	/**
	 * Tells if this node is connected to the given node with an edge
	 * @param endNode The node this node may be connected to
	 * @return Is this node connected to the other node
	 */
	public boolean isConnectedToNode(GraphNode endNode)
	{
		return endNode != null && getEdgeIndex(endNode.getID()) != -1;
	}
	
	/**
	 * @return The number of edges this node uses
	 */
	public int getEdgeAmount()
	{
		return this.leavingEdges.size();
	}
	
	/**
	 * Returns an edge with the given index
	 * @param index The 
	 * @return The edge with the given index or null if there isn't an edge with 
	 * the given index
	 */
	public GraphEdge getLeavingEdge(int index)
	{
		if (index < 0 || index >= getEdgeAmount())
			return null;
		
		return this.leavingEdges.get(index);
	}
	
	/**
	 * Returns a connected edge with the given identifier
	 * @param edgeID The unique identifier the edge should have
	 * @return The edge with the given id or null if no such edge exists
	 */
	public GraphEdge getLeavingEdge(String edgeID)
	{
		for (GraphEdge edge : this.leavingEdges)
		{
			if (edge.getID().equals(edgeID))
				return edge;
		}
		
		return null;
	}
	
	/**
	 * @return The data that is needed to recreate this node
	 */
	public String getSaveData()
	{
		return getID() + "#" + getData();
	}
	
	/**
	 * Removes all the edges from this node
	 * @param disconnectBothWays Should the edges connected to this node from 
	 * other nodes be removed as well
	 */
	public void removeAllEdges(boolean disconnectBothWays)
	{
		if (disconnectBothWays)
		{
			while (this.leavingEdges.size() > 0)
			{
				disconnectNode(this.leavingEdges.get(0).getEndNode(), true);
			}
		}
		else
			this.leavingEdges.clear();
	}
	
	// Returns the index of the edge connected to the given node or -1 if not 
	// connected
	private int getEdgeIndex(String endNodeID)
	{
		for (int i = 0; i < this.leavingEdges.size(); i++)
		{
			if (this.leavingEdges.get(i).getEndNode().getID().equals(endNodeID))
				return i;
		}
		
		return -1;
	}
}
