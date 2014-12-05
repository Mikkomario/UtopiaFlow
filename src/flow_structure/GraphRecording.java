package flow_structure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import flow_recording.AbstractConstructor;
import flow_recording.Constructable;
import flow_recording.ObjectFormatException;
import flow_recording.ObjectParser;
import flow_recording.TextObjectWriter;
import flow_recording.Writable;
import flow_recording.XMLObjectWriter;

/**
 * This class is used for saving and loading graphs from text files and XML data
 * 
 * @author Mikko Hilpinen
 * @param <TNode> The type of the node data
 * @param <TEdge> The type of the edge data
 * @since 5.12.2014
 */
public class GraphRecording<TNode, TEdge>
{
	// ATTRIBUTES	------------------------
	
	private static final String REGEX = "AND";
	
	private Map<String, NodeRecording> nodes;
	private Map<String, EdgeRecording> edges;
	private ObjectParser<TNode> nodeParser;
	private ObjectParser<TEdge> edgeParser;
	
	private NodeRecordingConstructor lastConstructor;
	
	
	// CONSTRUCTOR	------------------------
	
	/**
	 * Creates a new GraphRecording that is ready to read and write graphs
	 * @param nodeDataParser The parser that parses node data
	 * @param edgeDataParser The parser that parses edge data
	 */
	public GraphRecording(ObjectParser<TNode> nodeDataParser, 
			ObjectParser<TEdge> edgeDataParser)
	{
		// Initializes attributes
		this.nodeParser = nodeDataParser;
		this.edgeParser = edgeDataParser;
		reset();
	}
	
	
	// OTHER METHODS	---------------------
	
	/**
	 * @return a NodeConstructor that can be instructed to create GraphRecordings
	 * @see #toGraph()
	 */
	public NodeRecordingConstructor createConstructor()
	{
		// If there already was a constructor, clears it first
		if (this.lastConstructor != null)
			reset();
		return new NodeRecordingConstructor();
	}
	
	/**
	 * @return Creates a new Graph based on this recording
	 * @see #createConstructor()
	 * @see #reset()
	 */
	public Graph<TNode, TEdge> toGraph()
	{
		Graph<TNode, TEdge> graph = new Graph<>();
		
		// Adds nodes to the graph
		for (NodeRecording nodeRecord : this.nodes.values())
		{
			graph.addNode(nodeRecord.toNode());
		}
		
		// Connects them with edges
		for (EdgeRecording edgeRecord : this.edges.values())
		{
			GraphNode<TNode, TEdge> startNode = graph.getNode(edgeRecord.startID);
			GraphNode<TNode, TEdge> endNode = graph.getNode(edgeRecord.endID);
			edgeRecord.toEdge(startNode, endNode);
		}
		
		return graph;
	}
	
	/**
	 * Records the given graph into this recording. The previous content will be overwritten.
	 * @param graph The graph that will be recorded
	 */
	public void record(Graph<TNode, TEdge> graph)
	{
		reset();
		
		// Creates a NodeRecording for each node in the graph (edges are recorded by 
		// nodeRecordings)
		for (GraphNode<TNode, TEdge> node : graph.getNodes())
		{
			new NodeRecording(node);
		}
	}
	
	/**
	 * @return The writable content that has been recorded. The content can be written by a 
	 * writer, like a TextObjectWriter or XMLObjectWriter.
	 * @see TextObjectWriter#writeInto(Writable, java.io.BufferedWriter)
	 * @see XMLObjectWriter#writeInto(Writable, javax.xml.stream.XMLStreamWriter)
	 */
	public Collection<? extends Writable> getWritableContent()
	{
		return this.nodes.values();
	}
	
	/**
	 * Makes the recording forget all data it has recorded so far
	 */
	public void reset()
	{
		this.nodes = new HashMap<>();
		this.edges = new HashMap<>();
		
		if (this.lastConstructor != null)
		{
			this.lastConstructor.reset();
			this.lastConstructor = null;
		}
	}

	
	// SUBCLASSES	------------------------
	
	private class NodeRecording implements Constructable<NodeRecording>, Writable
	{
		// ATTRIBUTES	--------------------
		
		private String data, id;
		private Map<String, String> edges; // <EdgeID, NodeID>
		
		
		// CONSTRUCTOR	--------------------
		
		public NodeRecording()
		{
			this.edges = new HashMap<>();
		}
		
		public NodeRecording(GraphNode<TNode, TEdge> node)
		{
			this.edges = new HashMap<>();
			
			this.data = GraphRecording.this.nodeParser.parseToString(node.getData());
			this.id = node.getID();
			
			// Nodes are recorded in the recording
			GraphRecording.this.nodes.put(this.id, this);
			
			for (GraphEdge<TNode, TEdge> edge : node.getLeavingEdges())
			{
				// Only records each edge once, that's why this extra check
				if (edge.getStartNode() == node)
				{
					// Records the edge to this node's edge map as well
					this.edges.put(new EdgeRecording(edge).id, edge.getEndNode().getID());
				}
			}
		}
		
		
		// IMPLEMENTED METHODS	---------------------
		
		@Override
		public Map<String, String> getAttributes()
		{
			// There is only one attribute and that is node data
			Map<String, String> attributes = new HashMap<>();
			attributes.put("data", this.data);
			
			return attributes;
		}

		@Override
		public Map<String, Writable> getLinks()
		{
			// TODO: All links seem to point towards the node itself
			
			// Links to other nodeRecordings, keys are edge strings
			Map<String, Writable> links = new HashMap<>();
			
			for (String edgeID : this.edges.keySet())
			{
				String nodeID = this.edges.get(edgeID);
				links.put(GraphRecording.this.edges.get(edgeID).toString(), 
						GraphRecording.this.nodes.get(nodeID));
			}
			
			return links;
		}

		@Override
		public String getID()
		{
			return this.id;
		}

		@Override
		public void setID(String id)
		{
			this.id = id;
			// Also records the nodeRecording to the recording
			GraphRecording.this.nodes.put(id, this);
		}

		@Override
		public void setAttribute(String attributeName, String attributeValue)
		{
			// Sets the data (the only possible attribute)
			this.data = attributeValue;
		}

		@Override
		public void setLink(String linkName, NodeRecording target)
		{
			// Remembers the connecting link (also parses it into an edge, 
			// which is recorded automatically)
			this.edges.put(new EdgeRecording(linkName, this.id, target.id).id, target.id);
		}
		
		
		// OTHER METHODS	--------------------
		
		public GraphNode<TNode, TEdge> toNode()
		{
			return new GraphNode<>(this.id, 
					GraphRecording.this.nodeParser.parseFromString(this.data));
		}
	}
	
	private class EdgeRecording
	{
		// ATTRIBUTES	--------------------
		
		private String data, id, startID, endID;
		private boolean bothWays;
		
		
		// CONSTRUCTOR	--------------------
		
		public EdgeRecording(GraphEdge<TNode, TEdge> edge)
		{
			this.data = GraphRecording.this.edgeParser.parseToString(edge.getData());
			this.id = edge.getID();
			this.bothWays = edge.isBothWays();
			this.startID = edge.getStartNode().getID();
			this.endID = edge.getEndNode().getID();
			
			// Edges are recorded in the recording
			GraphRecording.this.edges.put(this.id, this);
		}
		
		// String format: dataANDidANDbothWays
		public EdgeRecording(String edgeString, String startID, String endID)
		{
			String[] arguments = edgeString.split(REGEX);
			
			if (arguments.length < 3)
				throw new ObjectFormatException("Can't parse an edge from " + edgeString);
			
			this.data = arguments[0];
			this.id = arguments[1];
			this.bothWays = Boolean.parseBoolean(arguments[2]);
			this.startID = startID;
			this.endID = endID;
			
			// Edges are recorded in the recording
			GraphRecording.this.edges.put(this.id, this);
		}
		
		
		// IMPLEMENTED METHODS	----------------
		
		@Override
		public String toString()
		{
			return this.data + REGEX + this.id + REGEX + this.bothWays;
		}
		
		
		// OTHER METHODS	--------------------
		
		public GraphEdge<TNode, TEdge> toEdge(GraphNode<TNode, TEdge> start, 
				GraphNode<TNode, TEdge> end)
		{
			return new GraphEdge<>(start, end, this.id, 
					GraphRecording.this.edgeParser.parseFromString(this.data), 
					this.bothWays);
		}
	}
	
	private class NodeRecordingConstructor extends AbstractConstructor<NodeRecording>
	{
		// IMPLEMENTED METHODS	----------------
		
		@Override
		protected GraphRecording<TNode, TEdge>.NodeRecording createConstructable(
				String instruction)
		{
			return new NodeRecording();
		}
	}
}
