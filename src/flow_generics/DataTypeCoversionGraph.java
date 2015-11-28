package flow_generics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flow_structure.Graph;
import flow_structure.GraphEdge;
import flow_structure.GraphNode;
import flow_structure.Pair;

/**
 * This class handles data type casting and finds the optimal ways to cast a value of a 
 * certain data type to another.
 * @author Mikko Hilpinen
 * @since 27.11.2015
 */
public class DataTypeCoversionGraph
{
	// ATTRIBUTES	-------------------
	
	private Graph<DataType, Pair<ValueParser, ConversionReliability>> conversionGraph;
	private Map<Pair<DataType, DataType>, List<Pair<Conversion, ValueParser>>> optimalConversions;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new data type conversion graph
	 */
	public DataTypeCoversionGraph()
	{
		this.conversionGraph = new Graph<>();
		this.optimalConversions = new HashMap<>();
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Adds a new conversion between the two data types
	 * @param from The data type the values are cast from
	 * @param to The data type the values are cast to
	 * @param parser The parser that does the actual casting
	 * @param reliability The reliability of the parser
	 */
	public void addConversion(DataType from, DataType to, ValueParser parser, 
			ConversionReliability reliability)
	{
		if (from == null || to == null || parser == null || reliability == null)
			return;
		
		// Clears any previous optimal conversions, since they may be changed
		this.optimalConversions.clear();
		
		// Finds the existing data type nodes (where possible)
		GraphNode<DataType, Pair<ValueParser, ConversionReliability>> fromNode = 
				findDataTypeNode(from);
		GraphNode<DataType, Pair<ValueParser, ConversionReliability>> toNode = 
				findDataTypeNode(to);
		
		// If there was no target node before, adds a new one
		if (toNode == null)
		{
			toNode = new GraphNode<>(to);
			this.conversionGraph.addNode(toNode);
		}
		
		// If there was no source node before, adds a new one and connects it to the target 
		// node
		if (fromNode == null)
		{
			fromNode = new GraphNode<>(from);
			this.conversionGraph.addNode(fromNode);
			fromNode.addEdge(new GraphEdge<>(new Pair<>(parser, reliability), toNode));
		}
		// Otherwise may replace a connection between the two nodes, if better reliability is 
		// offered
		else
		{
			GraphEdge<DataType, Pair<ValueParser, ConversionReliability>> previousConnection = 
					fromNode.getConnectingEdge(toNode);
			if (previousConnection == null || reliability.isBetterThan(
					previousConnection.getContent().getSecond()))
				fromNode.addEdge(createEdge(parser, reliability, toNode));
		}
	}
	
	/**
	 * Parses an object of a data type into another data type, possibly through multiple 
	 * casting operations
	 * @param value The value that will be cast
	 * @param from The original data type of the value
	 * @param to The desired data type of the value
	 * @return The new value with the new data type
	 */
	public Object parse(Object value, DataType from, DataType to)
	{
		if (value == null)
			return null;
		
		if (from.equals(to))
			return value;
		
		List<? extends Pair<Conversion, ValueParser>> conversions = 
				findOptimalConversionsBetween(from, to);
		if (conversions == null)
			throw new ValueParser.ValueParseException(value, from, to);
		
		Object castValue = value;
		for (Pair<Conversion, ValueParser> conversion : conversions)
		{
			castValue = conversion.getSecond().parse(castValue, 
					conversion.getFirst().getSourceType(), 
					conversion.getFirst().getTargetType());
		}
		
		return castValue;
	}
	
	private List<? extends Pair<Conversion, ValueParser>> findOptimalConversionsBetween(
			DataType from, DataType to)
	{
		if (from == null || to == null)
			return null;
		
		// If the data types are already equal, returns an empty list
		if (from.equals(to))
			return new ArrayList<>();
		
		// Checks if the optimal conversion check has already been made
		Pair<DataType, DataType> cast = new Pair<>(from, to);
		if (this.optimalConversions.containsKey(cast))
		{
			return this.optimalConversions.get(cast);
		}
		
		GraphNode<DataType, Pair<ValueParser, ConversionReliability>> fromNode = 
				findDataTypeNode(from);
		GraphNode<DataType, Pair<ValueParser, ConversionReliability>> toNode = 
				findDataTypeNode(to);
		
		// If either of the nodes can't be found from this graph, fails
		if (fromNode == null || toNode == null)
			return null;
		
		// First finds all conversion routes possible
		List<? extends List<GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>>> 
				routes = fromNode.findConnectingRoutes(toNode);
		
		// If there are no possible conversions, fails
		if (routes == null)
			return null;
		
		// If there is only a single route, uses that
		if (routes.size() == 1)
		{
			List<Pair<Conversion, ValueParser>> route = parseRoute(routes.get(0), from);
			this.optimalConversions.put(cast, route);
			return route;
		}
		// Otherwise finds the one with the smallest cost (most reliable)
		else
		{
			List<GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>> bestRoute = null;
			int bestCost = -1;
			for (List<GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>> route : routes)
			{
				int cost = calculateRouteCost(route);
				if (bestRoute == null || cost < bestCost)
				{
					bestRoute = route;
					bestCost = cost;
				}
			}
			
			List<Pair<Conversion, ValueParser>> route = parseRoute(bestRoute, from);
			this.optimalConversions.put(cast, route);
			return route;
		}
	}
	
	private GraphNode<DataType, Pair<ValueParser, ConversionReliability>> findDataTypeNode(DataType type)
	{
		List<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> nodes = 
				this.conversionGraph.findNodes(type);
		if (nodes.isEmpty())
			return null;
		else
			return nodes.get(0);
	}
	
	private static List<Pair<Conversion, ValueParser>> parseRoute(List<
			GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>> route, DataType startType)
	{
		List<Pair<Conversion, ValueParser>> parsed = new ArrayList<>();
		
		DataType lastType = startType;
		for (GraphEdge<DataType, Pair<ValueParser, ConversionReliability>> edge : route)
		{
			DataType nextType = edge.getEndNode().getContent();
			parsed.add(new Pair<>(new Conversion(lastType, nextType, 
					edge.getContent().getSecond()), edge.getContent().getFirst()));
			lastType = nextType;
		}
		
		return parsed;
	}
	
	private static int calculateRouteCost(List<
			GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>> route)
	{
		int cost = 0;
		for (GraphEdge<DataType, Pair<ValueParser, ConversionReliability>> edge : route)
		{
			cost += edge.getContent().getSecond().getCost();
		}
		
		return cost;
	}
	
	private static GraphEdge<DataType, Pair<ValueParser, ConversionReliability>> createEdge(
			ValueParser parser, ConversionReliability reliability, 
			GraphNode<DataType, Pair<ValueParser, ConversionReliability>> targetNode)
	{
		return new GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>(
				new Pair<>(parser, reliability), targetNode);
	}
}
