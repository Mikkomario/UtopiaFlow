package flow_test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import flow_io.FileOutputAccessor;
import flow_io.XMLIOAccessor;
import flow_recording.ObjectFormatException;
import flow_recording.ObjectParser;
import flow_recording.TextConstructorInstructor;
import flow_recording.TextObjectWriter;
import flow_recording.Writable;
import flow_recording.XMLConstructorInstructor;
import flow_recording.XMLObjectWriter;
import flow_structure.Graph;
import flow_structure.GraphEdge;
import flow_structure.GraphNode;
import flow_structure.GraphRecording;

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
		System.out.println("Edge 101: " + graph.findEdgeWithData(101).getID());
		
		
		// Creates a recording of the graph
		GraphRecording<String, Integer> recording = new GraphRecording<>(
				new ObjectParser.StringParser(), new XMLFriendlyIntegerParser());
		recording.record(graph);
		
		// Prints the recording
		System.out.println("The recorded graph: ");
		printGraph(recording.toGraph());
		
		// Saves the graph into a text file
		// Saves the graph into an xml stream
		TextObjectWriter textWriterObject = new TextObjectWriter();
		XMLObjectWriter xmlWriterObject = new XMLObjectWriter();
		BufferedWriter textWriter = FileOutputAccessor.openFile("GraphTest.txt");
		ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
		XMLStreamWriter xmlWriter = null;

		try
		{
			xmlWriter = XMLIOAccessor.createWriter(xmlStream);
			xmlWriterObject.openDocument("root", xmlWriter);
			
			for (Writable writable : recording.getWritableContent())
			{
				textWriterObject.writeInto(writable, textWriter);
				xmlWriterObject.writeInto(writable, xmlWriter);
			}
			
			xmlWriterObject.closeDocument(xmlWriter);
		}
		catch (UnsupportedEncodingException | XMLStreamException e)
		{
			System.err.println("Graph writing failed");
			e.printStackTrace();
		}
		finally
		{
			FileOutputAccessor.closeWriter(textWriter);
			XMLIOAccessor.closeWriter(xmlWriter);
			recording.reset();
		}
		
		// Prints the xml
		byte[] xml = xmlStream.toByteArray();
		System.out.println("Graph xml: " + new String(xml));
		
		
		// Reads and prints the copies
		try
		{
			new TextConstructorInstructor(recording.createConstructor()).constructFromFile(
					"GraphTest.txt", "*");
			System.out.println("Graph constructed from file: ");
			printGraph(recording.toGraph());
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Couldn't find graph file");
			e.printStackTrace();
		}
		finally
		{
			recording.reset();
		}
		
		try
		{
			new XMLConstructorInstructor(recording.createConstructor()).constructFrom(
					new ByteArrayInputStream(xml));
			System.out.println("Graph constructed from xml: ");
			printGraph(recording.toGraph());
		}
		catch (UnsupportedEncodingException | XMLStreamException e)
		{
			System.out.println("Couldn't read graph xml");
			e.printStackTrace();
		}
		finally
		{
			recording.reset();
		}
		
		
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
			graph.connectNodes(startNodeID, endNodeID, i, false, true);
		}
		graph.connectNodes(nodeIDs.get(0), nodeIDs.get(1), 100, false, true);
		graph.connectNodes(nodeIDs.get(1), nodeIDs.get(0), 101, false, true);
		graph.connectNodes(nodeIDs.get(2), nodeIDs.get(3), 102, true, true);
		
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
	
	
	// SUBCLASSES	---------------------
	
	private static class XMLFriendlyIntegerParser implements ObjectParser<Integer>
	{
		@Override
		public String parseToString(Integer object)
		{
			return "INT" + object;
		}

		@Override
		public Integer parseFromString(String s) throws ObjectFormatException
		{
			return Integer.parseInt(s.substring(3));
		}	
	}
}
