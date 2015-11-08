package flow_util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This static interface keeps track of the different data type hierarchies, etc.
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public class DataTypes implements ValueParser
{
	// ATTRIBUTES	------------------
	
	private static DataTypes instance = null;
	
	private List<DataTypeTreeNode> dataTypes;
	private LinkedList<ValueParser> parsers;
	private Set<DataType> supportedInput, supportedOutput;
	
	
	// CONSTRUCTOR	------------------
	
	private DataTypes()
	{
		this.dataTypes = new ArrayList<>();
		this.parsers = new LinkedList<>();
		
		// Initialises the basic data types
		for (DataType type : BasicDataType.values())
		{
			add(new DataTypeTreeNode(type));
		}
		// Connects number types
		DataTypeTreeNode number = get(BasicDataType.NUMBER);
		get(BasicDataType.INTEGER).setParent(number);
		get(BasicDataType.DOUBLE).setParent(number);
		get(BasicDataType.LONG).setParent(number);
		
		// Adds parsing support for basic data types
		addParser(BasicValueParser.getInstance(), true);
	}
	
	/**
	 * @return The data types instance
	 */
	public static DataTypes getInstance()
	{
		if (instance == null)
			instance = new DataTypes();
		
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	----------
	
	@Override
	public Object parse(Object value, DataType from, DataType to) throws ValueParseException
	{	
		for (ValueParser parser : this.parsers)
		{
			if (parser.getSupportedInputTypes().contains(from) && 
					parser.getSupportedOutputTypes().contains(to))
				return parser.parse(value, from, to);
		}
		
		throw new ValueParseException(value, from, to);
	}

	@Override
	public Object parse(Value value, DataType to) throws ValueParseException
	{
		for (ValueParser parser : this.parsers)
		{
			if (parser.getSupportedInputTypes().contains(value.getType()) && 
					parser.getSupportedOutputTypes().contains(to))
				return parser.parse(value, to);
		}
		
		throw new ValueParseException(value, value.getType(), to);
	}

	@Override
	public Collection<? extends DataType> getSupportedInputTypes()
	{
		return this.supportedInput;
	}

	@Override
	public Collection<? extends DataType> getSupportedOutputTypes()
	{
		return this.supportedOutput;
	}

	
	// OTHER METHODS	--------------
	
	/**
	 * Checks if the first data type belongs to the second data type
	 * @param type The first data type
	 * @param other The second data type
	 * @return Does the first data type belong to the second data type
	 * @throws DataTypeNotIntroducedException If the first data type hasn't been introduced
	 */
	public static boolean dataTypeIsOfType(DataType type, DataType other) throws 
		DataTypeNotIntroducedException
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
		DataTypeTreeNode node = getNode(type);
		
		if (node == null)
			throw new DataTypeNotIntroducedException(type);
		else
			return node;
	}
	
	/**
	 * Checks if the provided data type has been introduced
	 * @param type The data type
	 * @return Has the data type been introduced yet
	 */
	public boolean contains(DataType type)
	{
		return getNode(type) != null;
	}
	
	/**
	 * Introduces a new data type
	 * @param dataTypeNode The treeNode for the data type. The node should be connected 
	 * to other associated (hierarchically) data type nodes.
	 * @see #get(DataType)
	 */
	public void add(DataTypeTreeNode dataTypeNode)
	{
		if (!this.dataTypes.contains(dataTypeNode))
		{
			// Removes a previous node of the same type, if there is one
			DataTypeTreeNode previous = getNode(dataTypeNode.getContent());
			if (previous != null)
				this.dataTypes.remove(previous);
			
			this.dataTypes.add(dataTypeNode);
		}
	}
	
	/**
	 * Adds a new parser to the parsers used with data types
	 * @param parser The parser that should be used
	 * @param isPrimary Will the new parser become the primary parser for its supported 
	 * data types (true) or will it be used when others wouldn't work (false).
	 */
	public void addParser(ValueParser parser, boolean isPrimary)
	{
		if (parser == null || parser.equals(this))
			return;
		
		if (!this.parsers.contains(parser))
		{
			if (isPrimary)
				this.parsers.addFirst(parser);
			else
				this.parsers.addLast(parser);
			
			this.supportedInput.addAll(parser.getSupportedInputTypes());
			this.supportedOutput.addAll(parser.getSupportedOutputTypes());
		}
	}
	
	private DataTypeTreeNode getNode(DataType type)
	{
		for (DataTypeTreeNode node : this.dataTypes)
		{
			if (node.getContent().equals(type))
				return node;
		}
		
		return null;
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
}
