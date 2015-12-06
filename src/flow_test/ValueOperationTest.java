package flow_test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import flow_generics.BasicValueOperation;
import flow_generics.DataTypeException;
import flow_generics.Value;
import flow_generics.ValueOperation;

/**
 * This class tests the basic value operations introduced in this project
 * @author Mikko Hilpinen
 * @since 6.12.2015
 */
public class ValueOperationTest
{
	// CONSTRUCTOR	---------------
	
	private ValueOperationTest()
	{
		// Static interface
	}

	
	// MAIN METHOD	---------------
	
	/**
	 * Runs the test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		Value string = Value.String("76");
		Value i = Value.Integer(32);
		Value d = Value.Double(77.7);
		Value l = Value.Long(14l);
		Value date = Value.Date(LocalDate.now());
		Value dateTime = Value.DateTime(LocalDateTime.now());
		Value bool = Value.Boolean(true);
		
		plus(string, l);
		plus(string, date);
		
		plus(i, string);
		plus(i, d);
		plus(i, bool);
		
		plus(d, l);
		
		plus(l, i);
		
		plus(date, l);
		plus(date, string);
		
		plus(dateTime, l);
		plus(dateTime, d);
		plus(dateTime, bool);
	}
	
	// OTHER METHODS	-----------
	
	private static void plus(Value first, Value second)
	{
		operate(first, BasicValueOperation.PLUS, second);
	}
	
	private static void operate(Value first, ValueOperation operation, Value second)
	{
		try
		{
			System.out.println();
			System.out.println(first.getDescription() + " " + operation + " " + second.getDescription());
			System.out.println("Result:" + first.operate(operation, second));
		}
		catch (DataTypeException e)
		{
			System.out.println("Operation failed");
			System.out.println(e.getMessage());
		}
	}
}
