package flow_util;

import flow_structure.TreeNode;

/**
 * A data type tree organises data types into an hierarchy, where a lower data type may 
 * belong to a higher one.
 * @author Mikko Hilpinen
 * @since 7.11.2015
 */
public class DataTypeTreeNode extends TreeNode<DataType>
{
	// CONSTRUCTOR	-------------------
	
	/**
	 * Creates a new data type tree node
	 * @param content The data type of this node
	 * @param parent The parent data types of this node
	 */
	public DataTypeTreeNode(DataType content, TreeNode<DataType> parent)
	{
		super(content, parent);
	}
	
	/**
	 * Creates a new data type tree node
	 * @param content The data type of this node
	 */
	public DataTypeTreeNode(DataType content)
	{
		super(content);
	}
	
	
	// OTHER METHODS	---------------
	
	/**
	 * Checks if this data type is hierarchically under the provided data type
	 * @param other The other data type
	 * @return Is the data type of this node under the provided data type
	 */
	public boolean isOfType(DataType other)
	{
		if (getContent().equals(other))
			return true;
		
		TreeNode<DataType> parent = getParent();
		while (parent != null)
		{
			if (parent.getContent().equals(other))
				return true;
			parent = parent.getParent();
		}
		
		return false;
	}
}