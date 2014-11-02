package flow_structure;

/**
 * Edges are used in graphs to connect nodes together. They may also contain 
 * data. Please note that edges only work a single way, the end node is not 
 * aware of the edge connected to it.
 * 
 * @author Mikko Hilpinen
 * @since 1.5.2014
 */
public class GraphEdge
{
	// ATTRIBUTES	-----------------------------------------------------
	
	private GraphNode start, end;
	private String data, id;
	
	
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
	 */
	public GraphEdge(GraphNode start, GraphNode end, String id, String data)
	{
		// Initializes attributes
		this.start = start;
		this.end = end;
		this.data = data;
	}

	
	// GETTERS & SETTERS	---------------------------------------------
	
	/**
	 * @return The data the edge contains
	 */
	public String getData()
	{
		return this.data;
	}
	
	/**
	 * @return The node the edge starts from
	 */
	public GraphNode getStartNode()
	{
		return this.start;
	}
	
	/**
	 * @return The node the edge ends to
	 */
	public GraphNode getEndNode()
	{
		return this.end;
	}
	
	/**
	 * @return The id that describes this edge and distiguishes it from other 
	 * edges that might be connected to a same node
	 */
	public String getID()
	{
		return this.id;
	}
	
	
	// OTHER METHODS	-------------------------------------------------
	
	/**
	 * @return The identifier used to distinguish this edge from the rest
	 */
	public String getSaveData()
	{
		return getID() + "#" + getData() + "#" + this.start.getID() + "+" + this.end.getID();
	}
}
