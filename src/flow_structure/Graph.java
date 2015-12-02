package flow_structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	private Set<GraphNode<NodeContent, EdgeContent>> nodes;
	
	
	// CONSTRUCTOR	----------------------
	
	/**
	 * Creates a new graph
	 */
	public Graph()
	{
		this.nodes = new HashSet<>();
	}
	
	
	// ACCESSORS	-----------------------
	
	/**
	 * @return The nodes stored in this graph. The returned set is a copy and changes made 
	 * to it won't affect the graph.
	 */
	public Set<GraphNode<NodeContent, EdgeContent>> getNodes()
	{
		return new HashSet<>(this.nodes);
	}
	
	
	// OTHER METHODS	-------------------
	
	/**
	 * @return All the edges from each node in this graph
	 */
	/*
	public Set<GraphEdge<NodeContent, EdgeContent>> getEdges()
	{
		Set<GraphEdge<NodeContent, EdgeContent>> edges = new HashSet<>();
		for (GraphNode<NodeContent, EdgeContent> node : getNodes())
		{
			edges.addAll(node.getLeavingEdges());
		}
		
		return edges;
	}
	*/
	
	/**
	 * Adds a new node to the graph
	 * @param node The node that will be added to the graph
	 */
	public void addNode(GraphNode<NodeContent, EdgeContent> node)
	{
		if (node != null)
			this.nodes.add(node);
	}
	
	/**
	 * Finds the nodes in this graph that have edges pointing towards the provided node.
	 * @param endNode The node the edges point towards
	 * @return The nodes the connecting edges leave from
	 */
	public Set<GraphNode<NodeContent, EdgeContent>> findLeadingNodes(GraphNode<?, ?> endNode)
	{
		Set<GraphNode<NodeContent, EdgeContent>> nodes = new HashSet<>();
		
		for (GraphNode<NodeContent, EdgeContent> node : getNodes())
		{
			if (node.hasEdgeTowards(endNode))
				nodes.add(node);
		}
		
		return nodes;
	}
	
	/**
	 * Finds all the nodes in this graph that are connected to the provided node. That includes 
	 * all nodes the node has edges towards.
	 * @param node A node
	 * @return Each node in this graph connected to the provided node.
	 */
	public Set<GraphNode<NodeContent, EdgeContent>> findConnectedNodes(GraphNode<NodeContent, 
			EdgeContent> node)
	{
		Set<GraphNode<NodeContent, EdgeContent>> nodes = node.getEndNodes();
		nodes.addAll(findLeadingNodes(node));
		
		return nodes;
	}
	
	/**
	 * Finds all the nodes in this graph that have content equal to the one provided
	 * @param content The content of the searched nodes
	 * @return All nodes in this graph that have the provided content
	 */
	public List<GraphNode<NodeContent, EdgeContent>> findNodes(NodeContent content)
	{
		List<GraphNode<NodeContent, EdgeContent>> nodes = new ArrayList<>();
		for (GraphNode<NodeContent, EdgeContent> node : getNodes())
		{
			if (node.getContent() == null)
			{
				if (content == null)
					nodes.add(node);
			}
			else if (node.getContent().equals(content))
				nodes.add(node);
		}
		
		return nodes;
	}
	
	/**
	 * Finds all edges in this graph that have content equal to the one provided
	 * @param content The content of the searched edges
	 * @return All edges which have the provided content
	 */
	public List<GraphEdge<NodeContent, EdgeContent>> findEdges(EdgeContent content)
	{
		List<GraphEdge<NodeContent, EdgeContent>> edges = new ArrayList<>();
		for (GraphNode<NodeContent, EdgeContent> node : getNodes())
		{
			edges.addAll(node.findEdges(content));
		}
		
		return edges;
	}
}
