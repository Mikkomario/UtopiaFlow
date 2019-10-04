package utopia.java.flow.generics;

/**
 * This class represents a conversion a value parser can make between two data types. Each 
 * conversion has a level of reliability.
 * @author Mikko Hilpinen
 * @since 28.11.2015
 */
public class Conversion
{
	// ATTRIBUTES	---------------------
	
	private DataType from, to;
	private ConversionReliability reliability;
	
	
	// CONSTRUCTOR	---------------------
	
	/**
	 * Creates a new conversion
	 * @param from The data type that is casted
	 * @param to The data type resulting from the cast
	 * @param reliability How reliable is the conversion process
	 */
	public Conversion(DataType from, DataType to, ConversionReliability reliability)
	{
		this.from = from;
		this.to = to;
		this.reliability = reliability;
	}
	
	
	// ACCESSORS	---------------------
	
	/**
	 * @return The source data type that will be cast
	 */
	public DataType getSourceType()
	{
		return this.from;
	}
	
	/**
	 * @return The data type of the end result of the cast
	 */
	public DataType getTargetType()
	{
		return this.to;
	}
	
	/**
	 * @return How reliable the conversion process is
	 */
	public ConversionReliability getReliability()
	{
		return this.reliability;
	}
}
