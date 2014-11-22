package flow_structure;

import java.util.ArrayList;
import java.util.List;

/**
 * TreeNode is a simple tree-like data structure that can contain treeNodes of any type. 
 * These nodes are connected to each other in parent-child manner. A node with 
 * no parent is considered a root or a tree. A node can never be a direct 
 * or indirect child of itself.
 * 
 * @author Mikko Hilpinen
 * @since 23.7.2014
 * @param <T> The data type the node contains
 */
public class TreeNode<T>
{
	// ATTRIBUTES	------------------------------------------------------
	
	private List<TreeNode<T>> children;
	private TreeNode<T> parent;
	private T content;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new node that is attached to the given parent.
	 * 
	 * @param content The information this node contains.
	 * @param parent The parent this node is attached to. Null indicates that 
	 * this node will be a root of a tree.
	 */
	public TreeNode(T content, TreeNode<T> parent)
	{
		// Initializes attributes
		this.content = content;
		this.children = new ArrayList<TreeNode<T>>();
		
		setParent(parent);
	}
	
	
	// IMPLEMENTED METHODS	----------------------------------------------
	
	@Override
	public String toString()
	{
		String data = getContent().toString();
		if (getChildAmount() > 0)
		{
			data = data.concat("<");
			
			for (int i = 0; i < getChildAmount(); i++)
			{
				if (i != 0)
					data = data.concat(",");
				data = data.concat(getChild(i).toString());
			}
			
			data = data.concat(">");
		}
		
		return data;
	}
	
	
	// GETTERS & SETTERS	----------------------------------------------
	
	/**
	 * @return The parent of this node. Null if the node is a root node.
	 */
	public TreeNode<T> getParent()
	{
		return this.parent;
	}
	
	/**
	 * @return the content held in this node.
	 */
	public T getContent()
	{
		return this.content;
	}
	
	/**
	 * @return How many children this node has
	 */
	public int getChildAmount()
	{
		return this.children.size();
	}
	
	/**
	 * Returns a child of this node with the given index or null if no such 
	 * child exists.
	 * 
	 * @param index The index of the child.
	 * @return A child with the given index or null if no such child exists.
	 */
	public TreeNode<T> getChild(int index)
	{
		if (index >= getChildAmount())
			return null;
		
		return this.children.get(index);
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Adds a node as the direct child of this node. This does not work if 
	 * this node is a child of the given node.
	 * 
	 * @param child the node that will be placed as this node's child. The 
	 * previous parent of the child will be replaced by this node.
	 * @return Was any change made
	 */
	public boolean addChild(TreeNode<T> child)
	{
		// If the node is already listed as a child, does nothing
		if (this.children.contains(child))
			return false;
		
		if (child.isAboveNode(this))
			return false;
		
		// Adds the node as a child and replaces its parent
		this.children.add(child);
		child.setParent(this);
		
		return true;
	}
	
	/**
	 * Changes the parent of the node. This does not work if the given node 
	 * is a child of this one.
	 * 
	 * @param parent
	 * @return Was any change made
	 */
	public boolean setParent(TreeNode<T> parent)
	{
		// Null parent is always acceptable
		if (parent == null)
		{
			boolean parentWasNull = (this.parent == null);
			this.parent = null;
			return !parentWasNull;
		}
		
		// If the node is already the parent of this one, does nothing
		if (this.parent != null && this.parent.equals(parent))
			return false;
		
		if (isAboveNode(parent))
			return false;
		
		// Adds the node as a parent and adds this node as its child
		this.parent = parent;
		this.parent.addChild(this);
		
		return true;
	}
	
	/**
	 * Checks if the given node is a child or an indirect child of this node.
	 * 
	 * @param node The node that may be a direct or indirect child of this node.
	 * @return Is the node a child or an indirect child of this node.
	 */
	public boolean isAboveNode(TreeNode<T> node)
	{
		// If the node is a direct child of this one, true
		if (this.children.contains(node))
			return true;
		
		// Otherwise has to ask the children if they know the given node
		boolean nodeFound = false;
		for (int i = 0; i < getChildAmount(); i++)
		{
			if (getChild(i).isAboveNode(node))
			{
				nodeFound = true;
				break;
			}
		}
		
		return nodeFound;
	}
	
	/**
	 * Checks any child or indirect child of this node contains the given 
	 * content.
	 * 
	 * @param content The content that may be held by a direct or indirect 
	 * child of this node
	 * @return Does a direct or indirect child contain the given content
	 */
	public boolean isAboveNodeWithContent(T content)
	{
		// If one of the children has the given content, returns true
		for (int i = 0; i < getChildAmount(); i++)
		{
			if (getChild(i).getContent().equals(content))
				return true;
		}
		
		// Otherwise has to ask the children if their children have the 
		// desired content
		boolean contentFound = false;
		for (int i = 0; i < getChildAmount(); i++)
		{
			if (getChild(i).isAboveNodeWithContent(content))
			{
				contentFound = true;
				break;
			}
		}
		
		return contentFound;
	}
	
	/**
	 * @return How "high" the tree is. In other words, how many nodes there are a top of 
	 * each other.
	 */
	public int getDepth()
	{
		int maxDepth = 0;
		for (TreeNode<T> child : this.children)
		{
			int childDepth = child.getDepth();
			if (childDepth > maxDepth)
				maxDepth = childDepth;
		}
		
		return maxDepth + 1;
	}
	
	/**
	 * Checks if the given path of sequential content is valid for this tree. 
	 * If a child has content like the first element in the array and the child 
	 * of that child has content like the second and so on, the path is valid.
	 * 
	 * @param path a path of content that is tested for this tree.
	 * @param canBeAnyContent A piece of content that represents any kind of 
	 * content. If a node contains that content, it can't invalidate the path. 
	 * Null if all content should be read as is and no content should be ignored.
	 * @return Is the given path valid for this tree
	 */
	public boolean containsPath(T[] path, T canBeAnyContent)
	{
		return containsPath(path, canBeAnyContent, 0);
	}
	
	private boolean containsPath(T[] path, T canBeAnyContent, int checkIndex)
	{
		// If there is no path left to check, returns true
		if (path.length <= checkIndex)
			return true;
		
		// Finds all the children that are still on the path
		ArrayList<TreeNode<T>> childrenOnPath = new ArrayList<TreeNode<T>>();
		for (int i = 0; i < getChildAmount(); i++)
		{
			TreeNode<T> node = getChild(i);
			//if (ServerSettings.debugMode)
			//	System.out.println("Checks against child: " + node.getContent()); 
			if (node.getContent().equals(path[checkIndex]) || 
					(canBeAnyContent != null && node.getContent().equals(canBeAnyContent)))
				childrenOnPath.add(node);
		}
		
		// Also checks if the path is valid for any child that didn't break the path
		boolean validForAnyChild = false;
		for (int i = 0; i < childrenOnPath.size(); i++)
		{
			if (childrenOnPath.get(i).containsPath(path, canBeAnyContent, checkIndex + 1))
			{
				validForAnyChild = true;
				break;
			}
		}
		
		return validForAnyChild;
	}
}
