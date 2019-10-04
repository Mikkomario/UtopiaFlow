package utopia.java.flow.generics;

import java.util.ArrayList;
import java.util.List;

import utopia.java.flow.generics.ValueOperation.ValueOperationException;
import utopia.java.flow.io.BasicElementValueParser;
import utopia.java.flow.io.ElementValueParser;
import utopia.java.flow.parse.XmlElement;
import utopia.java.flow.structure.ImmutableList;
import utopia.java.flow.structure.ImmutableMap;
import utopia.java.flow.structure.Lazy;
import utopia.java.flow.structure.Option;
import utopia.java.flow.structure.Pair;
import utopia.java.flow.structure.TreeNode;

/**
 * This static interface keeps track of the different data type hierarchies, etc.
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public class DataTypes implements ValueParser
{
	// ATTRIBUTES	------------------
	
	private static final Lazy<DataTypes> INSTANCE = new Lazy<>(DataTypes::new);
	
	private ImmutableList<DataTypeTreeNode> dataTypes = ImmutableList.empty();
	private ConversionGraph graph = new ConversionGraph();
	@SuppressWarnings("deprecation")
	private ImmutableList<ValueOperator> operators = ImmutableList.empty();
	@SuppressWarnings("deprecation")
	private ImmutableMap<DataType, ElementValueParser> specialElementParsers = ImmutableMap.empty();
	
	
	// CONSTRUCTOR	------------------
	
	@SuppressWarnings("deprecation") // Operators added for backwards compatibility
	private DataTypes()
	{
		// Adds the object type
		DataTypeTreeNode objectNode = new DataTypeTreeNode(BasicDataType.OBJECT);
		add(objectNode);
		
		// Initialises the rest of the basic data types, and adds them under the object type
		this.dataTypes = this.dataTypes.plus(
				BasicDataType.VALUES.without(BasicDataType.OBJECT).map(type -> new DataTypeTreeNode(type, objectNode)));
		
		// Connects number types
		DataTypeTreeNode number = get(BasicDataType.NUMBER);
		get(BasicDataType.INTEGER).setParent(number);
		get(BasicDataType.DOUBLE).setParent(number);
		get(BasicDataType.LONG).setParent(number);
		get(BasicDataType.FLOAT).setParent(number);
		
		// Adds parsing between the super types
		addParser(new SuperTypeParser());
		
		// Adds parsing support for basic data types
		addParser(BasicValueParser.getInstance());
		
		// Adds operators for basic data types
		addOperator(BasicPlusOperator.getInstance());
		addOperator(BasicMinusOperator.getInstance());
		addOperator(BasicMultiplyOperator.getInstance());
		addOperator(BasicDivideOperator.getInstance());
		
		// Introduces basic element parser
		introduceSpecialParser(new BasicElementValueParser());
	}
	
	/**
	 * @return The data types instance
	 */
	public static DataTypes getInstance()
	{
		return INSTANCE.get();
	}
	
	
	// IMPLEMENTED METHODS	----------
	
	@Override
	public Value cast(Value value, DataType to) throws ValueParseException
	{	
		if (value == null)
			return null;
		return this.graph.cast(value, to);
	}
	
	@Override
	public ImmutableList<Conversion> getConversions()
	{
		return this.graph.getPossibleConversions();
	}
	
	
	// OTHER METHODS	--------------
	
	/**
	 * Parses an value to one of the provided data types
	 * @param value The source value
	 * @param to The target data type(s)
	 * @return The object value parsed to one of the provided data types
	 * @throws ValueParseException If the parsing failed
	 */
	public Value cast(Value value, SubTypeSet to) throws ValueParseException
	{
		return this.graph.cast(value, to);
	}
	
	/**
	 * Performs a value operation on two values
	 * @param first The first value
	 * @param operation The operation performed on the two values
	 * @param second The second value
	 * @return The result value of the operation
	 * @throws ValueOperationException If the operation couldn't be performed or it failed
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public Value operate(Value first, ValueOperation operation, Value second) throws ValueOperationException
	{
		if (first == null)
			throw new ValueOperationException(operation, null, second);
		if (second == null)
			return first;
		
		// Finds the suitable operator(s)
		List<ValueOperator> operators = new ArrayList<>();
		for (ValueOperator operator : this.operators)
		{
			if (operator.getOperation().equals(operation))
				operators.add(operator);
		}
		
		if (operators.isEmpty())
			throw new ValueOperation.ValueOperationException(
					"No operator provided for operation " + operation);
		
		// Finds the compatible target data types (and their operators)
		SubTypeSet targetTypes = new SubTypeSet();
		for (ValueOperator operator : operators)
		{
			for (Pair<DataType, DataType> parameterTypes : operator.getPossibleParameterTypes())
			{
				if (parameterTypes.getFirst().equals(first.getType()))
				{
					// If a straight match is found, uses that
					if (parameterTypes.getSecond().equals(second.getType()))
						return operator.operate(first, second);
					
					targetTypes.add(parameterTypes.getSecond());
				}
			}
		}
		
		// Casts the second value to a compatible data type
		Value casted;
		try
		{
			casted = cast(second, targetTypes);
		}
		catch (DataTypeException e)
		{
			throw new ValueOperation.ValueOperationException(operation, first, second, e);
		}
		
		// Finds an operator that is willing to accept the parameters
		for (ValueOperator operator : operators)
		{
			if (operatorSupportsDataTypes(operator, first.getType(), casted.getType()))
					return operator.operate(first, casted);
		}
		
		throw new ValueOperation.ValueOperationException(operation, first, second);
	}
	
	/**
	 * Checks if the first data type belongs to the second data type
	 * @param type The first data type
	 * @param other The second data type
	 * @return Does the first data type belong to the second data type
	 * @throws DataTypeNotIntroducedException If the first data type hasn't been introduced
	 */
	public static boolean dataTypeIsOfType(DataType type, DataType other) throws DataTypeNotIntroducedException
	{
		if (type.equals(other))
			return true;
		
		return getInstance().get(type).isOfType(other);
	}
	
	/**
	 * Finds the node version of a single data type from the introduced data types
	 * @param type The data type that is requested
	 * @return The data type node of that data type
	 * @throws DataTypeNotIntroducedException If the provided data type hasn't been introduced 
	 * yet
	 * @see #add(DataTypeTreeNode)
	 */
	public DataTypeTreeNode get(DataType type) throws DataTypeNotIntroducedException
	{
		return getNode(type).getOrFail(() -> new DataTypeNotIntroducedException(type));
	}
	
	/**
	 * @return Each data type that has been introduced at this point
	 */
	public ImmutableList<DataType> getIntroducedDataTypes()
	{
		return this.dataTypes.map(TreeNode::getContent);
	}
	
	/**
	 * Finds all the data types that are can be treated as the provided data type. For example, 
	 * would return Number, Integer, Double and Long for Number when only basic data types 
	 * are introduced.
	 * @param type a data type
	 * @param includeType Should the provided data type be included in the response
	 * @return All data types that count as the provided data type
	 */
	public ImmutableList<DataType> getSubTypesFor(DataType type, boolean includeType)
	{
		DataTypeTreeNode node = get(type);
		// TODO: Refactor once treenode has been refactored
		ImmutableList<DataType> lowerTypes = ImmutableList.of(node.getLowerNodes()).map(TreeNode::getContent);
		
		if (includeType)
			return lowerTypes.prepend(type);
		else
			return lowerTypes;
	}
	
	/**
	 * Finds all the data types that are considered super types for the data type. For example 
	 * calling this method for a double would result in a list including the data type number.
	 * @param type A data type
	 * @param includeType Should the provided data type be included in the response
	 * @return All super types of this data type
	 */
	public ImmutableList<DataType> getSuperTypesFor(DataType type, boolean includeType)
	{
		DataTypeTreeNode node = get(type);
		
		// TODO: Refactor after treenode refactored
		ImmutableList<DataType> higherTypes = ImmutableList.of(node.getHigherNodes()).map(TreeNode::getContent);
		
		if (includeType)
			return higherTypes.prepend(type);
		else
			return higherTypes;
	}
	
	/**
	 * Checks if the provided data type has been introduced
	 * @param type The data type
	 * @return Has the data type been introduced yet
	 */
	public boolean contains(DataType type)
	{
		return getNode(type).isDefined();
	}
	
	/**
	 * Introduces a new data type
	 * @param dataTypeNode The treeNode for the data type. The node should be connected 
	 * to other associated (hierarchically) data type nodes.
	 * @see #get(DataType)
	 * @see #updateParsingToSuperTypes()
	 */
	public void add(DataTypeTreeNode dataTypeNode)
	{
		if (!this.dataTypes.contains(dataTypeNode))
			this.dataTypes = this.dataTypes.overwrite(dataTypeNode, (a, b) -> a.getContent().equals(b.getContent()));
	}
	
	/**
	 * Adds a new parser to the parsers available to the class for value parsing
	 * @param parser The parser that should be used
	 */
	public void addParser(ValueParser parser)
	{
		if (parser == null || parser.equals(this))
			return;
		
		this.graph.addParser(parser);
	}
	
	/**
	 * This method rechecks the relations between the data types and adds automatic casting 
	 * from a subtype to its super type. This method should be called after the data types 
	 * have been introduced.
	 */
	public void updateParsingToSuperTypes()
	{
		addParser(new SuperTypeParser());
	}
	
	/**
	 * Adds a new value operator to the available value operators
	 * @param operator A value operator that could be used
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	public void addOperator(ValueOperator operator)
	{
		this.operators = this.operators.with(operator);
	}
	
	/**
	 * Calculates the reliability of a conversion between two data types
	 * @param from The source data type
	 * @param to The target data type
	 * @return How reliable the conversion is. None if the conversion is impossible.
	 */
	public Option<ConversionReliability> getConversionReliability(DataType from, DataType to)
	{
		return this.graph.getConversionReliability(from, to);
	}
	
	/**
	 * Finds the data type represented by the string
	 * @param s a string representing a data type
	 * @return The data type represented by the string. None if the string doesn't represent a known data type
	 */
	public static Option<DataType> typeFor(String s)
	{
		return DataTypes.getInstance().dataTypes.find(n -> n.getContent().toString().equalsIgnoreCase(s)).map(
				TreeNode::getContent);
	}
	
	/**
	 * Finds the data type represented by the string
	 * @param s a string representing a data type
	 * @return The data type represented by the string
	 * @throws DataTypeNotIntroducedException if the string doesn't represent an introduced 
	 * data type
	 */
	public static DataType parseType(String s) throws DataTypeNotIntroducedException
	{
		return typeFor(s).getOrFail(() -> new DataTypeNotIntroducedException(s + " doesn't represent a known data type"));
	}
	
	/**
	 * Introduces a new special case to element value parsing. The special cases of 
	 * {@link BasicDataType} BasicElementValueParser will be included by default 
	 * and need not be introduced separately.
	 * @param parser The parser that handles some element parsing special cases
	 * @deprecated {@link XmlElement} should be used when handling xml content
	 */
	public void introduceSpecialParser(ElementValueParser parser)
	{
		this.specialElementParsers = this.specialElementParsers.plus(
				ImmutableMap.of(parser.getParsedTypes().map(t -> new Pair<>(t, parser))));
	}
	
	/**
	 * Checks whether a element value parser should be used for the provided data type
	 * @param type a data type
	 * @return Should an element value parser be used for the provided data type
	 */
	public boolean isSpecialElementParsingCase(DataType type)
	{
		return this.specialElementParsers.containsKey(type);
	}
	
	/**
	 * Finds an element value parser used for the provided data type
	 * @param type a data type
	 * @return The element value parser used for the type. Null if the type doesn't need any 
	 * special element parsing and default implementation should be used.
	 * @deprecated {@link XmlElement} should be used when handling xml content
	 */
	public ElementValueParser getSpecialParserFor(DataType type)
	{
		return this.specialElementParsers.get(type);
	}
	
	private Option<DataTypeTreeNode> getNode(DataType type)
	{
		return this.dataTypes.find(n -> n.getContent().equals(type));
	}
	
	/**
	 * @deprecated Future support for operations has been (temporarily) dropped
	 */
	private boolean operatorSupportsDataTypes(ValueOperator operator, DataType firstType, 
			DataType secondType)
	{
		// An operator supports a data type also if it happens to support any of its supertypes
		Pair<ImmutableList<DataType>, ImmutableList<DataType>> types = new Pair<>(
				getSuperTypesFor(firstType, true), getSuperTypesFor(secondType, true));
		for (Pair<DataType, DataType> parameterTypes : operator.getPossibleParameterTypes())
		{
			if (types.getFirst().contains(parameterTypes.getFirst()) & 
					types.getSecond().contains(parameterTypes.getSecond()))
				return true;
		}
		
		return false;
	}
	
	
	// SUBCLASSES	------------------
	
	/**
	 * These exceptions are thrown when a data type hasn't been introduced when it is 
	 * used
	 * @author Mikko Hilpinen
	 * @since 7.11.2015
	 */
	public static class DataTypeNotIntroducedException extends RuntimeException
	{
		private static final long serialVersionUID = 2957343334100027240L;

		/**
		 * Creates a new exception
		 * @param dataType The data type that hasn't been introduced yet
		 */
		public DataTypeNotIntroducedException(DataType dataType)
		{
			super(dataType.getName() + " hasn't been introduced");
		}
		
		/**
		 * Creates a new exception
		 * @param message The message sent along with the exception
		 */
		public DataTypeNotIntroducedException(String message)
		{
			super(message);
		}
	}
	
	private class SuperTypeParser implements ValueParser
	{
		@Override
		public Value cast(Value value, DataType to) throws ValueParseException
		{
			// Since this operator is used for casting values to their super types, the 
			// values need not be changed at all
			return value;
		}

		@Override
		public ImmutableList<Conversion> getConversions()
		{
			// Finds all the superType relations and parses them to conversions
			List<Conversion> conversions = new ArrayList<>();
			for (DataTypeTreeNode node : DataTypes.this.dataTypes)
			{
				// TODO: Refactor once treeNode has been refactored
				TreeNode<DataType> parentNode = node.getParent();
				if (parentNode != null)
					conversions.add(new Conversion(node.getContent(), parentNode.getContent(), 
							ConversionReliability.NO_CONVERSION));
			}
			
			return ImmutableList.of(conversions);
		}
	}
}
