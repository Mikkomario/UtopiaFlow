package utopia.flow.test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.DataType;
import utopia.flow.generics.DataTypeException;
import utopia.flow.generics.DataTypes;
import utopia.flow.generics.Value;
import utopia.flow.util.ExtraBoolean;

/**
 * This class tests the generic value conversion
 * @author Mikko Hilpinen
 * @since 5.12.2015
 */
public class ConversionTest
{
	// CONSTRUCTOR	-----------------
	
	private ConversionTest()
	{
		// Static interface
	}

	
	// MAIN METHOD	-----------------
	
	/**
	 * Starts the test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		Value string = Value.String("0.7357");
		Value bool = Value.Boolean(true);
		Value i = Value.Integer(7357);
		Value d = Value.Double(0.7357);
		Value extraBoolean = Value.ExtraBoolean(ExtraBoolean.WEAK_FALSE);
		Value date = Value.Date(LocalDate.now());
		Value dateTime = Value.DateTime(LocalDateTime.now());
		Value l = Value.Long(Long.MAX_VALUE);
		
		System.out.println("Starting conversions");
		
		convert(string, BasicDataType.DOUBLE);
		convert(string, BasicDataType.INTEGER);
		convert(string, BasicDataType.EXTRA_BOOLEAN);
		
		convert(bool, BasicDataType.EXTRA_BOOLEAN);
		convert(bool, BasicDataType.INTEGER);
		
		convert(i, BasicDataType.DOUBLE);
		convert(i, BasicDataType.BOOLEAN);
		
		convert(d, BasicDataType.INTEGER);
		convert(d, BasicDataType.EXTRA_BOOLEAN);
		
		convert(extraBoolean, BasicDataType.BOOLEAN);
		convert(extraBoolean, BasicDataType.DOUBLE);
		convert(extraBoolean, BasicDataType.INTEGER);
		
		convert(date, BasicDataType.DATETIME);
		convert(dateTime, BasicDataType.DATE);
		
		convert(l, BasicDataType.INTEGER);
		
		/*
		System.out.println("\nVariable Conversions");
		Variable stringVar = new Variable("StringVar", BasicDataType.STRING, string);
		Variable doubleVar = new Variable("DoubleVar", BasicDataType.DOUBLE, d);
		//Variable booleanVar = new Variable("BooleanVar", BasicDataType.BOOLEAN, bool);
		
		convert(stringVar, BasicDataType.VARIABLE, BasicDataType.STRING);
		convert(stringVar, BasicDataType.VARIABLE, BasicDataType.DOUBLE);
		
		convert(doubleVar, BasicDataType.VARIABLE, BasicDataType.DOUBLE);
		convert(doubleVar, BasicDataType.VARIABLE, BasicDataType.MODEL);
		convert(doubleVar, BasicDataType.VARIABLE, BasicDataType.VARIABLE_DECLARATION);
		
		VariableDeclaration dec = new VariableDeclaration("TestVar", BasicDataType.BOOLEAN);
		
		convert(dec, BasicDataType.VARIABLE_DECLARATION, BasicDataType.MODEL_DECLARATION);
		convert(dec, BasicDataType.VARIABLE_DECLARATION, BasicDataType.VARIABLE);
		convert(dec, BasicDataType.VARIABLE_DECLARATION, BasicDataType.STRING);
		convert(dec, BasicDataType.VARIABLE_DECLARATION, BasicDataType.BOOLEAN);
		*/
	}
	
	
	// OTHER METHODS	-----------
	
	private static void convert(Value value, DataType to)
	{
		System.out.println();
		try
		{
			System.out.println("Converting " + value.getDescription() + " to " + to);
			System.out.println("Result: " + value.castTo(to).getDescription());
			System.out.println("Reliability: " + 
					DataTypes.getInstance().getConversionReliability(value.getType(), to));
		}
		catch (DataTypeException e)
		{
			System.out.println("Conversion failed");
			System.out.println(e.getMessage());
		}
	}
}
