package flow_util;

/**
 * This value parser is able to parse between all basic data types
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public class BasicValueParser implements ValueParser
{
	// ATTRIBUTES	-------------------
	
	private static BasicValueParser instance = null;
	
	
	// CONSTRUCTOR	-------------------
	
	private BasicValueParser()
	{
		// Static interface
	}
	
	/**
	 * @return An static instance of this parser
	 */
	public static BasicValueParser getInstance()
	{
		if (instance == null)
			instance = new BasicValueParser();
		
		return instance;
	}
	
	
	// IMPLEMENTED METHODS	-----------

	@Override
	public Object parse(Object value, DataType from, DataType to)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataType[] getSupportedInputTypes()
	{
		return BasicDataTypes.values();
	}

	@Override
	public DataType[] getSupportedOutputTypes()
	{
		return BasicDataTypes.values();
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Parses a value into string format. Works the same for all data types
	 * @param value The value that is parsed
	 * @return The value casted to string
	 */
	public static String parseToString(Object value)
	{
		if (value == null)
			return null;
		
		return value.toString();
	}
	
	public static Number parseToNumber(Object value, DataType type)
	{
		if (value == null)
			return 0;
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataTypes.NUMBER))
			return (Number) value;
		// TODO: Parse strings through double
		// TODO: Parse booleans through integer
		// TODO: Date values through long
		
		return 0;
	}
	
	public static Integer parseToInteger(Object value, DataType type)
	{
		if (value == null)
			return 0;
		
		// Integers are good as they are
		if (type.isSameTypeAs(BasicDataTypes.INTEGER))
			return (Integer) value;
		
		// Other numbers can be easily casted
		if (DataTypes.dataTypeIsOfType(type, BasicDataTypes.NUMBER))
			return parseToNumber(value, type).intValue();
		
		// Strings can be parsed
		if (type.isSameTypeAs(BasicDataTypes.STRING))
		{
			try
			{
				// TODO: Parse through double instead?
				return Integer.parseInt((String) value);
			}
			catch (NumberFormatException e)
			{
				// TODO: Check if the string represented a double instead
				throw new ValueParseException(value, type, BasicDataTypes.INTEGER, e);
			}
		}
		// TODO: Also from boolean / extraboolean
		
		return 0;
	}
	
	public Boolean parseToBoolean(Object value, DataType type)
	{
		if (value == null)
			return false;
		
		// TODO: Cast extra boolean
		
		if (type.isSameTypeAs(BasicDataTypes.BOOLEAN))
			return (Boolean) value;
		
		if (type.isSameTypeAs(BasicDataTypes.STRING))
		{
			// Parses strings through extra boolean
		}
		
		// Parse double through extra boolean
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataTypes.NUMBER))
			return parseToInteger(value, type) != 0;
		
		return false;
	}
	
	// TODO: Here, when using parseTo, use static methods of DataTypes instead?
	
	public ExtraBoolean parseToExtraBoolean(Object value, DataType type)
	{
		if (value == null)
			return ExtraBoolean.EXTRA_FALSE;
		
		if (type.isSameTypeAs(BasicDataTypes.EXTRA_BOOLEAN))
			return (ExtraBoolean) value;
		
		if (type.isSameTypeAs(BasicDataTypes.BOOLEAN))
			return ExtraBoolean.parseFromBoolean(parseToBoolean(value, type));
		
		return ExtraBoolean.EXTRA_FALSE;
	}
}
