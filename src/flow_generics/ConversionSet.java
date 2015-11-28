package flow_generics;

import java.util.ArrayList;
import java.util.List;

/**
 * This set contains conversions between different data types. Only one conversion may exist 
 * per data type and that will be the one with the highest reliability
 * @author Mikko Hilpinen
 * @since 28.11.2015
 */
public class ConversionSet
{
	// TODO: Remove?
	
	// ATTRIBUTES	-----------------
	
	private List<Conversion> conversions;
	
	
	// CONSTRUCTOR	-----------------
	
	/**
	 * Creates a new empty conversion set
	 */
	public ConversionSet()
	{
		this.conversions = new ArrayList<>();
	}

	
	// ACCESSORS	-----------------
	
	/**
	 * @return The conversions stored in this set. The list is a copy and changes made to it 
	 * won't affect this set.
	 */
	public List<Conversion> getConversions()
	{
		return new ArrayList<>(this.conversions);
	}
	
	
	// OTHER METHODS	-------------
	
	/**
	 * Adds a new conversion to the set. The conversion will only be added if it is more 
	 * reliable than a previous conversion between the data types or if there is no previous 
	 * conversion.
	 * @param conversion a conversion between two data types
	 */
	public void addConversion(Conversion conversion)
	{
		if (conversion != null)
		{
			Conversion previous = findMatchingConversion(conversion);
			if (conversion.getReliability().isBetterThan(previous.getReliability()))
			{
				this.conversions.remove(previous);
				this.conversions.add(conversion);
			}
		}
	}
	
	private Conversion findMatchingConversion(Conversion other)
	{
		for (Conversion conversion : this.conversions)
		{
			if (conversion.getSourceType().equals(other.getSourceType()) && 
					conversion.getTargetType().equals(other.getTargetType()))
				return conversion;
		}
		
		return null;
	}
}
