package utopia.flow.generics;

import java.util.ArrayList;
import java.util.List;

import utopia.flow.generics.ValueParser.ValueParseException;
import utopia.flow.structure.Graph;
import utopia.flow.structure.GraphEdge;
import utopia.flow.structure.GraphNode;
import utopia.flow.structure.ImmutableList;
import utopia.flow.structure.ImmutableMap;
import utopia.flow.structure.Pair;
import utopia.flow.util.Lazy;
import utopia.flow.util.Option;

/**
 * This class handles data type casting and finds the optimal ways to cast a value of a 
 * certain data type to another.
 * @author Mikko Hilpinen
 * @since 27.11.2015
 */
public class ConversionGraph
{
	// ATTRIBUTES	-------------------
	
	private Graph<DataType, Pair<ValueParser, ConversionReliability>> conversionGraph = new Graph<>();
	private ImmutableMap<Pair<DataType, DataType>, ConversionRoute> optimalConversions = ImmutableMap.empty();
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Adds each possible conversion of a value parser to the graph (where suitable)
	 * @param parser A parser that will be used by this graph
	 */
	public void addParser(ValueParser parser)
	{
		parser.getConversions().forEach(c -> addConversion(c.getSourceType(), c.getTargetType(), parser, c.getReliability()));
	}
	
	/**
	 * Adds a new conversion between the two data types
	 * @param from The data type the values are cast from
	 * @param to The data type the values are cast to
	 * @param parser The parser that does the actual casting
	 * @param reliability The reliability of the parser
	 */
	public void addConversion(DataType from, DataType to, ValueParser parser, ConversionReliability reliability)
	{
		if (from == null || to == null || parser == null || reliability == null)
			return;
		
		// Clears any previous optimal conversions, since they may be changed
		this.optimalConversions = ImmutableMap.empty();
		
		// Finds the existing data type nodes (where possible)
		ImmutableList<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> fromNodes = 
				findDataTypeNodes(from, false, false);
		ImmutableList<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> toNodes = 
				findDataTypeNodes(to, false, false);
		
		GraphNode<DataType, Pair<ValueParser, ConversionReliability>> toNode, fromNode;
		
		// If there was no target node before, adds a new one
		if (toNodes.isEmpty())
		{
			toNode = new GraphNode<>(to);
			this.conversionGraph.addNode(toNode);
		}
		else
			toNode = toNodes.head();
		
		// If there was no source node before, adds a new one and connects it to the target 
		// node
		if (fromNodes.isEmpty())
		{
			fromNode = new GraphNode<>(from);
			this.conversionGraph.addNode(fromNode);
			fromNode.addEdge(new GraphEdge<>(new Pair<>(parser, reliability), toNode));
		}
		// Otherwise may replace a connection between the two nodes, if better (or equal) 
		// reliability is offered
		else
		{
			fromNode = fromNodes.head();
			GraphEdge<DataType, Pair<ValueParser, ConversionReliability>> previousConnection = 
					fromNode.getConnectingEdge(toNode);
			if (previousConnection == null || 
					!previousConnection.getContent().getSecond().isBetterThan(reliability))
				fromNode.addEdge(createEdge(parser, reliability, toNode));
		}
	}
	
	/**
	 * Parses an object of a data type into another data type, possibly through multiple 
	 * casting operations. May parse the object to any subtype of the target data type
	 * @param value The value that will be cast
	 * @param to The desired data type of the value
	 * @return The new value with the new data type
	 */
	public Value cast(Value value, DataType to)
	{
		if (value == null)
			return null;
		if (value.getType().equals(to))
			return value;
		if (value.isNull())
			return Value.NullValue(to);
		
		SubTypeSet targetTypes = new SubTypeSet(to);
		DataType singleTarget = targetTypes.getSingularType();
		
		// TODO: Refactor once SubTypeSet has been refactored
		if (singleTarget == null)
			return cast(value, targetTypes);
		else
			return castNoSubTypes(value, singleTarget);
	}
	
	private Value castNoSubTypes(Value value, DataType to)
	{		
		return findOptimalRouteBetween(value.getType(), to).getOrFail(
				() -> new ValueParser.ValueParseException(value, to)).cast(value);
	}
	
	/**
	 * Parses the value to one of the target data types, whichever conversion is the most 
	 * reliable
	 * @param value The source value
	 * @param to The target data types
	 * @return a value parsed from the source value into one of the target data types
	 * @throws ValueParseException If the value can't be parsed into any of the target types 
	 * or if the conversion fails.
	 */
	public Value cast(Value value, SubTypeSet to) throws ValueParseException
	{
		if (value == null)
			return null;
		if (to.contains(value.getType()))
			return value;
		
		DataType optimalTarget = findOptimalTargetType(value.getType(), to).getOrFail(
				() -> new ValueParser.ValueParseException(value));
		
		return castNoSubTypes(value, optimalTarget);
	}
	
	/**
	 * Finds the optimal target data type for a conversion between two data types
	 * @param from The source data type
	 * @param to The target data types
	 * @return The target data types which the source data type can be cast from most reliably. 
	 * None if the source data type can't be converted to any of the target data types.
	 */
	public Option<DataType> findOptimalTargetType(DataType from, SubTypeSet to)
	{
		// Finds the optimal target data type (lowest defined cost)
		return ImmutableList.of(to).flatMap(type -> getConversionCost(from, type).map(
				cost -> new Pair<>(type, cost))).sortedBy(p -> p.getSecond()).headOption().map(p -> p.getFirst());
	}
	
	/**
	 * Calculates the conversion reliability of casting a value from one data type to 
	 * another
	 * @param from The source value data type
	 * @param to The target data type
	 * @return How reliable the conversion is. None if the conversion is impossible. In a 
	 * perfect conversion, each separate conversion is perfect.
	 */
	public Option<ConversionReliability> getConversionReliability(DataType from, DataType to)
	{
		if (from == null || to == null)
			return Option.none();
		
		if (from.equals(to))
			return Option.some(ConversionReliability.NO_CONVERSION);
		
		return findOptimalRouteBetween(from, to).map(r -> r.getReliability());
	}
	
	/**
	 * Calculates the 'cost' of casting a value from one data type to another
	 * @param from The source value data type
	 * @param to The target data type
	 * @return The cost of the conversion. The higher value, the more changes the value 
	 * will experience. None is returned for impossible conversions.
	 */
	public Option<Integer> getConversionCost(DataType from, DataType to)
	{
		if (from == null || to == null)
			return Option.none();
		
		if (from.equals(to))
			return Option.some(0);
		
		return findOptimalRouteBetween(from, to).map(r -> r.getCost());
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
		
		return findOptimalRouteBetween(from, to).isDefined();
	}
	
	/**
	 * @return All the possible single cast conversions available through this graph. This 
	 * does not include subtype conversions.
	 */
	public ImmutableList<Conversion> getPossibleConversions()
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
		
		return ImmutableList.of(conversions);
	}
	
	private Option<ConversionRoute> findOptimalRouteBetween(DataType from, DataType to)
	{
		if (from == null || to == null)
			return Option.none();
		
		// If the conversion is not necessary, returns an empty list
		if (DataTypes.dataTypeIsOfType(from, to))
			return Option.some(ConversionRoute.EMPTY);
		
		// Checks if the optimal conversion check has already been made
		Pair<DataType, DataType> cast = new Pair<>(from, to);
		if (this.optimalConversions.containsKey(cast))
			return this.optimalConversions.getOption(cast);
		
		ImmutableList<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> fromNodes = 
				findDataTypeNodes(from, false, true);
		ImmutableList<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> toNodes = 
				findDataTypeNodes(to, true, false);
		
		// If either of the nodes can't be found from this graph, fails
		if (fromNodes.isEmpty() || toNodes.isEmpty())
			return Option.none();
		
		// First finds all conversion routes possible
		List<List<GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>>> routes = new ArrayList<>();
		for (GraphNode<DataType, Pair<ValueParser, ConversionReliability>> fromNode : fromNodes)
		{
			for (GraphNode<DataType, Pair<ValueParser, ConversionReliability>> toNode : toNodes)
			{
				List<? extends List<GraphEdge<DataType, Pair<ValueParser, 
						ConversionReliability>>>> someRoutes = fromNode.findConnectingRoutes(toNode);
				if (someRoutes != null)
					routes.addAll(someRoutes);
			}
		}
		
		// If there are no possible conversions, fails
		if (routes.isEmpty())
			return Option.none();
		
		// If there is only a single route, uses that
		if (routes.size() == 1)
		{
			ConversionRoute route = parseRoute(routes.get(0), from);
			this.optimalConversions = this.optimalConversions.plus(cast, route);
			return Option.some(route);
		}
		// Otherwise finds the one with the smallest cost (most reliable)
		else
		{
			List<GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>> bestRoute = 
					ImmutableList.of(routes).minBy(ConversionGraph::calculateRouteCost).get();
			
			ConversionRoute route = parseRoute(bestRoute, from);
			this.optimalConversions = this.optimalConversions.plus(cast, route);
			return Option.some(route);
		}
	}
	
	private ImmutableList<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> 
			findDataTypeNodes(DataType type, boolean subTypesIncluded, boolean superTypesIncluded)
	{
		if (!subTypesIncluded && !superTypesIncluded)
			return this.conversionGraph.findNodes(type);
		
		ImmutableList<GraphNode<DataType, Pair<ValueParser, ConversionReliability>>> nodes = this.conversionGraph.findNodes(type);
		
		// Finds the nodes for the type. If There were none, tries for the subtypes, then 
		// their subtypes and so on
		if (subTypesIncluded)
			nodes = nodes.plus(DataTypes.getInstance().getSubTypesFor(type, false).flatMap(this.conversionGraph::findNodes));
		
		// May also add some supertypes
		if (superTypesIncluded)
			nodes = nodes.plus(DataTypes.getInstance().getSuperTypesFor(type, false).flatMap(this.conversionGraph::findNodes));
		
		return nodes;
	}
	
	private static ConversionRoute parseRoute(Iterable<GraphEdge<DataType, 
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
		
		return new ConversionRoute(ImmutableList.of(parsed));
	}
	
	private static int calculateRouteCost(
			Iterable<GraphEdge<DataType, Pair<ValueParser, ConversionReliability>>> route)
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
		return new GraphEdge<>(new Pair<>(parser, reliability), targetNode);
	}
	
	private static class ConversionRoute
	{
		// ATTRIBUTES	-----------------
		
		public static final ConversionRoute EMPTY = new ConversionRoute(ImmutableList.empty());
		
		private final ImmutableList<Pair<Conversion, ValueParser>> steps;
		private final Lazy<Integer> cost;
		private final Lazy<ConversionReliability> reliability;
		
		
		// CONSTRUCTOR	----------------
		
		public ConversionRoute(ImmutableList<Pair<Conversion, ValueParser>> steps)
		{
			this.steps = steps;
			this.cost = new Lazy<>(() -> calculateCostFrom(this.steps));
			this.reliability = new Lazy<>(() -> calculateReliabilityFrom(this.steps));
		}
		
		
		// ACCESSORS	-----------------
		
		public int getCost()
		{
			return this.cost.get();
		}
		
		public ConversionReliability getReliability()
		{
			return this.reliability.get();
		}
		
		
		// OTHER METHODS	-------------------
		
		public Value cast(Value value)
		{
			Value castValue = value;
			for (Pair<Conversion, ValueParser> step : this.steps)
			{
				castValue = step.getSecond().cast(castValue, step.getFirst().getTargetType());
			}
			
			return castValue;
		}
		
		private static ConversionReliability calculateReliabilityFrom(ImmutableList<Pair<Conversion, ValueParser>> steps)
		{
			ConversionReliability reliability = ConversionReliability.NO_CONVERSION;
			for (Pair<Conversion, ValueParser> step : steps)
			{
				ConversionReliability stepReliability = step.getFirst().getReliability();
				if (reliability.isBetterThan(stepReliability))
				{
					reliability = stepReliability;
					if (stepReliability == ConversionReliability.DANGEROUS)
						break;
				}
			}
			
			return reliability;
		}
		
		private static int calculateCostFrom(ImmutableList<Pair<Conversion, ValueParser>> steps)
		{
			return steps.map(s -> s.getFirst().getReliability().getCost()).reduceOption(
					(total, cost) -> total + cost).getOrElse(0);
		}
	}
}
