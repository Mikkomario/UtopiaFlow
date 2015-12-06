package flow_generics;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This value parser is able to parse between all basic data types
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public class BasicValueParser implements ValueParser
{
	// ATTRIBUTES	-------------------
	
	private static BasicValueParser instance = null;
	private Collection<Conversion> conversions;
	
	
	// CONSTRUCTOR	-------------------
	
	private BasicValueParser()
	{
		// Initialises the possible conversions
		this.conversions = new ArrayList<>();
		
		// Can reliably cast from any basic data type to a string. All of the data may not 
		// be present in the final form
		for (DataType type : BasicDataType.values())
		{
			addConversion(type, BasicDataType.STRING, ConversionReliability.DATA_LOSS);
		}
		
		// Can perfectly cast from int, double and long to number, but the backwards conversion 
		// is not perfect
		addConversion(BasicDataType.INTEGER, BasicDataType.NUMBER, ConversionReliability.PERFECT);
		addConversion(BasicDataType.DOUBLE, BasicDataType.NUMBER, ConversionReliability.PERFECT);
		addConversion(BasicDataType.LONG, BasicDataType.NUMBER, ConversionReliability.PERFECT);
		
		addConversion(BasicDataType.NUMBER, BasicDataType.INTEGER, ConversionReliability.DATA_LOSS);
		addConversion(BasicDataType.NUMBER, BasicDataType.LONG, ConversionReliability.DATA_LOSS);
		addConversion(BasicDataType.NUMBER, BasicDataType.DOUBLE, ConversionReliability.DATA_LOSS);
		
		// Integers and long can be parsed from booleans and extra booleans reliably
		addConversion(BasicDataType.BOOLEAN, BasicDataType.INTEGER, ConversionReliability.PERFECT);
		addConversion(BasicDataType.EXTRA_BOOLEAN, BasicDataType.INTEGER, ConversionReliability.DATA_LOSS);
		addConversion(BasicDataType.STRING, BasicDataType.INTEGER, ConversionReliability.DANGEROUS);
		
		addConversion(BasicDataType.BOOLEAN, BasicDataType.LONG, ConversionReliability.PERFECT);
		addConversion(BasicDataType.EXTRA_BOOLEAN, BasicDataType.LONG, ConversionReliability.DATA_LOSS);
		addConversion(BasicDataType.STRING, BasicDataType.LONG, ConversionReliability.DANGEROUS);
		
		// Doubles as well. Doubles can also be cast from Strings, although that is unreliable
		addConversion(BasicDataType.BOOLEAN, BasicDataType.DOUBLE, ConversionReliability.PERFECT);
		addConversion(BasicDataType.EXTRA_BOOLEAN, BasicDataType.DOUBLE, ConversionReliability.PERFECT);
		addConversion(BasicDataType.STRING, BasicDataType.DOUBLE, ConversionReliability.DANGEROUS);
		addConversion(BasicDataType.INTEGER, BasicDataType.DOUBLE, ConversionReliability.PERFECT);
		
		// Boolean values can be cast from numbers and extra booleans
		addConversion(BasicDataType.NUMBER, BasicDataType.BOOLEAN, ConversionReliability.MEANING_LOSS);
		addConversion(BasicDataType.EXTRA_BOOLEAN, BasicDataType.BOOLEAN, ConversionReliability.DATA_LOSS);
		
		// Extra booleans can be cast from booleans, doubles and strings
		addConversion(BasicDataType.BOOLEAN, BasicDataType.EXTRA_BOOLEAN, ConversionReliability.PERFECT);
		addConversion(BasicDataType.DOUBLE, BasicDataType.EXTRA_BOOLEAN, ConversionReliability.MEANING_LOSS);
		addConversion(BasicDataType.STRING, BasicDataType.EXTRA_BOOLEAN, ConversionReliability.DANGEROUS);
		
		// Dates can be cast from datetimes and string
		addConversion(BasicDataType.DATETIME, BasicDataType.DATE, ConversionReliability.DATA_LOSS);
		addConversion(BasicDataType.STRING, BasicDataType.DATE, ConversionReliability.DANGEROUS);
		
		// Datetimes can be cast from date and string
		addConversion(BasicDataType.DATE, BasicDataType.DATETIME, ConversionReliability.PERFECT);
		addConversion(BasicDataType.STRING, BasicDataType.DATETIME, ConversionReliability.DANGEROUS);
		
		// Variables can be cast to any other data type, but it is unreliable 
		// whether the cast will succeed since the variable's data type is not known
		for (DataType type : BasicDataType.values())
		{
			// Variables can easily be wrapped to models and variable declarations
			// But it is a bit dangerou
			if (type == BasicDataType.MODEL)
				addConversion(BasicDataType.VARIABLE, type, ConversionReliability.PERFECT);
			if (type == BasicDataType.VARIABLE_DECLARATION)
				addConversion(BasicDataType.VARIABLE, type, ConversionReliability.DATA_LOSS);
			else if (type != BasicDataType.VARIABLE)
				addConversion(BasicDataType.VARIABLE, type, ConversionReliability.DANGEROUS);
		}
		
		// Variable declarations can be cast to variables and model 
		// declarations perfectly
		addConversion(BasicDataType.VARIABLE_DECLARATION, BasicDataType.VARIABLE, 
				ConversionReliability.PERFECT);
		addConversion(BasicDataType.VARIABLE_DECLARATION, BasicDataType.MODEL_DECLARATION, 
				ConversionReliability.PERFECT);
		
		// Model declarations can be cast to models perfectly, but model back to declarations 
		// only reliably
		addConversion(BasicDataType.MODEL_DECLARATION, BasicDataType.MODEL, 
				ConversionReliability.PERFECT);
		addConversion(BasicDataType.MODEL, BasicDataType.MODEL_DECLARATION, 
				ConversionReliability.DATA_LOSS);
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
	public Object parse(Object value, DataType from, DataType to) throws ValueParseException
	{
		if (from == null || to == null)
			throw new ValueParseException(value, from, to);
		// Null stays the same no matter the data type
		if (value == null)
			return null;
		
		if (from.equals(BasicDataType.VARIABLE))
		{
			Variable var = (Variable) value;
			// May wrap the variable into a model (exception for model type variables)
			if (to.equals(BasicDataType.MODEL) && !var.getType().equals(BasicDataType.MODEL))
				return var.wrapToModel();
			// Variable declaration is also a possibility
			if (to.equals(BasicDataType.VARIABLE_DECLARATION) && !var.getType().equals(
					BasicDataType.VARIABLE_DECLARATION))
				return var.getDeclaration();
			// Otherwise tries to cast the variable value
			else
				return var.getObjectValue(to);
		}
		else if (from.equals(BasicDataType.VARIABLE_DECLARATION))
		{
			VariableDeclaration declaration = (VariableDeclaration) value;
			// Variable declarations can be wrapped to model declarations
			if (to.equals(BasicDataType.MODEL_DECLARATION))
				return declaration.wrapToModelDeclaration();
			// Can also be instantiated to variables
			if (to.equals(BasicDataType.VARIABLE))
				return declaration.assignNullValue();
		}
		else if (from.equals(BasicDataType.MODEL) && to.equals(BasicDataType.MODEL_DECLARATION))
			return ((Model) value).getDeclaration();
		else if (from.equals(BasicDataType.MODEL_DECLARATION) && to.equals(BasicDataType.MODEL))
			return ((ModelDeclaration) value).instantiate();
			
		
		if (to.equals(BasicDataType.BOOLEAN))
			return parseBoolean(value, from);
		if (to.equals(BasicDataType.DATE))
			return parseDate(value, from);
		if (to.equals(BasicDataType.DATETIME))
			return parseDateTime(value, from);
		if (to.equals(BasicDataType.DOUBLE))
			return parseDouble(value, from);
		if (to.equals(BasicDataType.EXTRA_BOOLEAN))
			return parseExtraBoolean(value, from);
		if (to.equals(BasicDataType.INTEGER))
			return parseInteger(value, from);
		if (to.equals(BasicDataType.LONG))
			return parseLong(value, from);
		if (to.equals(BasicDataType.NUMBER))
			return parseNumber(value, from);
		if (to.equals(BasicDataType.STRING))
			return parseString(value);

		throw new ValueParseException(value, from, to);
	}
	
	@Override
	public Collection<? extends Conversion> getConversions()
	{
		return this.conversions;
	}
	
	
	// OTHER METHODS	---------------
	
	private void addConversion(DataType from, DataType to, ConversionReliability reliability)
	{
		this.conversions.add(new Conversion(from, to, reliability));
	}
	
	/**
	 * Parses a value into string format. Works the same for all data types
	 * @param value The value that is parsed
	 * @return The value casted to string
	 */
	public static String parseString(Object value)
	{
		if (value == null)
			return null;
		
		return value.toString();
	}
	
	/**
	 * Parses an object value to a number.
	 * @param value The value
	 * @param type The data type of the provided value. Numbers, strings, booleans and extra 
	 * booleans are supported.
	 * @return A number parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static Number parseNumber(Object value, DataType type) throws ValueParseException
	{
		if (value == null)
			return 0;
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataType.NUMBER))
			return (Number) value;
		
		if (type.equals(BasicDataType.STRING) || type.equals(BasicDataType.EXTRA_BOOLEAN))
			return parseDouble(value, type);
		
		if (type.equals(BasicDataType.BOOLEAN))
			return parseInteger(value, type);
		
		throw new ValueParseException(value, type, BasicDataType.NUMBER);
	}
	
	/**
	 * Parses an object value to an integer.
	 * @param value The value
	 * @param type The data type of the provided value. Numbers, strings, booleans and extra 
	 * booleans are supported.
	 * @return An integer parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static Integer parseInteger(Object value, DataType type)
	{
		if (value == null)
			return 0;
		
		if (type.equals(BasicDataType.INTEGER))
			return (Integer) value;
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataType.NUMBER))
			return parseNumber(value, type).intValue();
		
		if (type.equals(BasicDataType.STRING))
		{
			try
			{
				return Integer.parseInt((String) value);
			}
			catch (NumberFormatException e)
			{
				// The string may contain decimal numbers
				return parseDouble(value, type).intValue();
			}
		}
		
		if (type.equals(BasicDataType.BOOLEAN))
			return parseBoolean(value, type) ? 1 : 0;
		
		if (type.equals(BasicDataType.EXTRA_BOOLEAN))
			return parseExtraBoolean(value, type).toInteger();
		
		throw new ValueParseException(value, type, BasicDataType.INTEGER);
	}
	
	/**
	 * Parses an object value to a double.
	 * @param value The value
	 * @param type The data type of the provided value. Numbers, strings, booleans and extra 
	 * booleans are supported.
	 * @return A double parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static Double parseDouble(Object value, DataType type)
	{
		if (value == null)
			return 0.0;
		
		if (type.equals(BasicDataType.DOUBLE))
			return (Double) value;
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataType.NUMBER))
			return parseNumber(value, type).doubleValue();
		
		if (type.equals(BasicDataType.STRING))
		{
			try
			{
				return Double.parseDouble(parseString(value));
			}
			catch (NumberFormatException e)
			{
				throw new ValueParseException(value, type, BasicDataType.DOUBLE, e);
			}
		}
		
		if (type.equals(BasicDataType.EXTRA_BOOLEAN))
			return parseExtraBoolean(value, type).toDouble();
		
		if (type.equals(BasicDataType.BOOLEAN))
			return parseBoolean(value, type) ? 1.0 : 0.0;
		
		throw new ValueParseException(value, type, BasicDataType.DOUBLE);
	}
	
	/**
	 * Parses an object value to a long.
	 * @param value The value
	 * @param type The data type of the provided value. Numbers, strings, booleans and extra 
	 * booleans are supported.
	 * @return A long parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static Long parseLong(Object value, DataType type)
	{
		if (value == null)
			return 0l;
		
		if (type.equals(BasicDataType.LONG))
			return (Long) value;
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataType.NUMBER))
			return parseNumber(value, type).longValue();
		
		if (type.equals(BasicDataType.BOOLEAN))
			return parseBoolean(value, type) ? 1l : 0l;
		
		if (type.equals(BasicDataType.EXTRA_BOOLEAN))
			return parseExtraBoolean(value, type).toBoolean() ? 1l : 0l;
		
		if (type.equals(BasicDataType.STRING))
		{
			try
			{
				return Long.parseLong(parseString(value));
			}
			catch (NumberFormatException e)
			{
				return parseDouble(value, type).longValue();
			}
		}
		
		throw new ValueParseException(value, type, BasicDataType.LONG);
	}
	
	/**
	 * Parses an object value to a boolean.
	 * @param value The value
	 * @param type The data type of the provided value. Numbers, strings, booleans and extra 
	 * booleans are supported.
	 * @return A boolean parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static Boolean parseBoolean(Object value, DataType type)
	{
		if (value == null)
			return false;
		
		if (type.equals(BasicDataType.BOOLEAN))
			return (Boolean) value;
		
		// Parses strings through extra boolean as well
		if (type.equals(BasicDataType.EXTRA_BOOLEAN) || 
				type.equals(BasicDataType.STRING))
			return parseExtraBoolean(value, type).toBoolean();
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataType.NUMBER))
			return parseNumber(value, type).intValue() != 0;
		
		throw new ValueParseException(value, type, BasicDataType.BOOLEAN);
	}
	
	/**
	 * Parses an object value to an extra boolean.
	 * @param value The value
	 * @param type The data type of the provided value. Numbers, strings, booleans and extra 
	 * booleans are supported.
	 * @return An extra boolean parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static ExtraBoolean parseExtraBoolean(Object value, DataType type)
	{
		if (value == null)
			return null;
		
		if (type.equals(BasicDataType.EXTRA_BOOLEAN))
			return (ExtraBoolean) value;
		
		if (type.equals(BasicDataType.BOOLEAN))
			return ExtraBoolean.parseFromBoolean(parseBoolean(value, type));
		
		if (DataTypes.dataTypeIsOfType(type, BasicDataType.NUMBER))
			return ExtraBoolean.parseFromDouble(parseDouble(value, type));
		
		if (type.equals(BasicDataType.STRING))
		{
			ExtraBoolean parsed = ExtraBoolean.parseFromString(parseString(value));
			if (parsed == null)
				throw new ValueParseException(value, type, BasicDataType.EXTRA_BOOLEAN);
			else
				return parsed;
		}
		
		throw new ValueParseException(value, type, BasicDataType.EXTRA_BOOLEAN);
	}
	
	/**
	 * Parses an object value to a local date.
	 * @param value The value
	 * @param type The data type of the provided value. Date, datetime and string are supported.
	 * @return A local date parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static LocalDate parseDate(Object value, DataType type)
	{
		if (value == null)
			return null;
		
		if (type.equals(BasicDataType.DATE))
			return (LocalDate) value;
		
		if (type.equals(BasicDataType.DATETIME))
			return parseDateTime(value, type).toLocalDate();
		
		if (type.equals(BasicDataType.STRING))
		{
			try
			{
				return LocalDate.parse(parseString(value));
			}
			catch (DateTimeParseException e)
			{
				throw new ValueParseException(value, type, BasicDataType.DATE, e);
			}
		}
		
		throw new ValueParseException(value, type, BasicDataType.DATE);
	}
	
	/**
	 * Parses an object value to a local date time.
	 * @param value The value
	 * @param type The data type of the provided value. Date, datetime and string are supported.
	 * @return A local date time parsed from the provided value
	 * @throws ValueParseException If the parsing failed.
	 */
	public static LocalDateTime parseDateTime(Object value, DataType type)
	{
		if (value == null)
			return null;
		
		if (type.equals(BasicDataType.DATETIME))
			return (LocalDateTime) value;
		
		if (type.equals(BasicDataType.DATE))
			return parseDate(value, type).atStartOfDay();
		
		if (type.equals(BasicDataType.STRING))
		{
			try
			{
				return LocalDateTime.parse(parseString(value));
			}
			catch (DateTimeParseException e)
			{
				throw new ValueParseException(value, type, BasicDataType.DATETIME, e);
			}
		}
		
		throw new ValueParseException(value, type, BasicDataType.DATETIME);
	}
}