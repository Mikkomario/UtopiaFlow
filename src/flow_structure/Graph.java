package flow_structure;

import java.util.ArrayList;

/**
 * Graph is a set of nodes and edges. Graphs can be saved and loaded from 
 * text files.
 * 
 * @author Mikko Hilpinen
 * @since 1.5.2014
 */
public class Graph
{
	// ATTRIBUTES	----------------------------------------------------
	
	private ArrayList<GraphNode> nodes;
	
	
	// CONSTRUCTOR	----------------------------------------------------
	
	/**
	 * Creates a new empty graph
	 */
	public Graph()
	{
		// Initializes attributes
		this.nodes = new ArrayList<GraphNode>();
	}

	
	// OTHER METHODS	------------------------------------------------
	
	/**
	 * Adds a new node to the graph
	 * @param node The node that will be added to the graph
	 */
	public void addNode(GraphNode node)
	{
		if (!this.nodes.contains(node))
			this.nodes.add(node);
	}
	
	/**
	 * Checks if the graph contains a node with the given ID
	 * @param nodeID The id to be searched for
	 * @return is there a node with the given ID in the graph
	 */
	public boolean graphContainsNodeWithID(String nodeID)
	{
		return getNodeWithID(nodeID) != null;
	}
	
	/**
	 * Checks if there's a node with the given ID and returns it
	 * @param nodeID The ID that is searched for
	 * @return The node with the given ID or null if it couldn't be found
	 */
	public GraphNode getNodeWithID(String nodeID)
	{
		for (GraphNode node : this.nodes)
		{
			if (node.getID().equals(nodeID))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Removes a node from the graph, disconnecting it from any other nodes
	 * @param node The node that will be removed from the graph
	 */
	public void removeNode(GraphNode node)
	{
		if (!this.nodes.contains(node))
			return;
		
		this.nodes.remove(node);
		
		// Also removes the edges
		node.removeAllEdges(true);
	}
	
	/**
	 * @return How many nodes there are in this graph
	 */
	public int getNodeAmount()
	{
		return this.nodes.size();
	}
	
	/**
	 * Returns a node with the given index
	 * @param index The index of the node to be returned
	 * @return A node with the given index or null if there isn't one
	 */
	public GraphNode getNode(int index)
	{
		if (index < 0 || index >= getNodeAmount())
			return null;
		
		return this.nodes.get(index);
	}
	
	// TODO: Add saving and loading
	
	/**
	 * Saves the graph into the given file
	 * @param fileName The name of the file that will be created. Previous files 
	 * with the same name will be overwritten. "data/" included automatically.
	 */
	/*
	public void saveGraphIntoFile(String fileName)
	{
		new GraphSaver(fileName);
	}
	*/
	
	/**
	 * Loads a graph from the given file and adds as a part of this graph. 
	 * Adding graphs that contain similar IDs may be problematic.
	 * @param fileName The name of the file the data will be loaded from. 
	 * "data/" automatically included
	 * @throws FileNotFoundException If the file couldn't be found
	 */
	/*
	public void loadDataFromFile(String fileName) throws FileNotFoundException
	{
		new GraphLoader(fileName);
	}
	*/
	
	
	// SUBCLASSES	-----------------------------------------------------
	/*
	private class GraphSaver extends AbstractFileWriter
	{
		// ATTRIBUTES	-------------------------------------------------
		
		private int currentNodeIndex, currentEdgeIndex, mode;
		
		private static final int NODEINTRO = 0, NODES = 1, EDGEINTRO = 2, EDGES = 3;
		
		
		// CONSTRUCTOR	-------------------------------------------------
		
		public GraphSaver(String fileName)
		{
			// Initializes attributes
			this.currentNodeIndex = 0;
			this.currentEdgeIndex = 0;
			this.mode = NODEINTRO;
			
			// Starts writing the data
			saveIntoFile(fileName);
		}
		
		
		// IMPLEMENTED METHODS	-----------------------------------------
		
		@Override
		protected String writeLine(int lineindex)
		{
			// Acts differently in different modes
			if (this.mode == EDGES)
				getNextEdgeLine();
			else if (this.mode == NODES)
				getNextNodeLine();
			else if (this.mode == EDGEINTRO)
			{
				this.mode = EDGES;
				return "&edges";
			}
			else if (this.mode == NODEINTRO)
			{
				this.mode = NODES;
				return "&nodes";
			}
			
			return END_OF_STREAM;
		}
		
		
		// OTHER METHODS	---------------------------------------------
		
		private String getNextEdgeLine()
		{
			this.currentEdgeIndex ++;
			
			// Checks if the last edge was reached. If so, moves to the next node
			if (this.currentEdgeIndex >= 
					Graph.this.getNode(this.currentNodeIndex).getEdgeAmount())
			{
				this.currentEdgeIndex = 0;
				this.currentNodeIndex ++;
			}
			
			// Checks if the last node was reached. If so, quits
			if (this.currentNodeIndex >= Graph.this.getNodeAmount())
				return END_OF_STREAM;
			
			// Otherwise returns the savedata of the current edge
			return Graph.this.getNode(this.currentNodeIndex).getLeavingEdge(
					this.currentEdgeIndex).getSaveData();
		}
		
		private String getNextNodeLine()
		{
			this.currentNodeIndex ++;
			
			// Checks if the end of the nodes was reached and stops if it was
			if (this.currentNodeIndex >= Graph.this.getNodeAmount())
			{
				this.currentNodeIndex = 0;
				this.mode = EDGEINTRO;
				return "";
			}
			
			// Otherwise prints the current node data
			return Graph.this.getNode(this.currentNodeIndex).getSaveData();
		}
	}
	
	private class GraphLoader extends AbstractFileReader
	{
		// ATTRIBUTES	-------------------------------------------------
		
		private int mode;
		
		private static final int NONE = 0, NODES = 1, EDGES = 2;
		
		
		// CONSTRUCTOR	-------------------------------------------------
		
		public GraphLoader(String fileName) throws FileNotFoundException
		{
			// Initializes attributes
			this.mode = NONE;
			
			// Reads the save file
			readFile(fileName, "*");
		}
		
		
		// IMPLEMENTED METHODS	----------------------------------------
		
		@Override
		protected void onLine(String line)
		{
			// Changes mode on lines that start with with "&"
			if (line.startsWith("&"))
			{
				if (line.equals("&nodes"))
					this.mode = NODES;
				else if (line.equals("&edges"))
					this.mode = EDGES;
			}
			else
			{
				String[] arguments = line.split("#");
				
				// Otherwise acts as the mode requires
				if (this.mode == NODES)
				{
					if (arguments.length < 2)
					{
						System.err.println("Cannot load a node from a line " + 
								line + ". The line contains two few (<2) arguments");
						return;
					}
					
					// Reads a node from the line
					// (Name#Data)
					Graph.this.addNode(new GraphNode(arguments[0], arguments[1]));
				}
				else if (this.mode == EDGES)
				{
					if (arguments.length < 3)
					{
						System.err.println("Cannot load an edge from a line " + 
								line + ". The line contains two few (<3) arguments");
						return;
					}
					
					// Checks the node IDs
					String[] nodeIDs = arguments[2].split("+");
					
					if (nodeIDs.length < 2)
					{
						System.err.println("Cannot load an edge from a line " + 
								line + ". There aren't enough (2) nodeIDs to connect.");
						return;
					}
					
					// Connects the nodes with an edge
					Graph.this.getNodeWithID(nodeIDs[0]).connectNode(
							Graph.this.getNodeWithID(nodeIDs[1]), arguments[0], 
							arguments[1], false);
				}
			}
		}
	}
	*/
}
