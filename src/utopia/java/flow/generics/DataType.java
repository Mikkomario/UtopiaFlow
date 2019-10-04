package utopia.java.flow.generics;

/**
 * All data type classes / enumerations should implement this interface so that they can be 
 * used with generalised values. The classes implementing this interface should also 
 * override their equals method.
 * @author Mikko Hilpinen
 * @since 25.10.2015
 */
public interface DataType
{	
	/**
	 * @return The name of the data type
	 */
	public String getName();
}
