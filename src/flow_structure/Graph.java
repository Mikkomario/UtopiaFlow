package flow_structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flow_recording.IDGenerator;

/**
 * Graph is a set of nodes and edges. Graphs can be saved and loaded from 
 * text files.
 * 
 * @author Mikko Hilpinen
 * @param <TNode> The type of data contained within the nodes in this Graph
 * @param <TEdge> The type of data contained within the edges in this Graph
 * @since 1.5.2014
 */
public class Graph<TNode, TEdge>
{
	// ATTRIBUTES	----------------------------------------------------
	
	private Map<String, GraphNode<TNode, TEdge>> nodes;
	private IDGenerator idGen;
	
	
	// CONSTRUCTOR	----------------------------------------------------
	
	/**
	 * Creates a new empty graph
	 */
	public Graph()
	{
		// Initializes attributes
		this.nodes = new HashMap<>();
		this.idGen = new IDGenerator();
	}

	
	// OTHER METHODS	------------------------------------------------

	// TODO: Add copying feature
	
	/**
	 * Adds a new node to the graph
	 * @param node The node that will be added to the graph
	 */
	protected void addNode(GraphNode<TNode, TEdge> node)
	{
		if (!contains(node))
			this.nodes.put(node.getID(), node);
		
		this.idGen.reserveID(node.getID());
	}
	
	/**
	 * Creates and adds a node to the Graph. The node will contain the given data.
	 * @param data The data contained within the new node.
	 * @return The node that was just created to contain the data.
	 */
	public GraphNode<TNode, TEdge> addNode(TNode data)
	{
		GraphNode<TNode, TEdge> node = new GraphNode<>(this.idGen.generateID(), data);
		addNode(node);
		return node;
	}
	
	/**
	 * Checks if the graph contains a node with the given ID
	 * @param nodeID The id to be searched for
	 * @return is there a node with the given ID in the graph
	 */
	public boolean contains(String nodeID)
	{
		return this.nodes.containsKey(nodeID);
	}
	
	/**
	 * Checks if the graph contains the given node
	 * @param node The node that may be contained within the Graph
	 * @return Does the Graph contain the given node
	 */
	public boolean contains(GraphNode<?, ?> node)
	{
		return this.nodes.containsValue(node);
	}
	
	/**
	 * Checks if the graph contains the given graph
	 * @param graph The graph that may be contained within this graph
	 * @return Is the given graph a sub graph of this graph
	 */
	public boolean contains(Graph<TNode, TEdge> graph)
	{
		// Contains the graph if contains all the nodes in it
		for (GraphNode<TNode, TEdge> node : graph.getNodes())
		{
			if (!contains(node))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if there's a node with the given ID and returns it
	 * @param nodeID The ID that is searched for
	 * @return The node with the given ID or null if it couldn't be found
	 */
	public GraphNode<TNode, TEdge> getNode(String nodeID)
	{
		return this.nodes.get(nodeID);
	}
	
	/**
	 * Removes a node with the given ID from the graph. Disconnecting it from any other nodes.
	 * @param nodeID The unique identifier of the node that is removed
	 */
	public void removeNode(String nodeID)
	{
		// Removes the node
		this.nodes.remove(nodeID);
		
		// Removes all the edges connected to the node
		for (GraphEdge<TNode, TEdge> edge : getDirectlyConnectedEdges(nodeID))
		{
			edge.remove();
		}
	}
	
	/**
	 * @return How many nodes there are in this graph
	 */
	public int size()
	{
		return this.nodes.size();
	}
	
	/**
	 * Creates a connecting edge between two nodes
	 * 
	 * @param startNodeID The node the edge starts from
	 * @param endNodeID The node the edge ends to
	 * @param edgeData The data contained within the edge
	 * @param bothWays If the edge should be two way instead of one way
	 */
	public void connectNodes(String startNodeID, String endNodeID, TEdge edgeData, 
			boolean bothWays)
	{
		// Checks the parameters
		if (!contains(startNodeID) || !contains(endNodeID))
			return;
		
		// Connects the nodes with an edge
		new GraphEdge<>(getNode(startNodeID), getNode(endNodeID), this.idGen.generateID(), 
				edgeData, bothWays);
	}
	
	/**
	 * @return A list of all the nodes within this graph
	 */
	public ArrayList<GraphNode<TNode, TEdge>> getNodes()
	{
		ArrayList<GraphNode<TNode, TEdge>> nodes = new ArrayList<>();
		nodes.addAll(this.nodes.values());
		return nodes;
	}
	
	/**
	 * @return A list of all the node IDs used within this graph
	 */
	public ArrayList<String> getNodeIDs()
	{
		ArrayList<String> nodeIDs = new ArrayList<>();
		nodeIDs.addAll(this.nodes.keySet());
		return nodeIDs;
	}
	
	/**
	 * @return All the edges within this graph
	 */
	public ArrayList<GraphEdge<TNode, TEdge>> getEdges()
	{
		ArrayList<GraphEdge<TNode, TEdge>> foundEdges = new ArrayList<>();
		
		for (GraphNode<TNode, TEdge> node : getNodes())
		{
			for (GraphEdge<TNode, TEdge> edge : node.getLeavingEdges())
			{
				if (!foundEdges.contains(edge))
					foundEdges.add(edge);
			}
		}
		
		return foundEdges;
	}
	
	/**
	 * Returns an edge with the given id or null if no such edge exists within this Graph
	 * @param edgeID The unique identifier of the edge
	 * @return The edge with the given identifier
	 */
	public GraphEdge<TNode, TEdge> getEdge(String edgeID)
	{
		for (GraphNode<TNode, TEdge> node : getNodes())
		{
			for (GraphEdge<TNode, TEdge> edge : node.getLeavingEdges())
			{
				if (edge.getID() == edgeID)
					return edge;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds all the edges that connect the given nodes
	 * @param nodes The nodes that may be connected in some way
	 * @return The edges that connect the nodes
	 */
	public ArrayList<GraphEdge<TNode, TEdge>> getDirectlyConnectingEdges(List<GraphNode<TNode, TEdge>> nodes)
	{
		ArrayList<GraphEdge<TNode, TEdge>> foundEdges = new ArrayList<>();
		
		// Finds all the leaving edges that connect a node to any other nodes
		for (int startIndex = 0; startIndex < nodes.size(); startIndex ++)
		{
			for (GraphEdge<TNode, TEdge> edge : nodes.get(startIndex).getLeavingEdges())
			{
				for (int endIndex = startIndex + 1; endIndex < nodes.size(); endIndex ++)
				{
					if (edge.connectsNodes(nodes.get(startIndex), nodes.get(endIndex)) 
							&& !foundEdges.contains(edge))
						foundEdges.add(edge);
				}
			}
		}
		
		return foundEdges;
	}
	
	/**
	 * Creates a graph that contains all the nodes that can be reached from a node with 
	 * the given id. In other words, returns the largest connected Graph that contains the 
	 * given node. Please note that changes in the subGraph will be seen in this graph 
	 * as well
	 * @param includedNodeID The id of the node that should be contained within the graph
	 * @return The largest solid graph containing the given node.
	 */
	public Graph<TNode, TEdge> getSubGraph(String includedNodeID)
	{
		Graph<TNode, TEdge> graph = new Graph<>();
		
		GraphNode<TNode, TEdge> startNode = getNode(includedNodeID);
		if (startNode == null)
			return graph;
		
		// Finds out the neighbors of the node, their neighbors and so on until all connected 
		// nodes have been found
		List<GraphNode<TNode, TEdge>> foundNodes = new ArrayList<>();
		List<GraphNode<TNode, TEdge>> lastNeighbors = new ArrayList<>();
		List<GraphNode<TNode, TEdge>> newNeighbors = new ArrayList<>();
		
		lastNeighbors.add(startNode);
		
		while (!lastNeighbors.isEmpty())
		{
			// Finds the new neighbors
			for (GraphNode<TNode, TEdge> node : lastNeighbors)
			{
				for (GraphNode<TNode, TEdge> neighbor : node.getEndNodes())
				{
					if (!newNeighbors.contains(neighbor) && !lastNeighbors.contains(neighbor) 
							&& !foundNodes.contains(neighbor))
						newNeighbors.add(neighbor);
				}
			}
			
			// Prepares for the next possible loop
			foundNodes.addAll(lastNeighbors);
			lastNeighbors.clear();
			lastNeighbors.addAll(newNeighbors);
			newNeighbors.clear();
		}
		
		// Creates a graph out of the nodes
		for (GraphNode<TNode, TEdge> node : foundNodes)
		{
			graph.addNode(node);
		}
		
		return graph;
	}
	
	/**
	 * @return Is the graph solid. Solid graphs can't be broken into smaller parts without 
	 * removing any edges. In solid graphs, all nodes are connected to each other in some way.
	 */
	public boolean isSolid()
	{
		if (size() == 0)
			return true;
		
		return getSubGraph(getNodeIDs().get(0)).size() == size();
	}
	
	/**
	 * Checks if the end node can be reached from the start node by any means
	 * @param startNodeID The identifier of the start node
	 * @param endNodeID The identifier of the end node
	 * @return Can the end node be reached from the start node by any means
	 */
	public boolean traversingIsPossible(String startNodeID, String endNodeID)
	{
		GraphNode<TNode, TEdge> endNode = getNode(endNodeID);
		
		if (endNode == null)
			return false;
		
		return getSubGraph(startNodeID).contains(endNode);
	}
	
	/**
	 * Finds all the edges that are directly connected to the given node (= start from it or 
	 * end to it).
	 * @param nodeID The identifier of the node in this graph
	 * @return All edges in this graph that are connected to the given node
	 */
	public ArrayList<GraphEdge<TNode, TEdge>> getDirectlyConnectedEdges(String nodeID)
	{
		ArrayList<GraphEdge<TNode, TEdge>> edges = new ArrayList<>();
		GraphNode<TNode, TEdge> startNode = getNode(nodeID);
		
		if (startNode == null)
			return edges;
		
		for (GraphEdge<TNode, TEdge> edge : getEdges())
		{
			if (edge.isConnectedTo(startNode) && !edges.contains(edge))
				edges.add(edge);
		}
		
		return edges;
	}
	
	/**
	 * Finds all the nodes that are directly connected to this one node
	 * @param nodeID The identifier of the node in this graph
	 * @return All nodes directly connected to the given node
	 */
	public ArrayList<GraphNode<TNode, TEdge>> getDirectlyConnectedNodes(String nodeID)
	{
		GraphNode<TNode, TEdge> startNode = getNode(nodeID);
		ArrayList<GraphNode<TNode, TEdge>> foundNodes = new ArrayList<>();
		
		if (startNode == null)
			return foundNodes;
		
		ArrayList<GraphEdge<TNode, TEdge>> edges = getDirectlyConnectedEdges(nodeID);
		
		for (GraphEdge<TNode, TEdge> edge : edges)
		{
			for (GraphNode<TNode, TEdge> node : edge.getConnectedNodes())
			{
				if (node != startNode && !foundNodes.contains(node))
					foundNodes.add(node);
			}
		}
		
		return foundNodes;
	}
	
	/**
	 * Checks if the two nodes are directly connected to each other
	 * @param node1ID The identifier of the first node
	 * @param node2ID The identifier of the second node
	 * @return Are the two nodes directly connected to each other
	 */
	public boolean nodesAreDirectlyConnected(String node1ID, String node2ID)
	{
		GraphNode<TNode, TEdge> targetNode = getNode(node2ID);
		return getDirectlyConnectedNodes(node1ID).contains(targetNode);
	}
	
	/**
	 * Checks if the two nodes are directly connected to each other
	 * @param node1 The first node
	 * @param node2 The second node
	 * @return Are the two nodes directly connected to each other
	 */
	public boolean nodesAreDirectlyConnected(GraphNode<TNode, TEdge> node1, 
			GraphNode<TNode, TEdge> node2)
	{
		List<GraphNode<TNode, TEdge>> nodes = new ArrayList<>();
		nodes.add(node1);
		nodes.add(node2);
		return !getDirectlyConnectingEdges(nodes).isEmpty();
	}
}
