package utopia.flow.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.BasicValueOperation;
import utopia.flow.generics.BasicVariableParser;
import utopia.flow.generics.DataTypeException;
import utopia.flow.generics.Model;
import utopia.flow.generics.Value;
import utopia.flow.generics.ValueOperation;
import utopia.flow.generics.Variable;
import utopia.flow.generics.VariableDeclaration;
import utopia.flow.util.ExtraBoolean;

/**
 * This class tests the basic value operations introduced in this project
 * @author Mikko Hilpinen
 * @since 6.12.2015
 */
class ValueOperationTest
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
		Value string = Value.String("77");
		Value i = Value.Integer(2);
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
		
		
		minus(Value.String("77677"), string);
		minus(string, Value.Integer(2));
		
		minus(i, string);
		minus(i, d);
		minus(i, bool);
		
		minus(d, l);
		minus(l, i);
		
		minus(date, l);
		minus(dateTime, l);
		
		times(string, i);
		times(string, bool);
		
		times(i, i);
		times(d, i);
		times(l, i);
		times(d, d);
		
		divide(i, d);
		divide(i, i);
		divide(i, l);
		
		divide(d, i);
		divide(d, string);
		
		divide(l, i);
		divide(l, l);
		divide(l, d);
		divide(l, Value.ExtraBoolean(ExtraBoolean.WEAK_FALSE));
		
		Value stringVar = Value.Variable(new Variable("stringVar", string));
		Value intVar = Value.Variable(new Variable("intVar", i));
		Value doubleVarDecl = Value.VariableDeclaration(new VariableDeclaration("doubleVar", 
				BasicDataType.DOUBLE));
		Value boolVarDecl = Value.VariableDeclaration(new VariableDeclaration("boolVar", BasicDataType.BOOLEAN));
		
		List<Variable> modelVars = new ArrayList<>();
		modelVars.add(intVar.toVariable());
		modelVars.add(stringVar.toVariable());
		Value modelVar = Value.Model(new Model<>(new BasicVariableParser(), modelVars));
		Value modelDeclVar = Value.ModelDeclaration(doubleVarDecl.toModelDeclaration());
		
		plus(stringVar, intVar);
		plus(stringVar, string);
		plus(stringVar, d);
		
		plus(doubleVarDecl, boolVarDecl);
		plus(doubleVarDecl, intVar);
		
		plus(boolVarDecl, modelVar);
		plus(boolVarDecl, modelDeclVar);
		
		plus(modelVar, stringVar);
		plus(modelVar, boolVarDecl);
		
		plus(modelDeclVar, boolVarDecl);
		plus(modelDeclVar, stringVar);
		
		minus(stringVar, intVar);
		minus(stringVar, string);
		minus(stringVar, Value.Integer(1));
		
		minus(modelVar, stringVar);
		minus(modelDeclVar, doubleVarDecl);
		
		times(stringVar, i);
		times(stringVar, intVar);
		times(intVar, doubleVarDecl);
		
		divide(intVar, stringVar);
		divide(intVar, bool);
	}
	
	// OTHER METHODS	-----------
	
	private static void minus(Value first, Value second)
	{
		operate(first, BasicValueOperation.MINUS, second);
	}
	
	private static void plus(Value first, Value second)
	{
		operate(first, BasicValueOperation.PLUS, second);
	}
	
	private static void times(Value first, Value second)
	{
		operate(first, BasicValueOperation.MULTIPLY, second);
	}
	
	private static void divide(Value first, Value second)
	{
		operate(first, BasicValueOperation.DIVIDE, second);
	}
	
	private static void operate(Value first, ValueOperation operation, Value second)
	{
		try
		{
			System.out.println();
			System.out.println(first.getDescription() + " " + operation + " " + second.getDescription());
			System.out.println("Result: " + first.operate(operation, second).getDescription());
		}
		catch (DataTypeException e)
		{
			System.out.println("Operation failed");
			e.printStackTrace();
		}
	}
}
