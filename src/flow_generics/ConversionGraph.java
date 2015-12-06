package flow_generics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flow_generics.ValueParser.ValueParseException;
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
public class ConversionGraph
{
	// ATTRIBUTES	-------------------
	
	private Graph<DataType, Pair<ValueParser, ConversionReliability>> conversionGraph;
	private Map<Pair<DataType, DataType>, ConversionRoute> optimalConversions;
	
	
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new data type conversion graph
	 */
	public ConversionGraph()
	{
		this.conversionGraph = new Graph<>();
		this.optimalConversions = new HashMap<>();
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Adds each possible conversion of a value parser to the graph (where suitable)
	 * @param parser A parser that will be used by this graph
	 */
	public void addParser(ValueParser parser)
	{
		for (Conversion conversion : parser.getConversions())
		{
			addConversion(conversion.getSourceType(), conversion.getTargetType(), parser, 
					conversion.getReliability());
		}
	}
	
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
		
		ConversionRoute route = findOptimalRouteBetween(from, to);
		if (route == null)
			throw new ValueParser.ValueParseException(value, from, to);
		
		return route.cast(value);
	}
	
	/**
	 * Parses the value to one of the target data types, whichever conversion is the most 
	 * reliable
	 * @param value The source value
	 * @param from The data type of the source value
	 * @param to The target data types
	 * @return a value parsed from the source value into one of the target data types
	 * @throws ValueParseException If the value can't be parsed into any of the target types 
	 * or if the conversion fails.
	 */
	public Value parse(Object value, DataType from, Iterable<? extends DataType> to) throws 
			ValueParseException
	{
		DataType optimalTarget = findOptimalTargetType(from, to);
		
		if (optimalTarget == null)
			throw new ValueParser.ValueParseException(value, from);
		else
			return new Value(parse(value, from, optimalTarget), optimalTarget);
	}
	
	/**
	 * Finds the optimal target data type for a conversion between two data types
	 * @param from The source data type
	 * @param to The target data types
	 * @return The target data types which the source data type can be cast from most reliably. 
	 * Null if the source data type can't be converted to any of the target data types.
	 */
	public DataType findOptimalTargetType(DataType from, Iterable<? extends DataType> to)
	{
		// Finds the optimal target data type
		DataType optimalTarget = null;
		int smallestCost = -1;
		
		for (DataType type : to)
		{
			int cost = getConversionCost(from, type);
			if (cost >= 0 && (optimalTarget == null || cost < smallestCost))
				optimalTarget = type;
		}
		
		return optimalTarget;
	}
	
	/**
	 * Calculates the conversion reliability of casting a value from one data type to 
	 * another
	 * @param from The source value data type
	 * @param to The target data type
	 * @return How reliable the conversion is. Null if the conversion is impossible. In a 
	 * perfect conversion, each separate conversion is perfect.
	 */
	public ConversionReliability getConversionReliability(DataType from, DataType to)
	{
		if (from == null || to == null)
			return null;
		
		if (from.equals(to))
			return ConversionReliability.PERFECT;
		
		ConversionRoute route = findOptimalRouteBetween(from, to);
		if (route == null)
			return null;
		else
			return route.getReliability();
	}
	
	/**
	 * Calculates the 'cost' of casting a value from one data type to another
	 * @param from The source value data type
	 * @param to The target data type
	 * @return The cost of the conversion. The higher value, the more changes the value 
	 * will experience. A negative number is returned for impossible conversions.
	 */
	public int getConversionCost(DataType from, DataType to)
	{
		if (from == null || to == null)
			return -1;
		
		if (from.equals(to))
			return 0;
		
		ConversionRoute route = findOptimalRouteBetween(from, to);
		if (route == null)
			return -1;
		else
			return route.getCost();
	}
	
	/**
	 * Finds out whether a conversion between the two values is possible
	 * @param from The source value data type
	 * @param to The target data type
	 * @return Is the conversion between the two data types possible using this graph
	 */
	public boolean conversionIsPossible(DataType from, DataType to)
	{
		if (from == null || to == null)
			return false;
		
		if (from.equals(to))
			return true;
		
		return findOptimalRouteBetween(from, to) != null;
	}
	
	/**
	 * @return All the possible single cast conversions available through this graph
	 */
	public List<Conversion> getPossibleConversions()
	{
		List<Conversion> conversions = new ArrayList<>();
		for (GraphNode<DataType, Pair<ValueParser, ConversionReliability>> node : 
			this.conversionGraph.getNodes())
		{
			DataType startType = node.getContent();
			for (GraphEdge<DataType, Pair<ValueParser, ConversionReliability>> edge : 
				node.getLeavingEdges())
			{
				conversions.add(new Conversion(startType, edge.getEndNode().getContent(), 
						edge.getContent().getSecond()));
			}
		}
		
		return conversions;
	}
	
	private ConversionRoute findOptimalRouteBetween(DataType from, DataType to)
	{
		if (from == null || to == null)
			return null;
		
		// If the data types are already equal, returns an empty list
		if (from.equals(to))
			return new ConversionRoute(new ArrayList<>());
		
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
			ConversionRoute route = parseRoute(routes.get(0), from);
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
			
			ConversionRoute route = parseRoute(bestRoute, from);
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
	
	private static ConversionRoute parseRoute(List<GraphEdge<DataType, 
			Pair<ValueParser, ConversionReliability>>> route, DataType startType)
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
		
		return new ConversionRoute(parsed);
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
	
	private static class ConversionRoute
	{
		// ATTRIBUTES	-----------------
		
		private List<Pair<Conversion, ValueParser>> steps;
		private int cost;
		private ConversionReliability reliability;
		
		
		// CONSTRUCTOR	----------------
		
		public ConversionRoute(List<Pair<Conversion, ValueParser>> steps)
		{
			this.steps = steps;
			this.cost = -1;
			this.reliability = null;
		}
		
		
		// ACCESSORS	-----------------
		
		public int getCost()
		{
			if (this.cost < 0)
			{
				this.cost = 0;
				for (Pair<Conversion, ValueParser> step : this.steps)
				{
					this.cost += step.getFirst().getReliability().getCost();
				}
			}
			
			return this.cost;
		}
		
		public ConversionReliability getReliability()
		{
			if (this.reliability == null)
			{
				this.reliability = ConversionReliability.PERFECT;
				for (Pair<Conversion, ValueParser> step : this.steps)
				{
					ConversionReliability stepReliability = step.getFirst().getReliability();
					if (this.reliability.isBetterThan(stepReliability))
					{
						this.reliability = stepReliability;
						if (stepReliability == ConversionReliability.DANGEROUS)
							break;
					}
				}
			}
			
			return this.reliability;
		}
		
		
		// OTHER METHODS	-------------------
		
		public Object cast(Object source)
		{
			Object castValue = source;
			for (Pair<Conversion, ValueParser> step : this.steps)
			{
				castValue = step.getSecond().parse(castValue, step.getFirst().getSourceType(), 
						step.getFirst().getTargetType());
			}
			
			return castValue;
		}
	}
}
