package utopia.flow.util;

import utopia.flow.structure.Lazy;

/**
 * Unit is a class that does nothing. Similar to the use of void, but can also be used as a generic type parameter
 * @author Mikko Hilpinen
 * @since 26.2.2018
 */
public class Unit implements StringRepresentable
{
	// ATTRIBUTES	------------------------
	
	private static final Lazy<Unit> INSTANCE = new Lazy<>(() -> new Unit());
	
	
	// CONSTRUCTOR	------------------------
	
	/**
	 * @return The singular unit instance
	 */
	public static Unit getInstance()
	{
		return INSTANCE.get();
	}
	
	private Unit() { }
	
	
	// IMPLEMENTED METHODS	----------------
	
	@Override
	public String toString()
	{
		return "Unit";
	}
}
