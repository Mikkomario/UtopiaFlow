package flow_generics;

import java.util.Collection;
import java.util.HashSet;

import flow_structure.TreeNode;

/**
 * This is a set for data types where the subtypes of the included data types are also included
 * @author Mikko Hilpinen
 * @since 8.1.2015
 */
public class SubTypeSet extends HashSet<DataType>
{
	// ATTRIBUTES	--------------
	
	private static final long serialVersionUID = -5411484742431413992L;

	
	// CONSTRUCTOR	--------------
	
	/**
	 * Creates a new empty set
	 */
	public SubTypeSet()
	{
		// A simple constructor
	}
	
	/**
	 * Creates a new set from a collection of data types. The subtypes of the data types 
	 * will also be included
	 * @param dataTypes The data types included in the set
	 */
	public SubTypeSet(Collection<? extends DataType> dataTypes)
	{
		addAll(dataTypes);
	}
	
	/**
	 * Creates a new set that contains the provided data type and its subtypes
	 * @param type A data type added to the set
	 */
	public SubTypeSet(DataType type)
	{
		add(type);
	}
	
	
	// IMPLEMENTED METHODS	-----
	
	@Override
	public boolean add(DataType t)
	{
		if (t == null)
			return false;
		else if (super.add(t))
		{
			for (TreeNode<DataType> node : DataTypes.getInstance().get(t).getChildren())
			{
				add(node.getContent());
			}
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean addAll(Collection<? extends DataType> types)
	{
		boolean someWereAdded = false;
		for (DataType type : types)
		{
			if (add(type))
				someWereAdded = true;
		}
		
		return someWereAdded;
	}
	
	
	// OTHER METHODS	-----------
	
	/**
	 * This method returns the single data type in the set, provided that the set only 
	 * contains a single data type.
	 * @return If the set only contains a single data type, returns that. Otherwise returns 
	 * null.
	 */
	public DataType getSingularType()
	{
		DataType singleType = null;
		for (DataType type : this)
		{
			if (singleType == null)
				singleType = type;
			else
				return null;
		}
		
		return singleType;
	}
}
