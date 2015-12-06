package flow_test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import flow_generics.BasicDataType;
import flow_generics.DataType;
import flow_generics.DataTypeException;
import flow_generics.DataTypes;
import flow_generics.ExtraBoolean;
import flow_generics.Variable;
import flow_generics.VariableDeclaration;

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
		String string = "0.7357";
		boolean bool = true;
		int i = 7357;
		double d = 0.7357;
		ExtraBoolean extraBoolean = ExtraBoolean.WEAK_FALSE;
		LocalDate date = LocalDate.now();
		LocalDateTime dateTime = LocalDateTime.now();
		long l = Long.MAX_VALUE;
		
		System.out.println("Starting conversions");
		
		convert(string, BasicDataType.STRING, BasicDataType.DOUBLE);
		convert(string, BasicDataType.STRING, BasicDataType.INTEGER);
		convert(string, BasicDataType.STRING, BasicDataType.EXTRA_BOOLEAN);
		
		convert(bool, BasicDataType.BOOLEAN, BasicDataType.EXTRA_BOOLEAN);
		convert(bool, BasicDataType.BOOLEAN, BasicDataType.INTEGER);
		
		convert(i, BasicDataType.INTEGER, BasicDataType.DOUBLE);
		convert(i, BasicDataType.INTEGER, BasicDataType.BOOLEAN);
		
		convert(d, BasicDataType.DOUBLE, BasicDataType.INTEGER);
		convert(d, BasicDataType.DOUBLE, BasicDataType.EXTRA_BOOLEAN);
		
		convert(extraBoolean, BasicDataType.EXTRA_BOOLEAN, BasicDataType.BOOLEAN);
		convert(extraBoolean, BasicDataType.EXTRA_BOOLEAN, BasicDataType.DOUBLE);
		convert(extraBoolean, BasicDataType.EXTRA_BOOLEAN, BasicDataType.INTEGER);
		
		convert(date, BasicDataType.DATE, BasicDataType.DATETIME);
		convert(dateTime, BasicDataType.DATETIME, BasicDataType.DATE);
		
		convert(l, BasicDataType.LONG, BasicDataType.INTEGER);
		
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
	}
	
	
	// OTHER METHODS	-----------
	
	private static void convert(Object value, DataType from, DataType to)
	{
		System.out.println();
		try
		{
			System.out.println("Converting " + value + " from " + from + " to " + to);
			System.out.println("Output: " + DataTypes.getInstance().parse(value, from, to));
			System.out.println("Reliability: " + DataTypes.getInstance().getConversionReliability(from, to));
		}
		catch (DataTypeException e)
		{
			System.out.println("Conversion failed");
			System.out.println(e.getMessage());
		}
	}
}
