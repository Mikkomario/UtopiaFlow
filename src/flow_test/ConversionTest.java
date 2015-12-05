package flow_test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import flow_generics.BasicDataType;
import flow_generics.DataType;
import flow_generics.DataTypeException;
import flow_generics.DataTypes;
import flow_generics.ExtraBoolean;

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
