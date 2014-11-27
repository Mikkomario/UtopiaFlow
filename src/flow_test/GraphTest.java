package flow_test;

import java.util.List;
import java.util.Random;

import flow_structure.Graph;
import flow_structure.GraphEdge;
import flow_structure.GraphNode;

/**
 * GraphTest test the simple functionalities of graphs
 * 
 * @author Mikko Hilpinen
 * @since 27.11.2014
 */
public class GraphTest
{
	// CONSTRUCTOR	------------------------------------
	
	private GraphTest()
	{
		// The constructor is hidden since the interface is static 
	}

	
	// MAIN METHOD	-------------------------------------
	
	/**
	 * Starts the test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		// Creates a graph
		Graph<String, Integer> graph = createGraph(15, 15);
		
		// Prints the graph's nodes
		// Prints the graph's edges
		printGraph(graph);
		
		// Tries to find certain data
		System.out.println("Node with data 'Node 1': " + graph.findNodeWithData("Node 1").getID());
		System.out.println("Edge 100: " + graph.findEdgeWithData(100).getID());
		
		// Removes some nodes
		System.out.println("Removing Node 1");
		GraphNode<String, Integer> node1 = graph.findNodeWithData("Node 1");
		System.out.println("Which has " + node1.getLeavingEdgeAmount() + " leaving edges.");
		System.out.println("And " + graph.getDirectlyConnectedEdges(node1.getID()).size() + " connected edges.");
		System.out.println("Graph size before removal: " + graph.size());
		System.out.println("Graph edge amount before removal: " + graph.getEdges().size());
		graph.removeNode(node1.getID());
		System.out.println("Graph size after removal: " + graph.size());
		System.out.println("Graph edge amount after removal: " + graph.getEdges().size());
		
		// Prints the graph
		printGraph(graph);
		
		// Checks solidity
		System.out.println("Graph is solid: " + graph.isSolid());
		
		// Creates a subgraph
		System.out.println("Creating a subgraph ---------");
		Graph<String, Integer> subGraph = graph.getSubGraph(graph.findNodeWithData("Node 0").getID());
		// Prints the subgraph
		printGraph(subGraph);
		
		// Checks solidity
		System.out.println("Subgraph is solid: " + subGraph.isSolid());
		
		// Changes the subgraph
		System.out.println("Removes all nodes in the subGraph");
		for (String nodeID : subGraph.getNodeIDs())
		{
			subGraph.removeNode(nodeID);
		}
		
		// Prints both graphs
		System.out.println("Subgraph after removal: ");
		printGraph(subGraph);
		System.out.println("Graph after removal: ");
		printGraph(graph);
	}
	
	
	// OTHER METHODS	---------------------------------
	
	private static Graph<String, Integer> createGraph(int nodeAmount, int edgeAmount)
	{
		Random random = new Random();
		Graph<String, Integer> graph = new Graph<>();
		
		// Adds nodes
		for (int i = 0; i < nodeAmount; i ++)
		{
			graph.addNode("Node " + i);
		}
		
		// Adds edges randomly
		List<String> nodeIDs = graph.getNodeIDs();
		for (int i = 0; i < edgeAmount; i++)
		{
			String startNodeID = nodeIDs.get(random.nextInt(nodeAmount));
			String endNodeID = nodeIDs.get(random.nextInt(nodeAmount));
			graph.connectNodes(startNodeID, endNodeID, i, false);
		}
		graph.connectNodes(nodeIDs.get(0), nodeIDs.get(1), 100, false);
		graph.connectNodes(nodeIDs.get(1), nodeIDs.get(0), 101, false);
		graph.connectNodes(nodeIDs.get(2), nodeIDs.get(3), 102, true);
		
		return graph;
	}
	
	private static void printGraph(Graph<String, Integer> graph)
	{
		// Prints the nodes
		System.out.println("Nodes:");
		for (GraphNode<String, Integer> node : graph.getNodes())
		{
			System.out.println(node.getID() + ": " + node.getData());
		}
		System.out.println("Edges:");
		for (GraphEdge<String, Integer> edge : graph.getEdges())
		{
			System.out.println(edge.getID() + ": " + edge.getData() + " (" + 
					edge.getStartNode().getData() + (edge.isBothWays() ? " <-> " : " -> ") + 
					edge.getEndNode().getData() + ")");
		}
	}
}
