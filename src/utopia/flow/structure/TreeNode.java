package utopia.flow.structure;

import java.util.ArrayList;
import java.util.List;

import utopia.flow.util.Filter;

/**
 * TreeNode is a simple tree-like data structure that can contain treeNodes of any type. 
 * These nodes are connected to each other in parent-child manner. A node with 
 * no parent is considered a root or a tree. A node can never be a direct 
 * or indirect child of itself.
 * @author Mikko Hilpinen
 * @since 23.7.2014
 * @param <T> The data type the node contains
 */
@SuppressWarnings("deprecation")
public class TreeNode<T> implements Node<T>
{
	// ATTRIBUTES	------------------------------------------------------
	
	private List<TreeNode<T>> children;
	private TreeNode<T> parent;
	private T content;
	
	
	// CONSTRUCTOR	------------------------------------------------------
	
	/**
	 * Creates a new node that is attached to the given parent.
	 * @param content The information this node contains.
	 * @param parent The parent this node is attached to. Null indicates that 
	 * this node will be a root of a tree.
	 */
	public TreeNode(T content, TreeNode<T> parent)
	{
		// Initializes attributes
		this.content = content;
		this.children = new ArrayList<>();
		
		setParent(parent);
	}
	
	/**
	 * Creates a new node.
	 * @param content The node's content
	 */
	public TreeNode(T content)
	{
		this.content = content;
		this.children = new ArrayList<>();
	}
	
	
	// IMPLEMETED METHODS	-----------------
	
	@Override
	public T getContent()
	{
		return this.content;
	}
	
	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		s.append("<");
		s.append(String.valueOf(getContent()));
		
		if (hasChildren())
		{
			s.append(">");
			
			for (TreeNode<T> child : getChildren())
			{
				s.append(child.toString());
			}
			
			s.append("</");
			s.append(String.valueOf(getContent()));
			s.append(">");
			
			return s.toString();
		}
		else
		{
			s.append("/>");
			return s.toString();
		}
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
		if (index >= getChildAmount() || index < 0)
			return null;
		
		return this.children.get(index);
	}
	
	/**
	 * Finds the first child node that is accepted by the provided filter
	 * @param filter a filter
	 * @return The first child node accepted by the filter or null if there was no such node
	 */
	public TreeNode<T> getChild(Filter<T> filter)
	{
		return Filter.findFirstNode(getChildren(), filter);
	}
	
	/**
	 * @return The direct child nodes of this node. The list is a copy and the changes made to it 
	 * won't affect the node.
	 */
	public List<TreeNode<T>> getChildren()
	{
		List<TreeNode<T>> children = new ArrayList<>();
		children.addAll(this.children);
		return children;
	}
	
	/**
	 * @return The content of this node's children
	 */
	public List<T> getChildContent()
	{
		return Node.getNodeContent(getChildren());
	}
	
	/**
	 * @return All nodes under this node. This means the nodes direct children, their 
	 * children and so on.
	 */
	public List<TreeNode<T>> getLowerNodes()
	{
		List<TreeNode<T>> nodes = new ArrayList<>();
		
		for (TreeNode<T> child : getChildren())
		{
			nodes.addAll(child.getLowerNodes());
		}
		
		return nodes;
	}
	
	/**
	 * @return The parent node of this node, along with the parent of that node and so on 
	 * until the very root of the tree
	 */
	public List<TreeNode<T>> getHigherNodes()
	{
		List<TreeNode<T>> nodes = new ArrayList<>();
		
		if (getParent() != null)
		{
			nodes.add(getParent());
			nodes.addAll(getParent().getHigherNodes());
		}
		
		return nodes;
	}
	
	
	// OTHER METHODS	--------------------------------------------------
	
	/**
	 * Removes the given node from under this node
	 * @param child The child that will be removed
	 */
	public void removeChild(TreeNode<T> child)
	{
		this.children.remove(child);
		
		if (child != null && child.getParent() != null && child.getParent().equals(this))
			child.setParent(null);
	}
	
	/**
	 * Removes the child at the given index from the nodes under this node
	 * @param index The index of the child that will be removed
	 */
	public void removeChild(int index)
	{
		removeChild(getChild(index));
	}
	
	/**
	 * Removes all the child nodes under this node
	 */
	public void removeChildren()
	{
		for (TreeNode<T> child : getChildren())
		{
			removeChild(child);
		}
	}
	
	/**
	 * Checks if the given node is a direct child of this node
	 * @param node The node that may be a child of this node
	 * @return Is the given node a direct child of this node
	 */
	public boolean hasChild(TreeNode<T> node)
	{
		return this.children.contains(node);
	}
	
	/**
	 * @return Does the node have any children under it
	 */
	public boolean hasChildren()
	{
		return !this.children.isEmpty();
	}
	
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
			if (getParent() == null)
				return false;
			
			TreeNode<T> previousParent = getParent();
			this.parent = null;
			
			previousParent.removeChild(this);
			return true;
		}
		
		// If the node is already the parent of this one, does nothing
		if (this.parent != null && this.parent.equals(parent))
			return false;
		
		if (isAboveNode(parent))
			return false;
		
		// Adds the node as a parent and adds this node as its child
		TreeNode<T> previousParent = getParent();
		this.parent = parent;
		this.parent.addChild(this);
		
		if (previousParent != null)
			previousParent.removeChild(this);
		
		return true;
	}
	
	/**
	 * @return How many nodes there are in this tree in total, including this node. 
	 * (Doesn't count the parent or sibling nodes or any node above those)
	 */
	public int size()
	{
		if (!this.hasChildren())
			return 1;
		
		int size = 1;
		for (TreeNode<T> child : this.children)
		{
			size += child.size();
		}
		
		return size;
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
	 * @return A child on the right side of this node under the same parent
	 */
	public TreeNode<T> getRightSibling()
	{
		if (getParent() == null)
			return null;
		return getParent().getChild(getIndex() + 1);
	}
	
	/**
	 * @return A child on the left side of this node under the same parent
	 */
	public TreeNode<T> getLeftSibling()
	{
		if (getParent() == null)
			return null;
		return getParent().getChild(getIndex() - 1);
	}
	
	/**
	 * @return The index of this node under its parent. getParent().getChild(int) should return 
	 * this node with the this index.
	 */
	public int getIndex()
	{
		if (getParent() == null)
			return -1;
		
		int index = 0;
		while (!getParent().getChild(index).equals(this))
		{
			index ++;
		}
		
		return index;
	}
	
	/**
	 * Finds the children of this node that fulfil the provided condition
	 * @param condition A condition
	 * @return The child nodes of this node that fulfil the condition
	 */
	public List<TreeNode<T>> findChildren(Filter<T> condition)
	{
		return Filter.filterNodes(getChildren(), condition);
	}
	
	/**
	 * Finds the nodes below this node that fulfil the provided condition
	 * @param condition A condition
	 * @return The nodes below this node that fulfil the condition
	 */
	public List<TreeNode<T>> findBelow(Filter<T> condition)
	{
		return Filter.filterNodes(getLowerNodes(), condition);
	}
	
	/**
	 * Creates a filtered tree from this node
	 * @param filter The filter applied
	 * @return This node. Any of the children of this node that are accepted by the filter, 
	 * any nodes 
	 */
	public TreeNode<T> filteredTree(Filter<T> filter)
	{
		TreeNode<T> node = new TreeNode<>(getContent());
		// Adds children and their children, provided they are accepted by the filter
		for (TreeNode<T> child : getChildren())
		{
			if (filter.includes(child.getContent()))
				node.addChild(child.filteredTree(filter));
		}
		
		return node;
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
		ArrayList<TreeNode<T>> childrenOnPath = new ArrayList<>();
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
	/*
	private static int countSubStrings(String searchFrom, String searchedString)
	{
		int count = 0;
		String searchLeft = new String(searchFrom);
		
		while (true)
		{
			int foundAt = searchLeft.indexOf(searchedString);
			
			// If there aren't any left, stops
			if (foundAt < 0)
				break;
			
			// Otherwise remembers it and moves to the next one
			count ++;
			int newIndex = foundAt + searchedString.length();
			if (newIndex >= searchLeft.length())
				break;
			searchLeft = searchLeft.substring(newIndex);
		}
		
		return count;
	}*/
}