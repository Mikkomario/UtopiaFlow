package utopia.java.flow.test;

import java.util.HashMap;
import java.util.Map;

import utopia.java.flow.generics.Model;
import utopia.java.flow.generics.Value;
import utopia.java.flow.generics.Variable;

/**
 * This class simply tests new model features introduced in v3.2.0.0
 * @author Mikko Hilpinen
 * @since 13.9.2017
 */
public class MapModelTest
{
	// CONSTRUCTROR	-------------------
	
	private MapModelTest()
	{
		// Hidden constructor
	}
	
	
	// MAIN METHOD	------------------

	/**
	 * Runs the test
	 * @param args Not used
	 */
	public static void main(String[] args)
	{
		Map<String, String> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "1");
		map.put("c", "0.2");
		
		Model<Variable> model = Model.fromMap(map);
		
		assert model.find("d").isEmpty();
		assert model.find("a").isDefined();
		assert model.get("a").toString().equals("a");
		assert model.get("b").toInteger().equals(1);
		assert model.get("c").toDouble().equals(0.2);
		
		model.set("d", Value.of("d"));
		
		assert model.find("d").isDefined();
		assert model.get("d").toString().equals("d");
		
		System.out.println("Success!");
	}
}
