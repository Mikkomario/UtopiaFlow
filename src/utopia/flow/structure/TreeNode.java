package utopia.flow.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import utopia.flow.recording.ObjectParser;

/**
 * TreeNode is a simple tree-like data structure that can contain treeNodes of any type. 
 * These nodes are connected to each other in parent-child manner. A node with 
 * no parent is considered a root or a tree. A node can never be a direct 
 * or indirect child of itself.
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
	
	private static final char CONTINUELAYER = ',';
	private static final char NEWLAYER = '<';
	private static final char PREVIOUSLAYER = '>';
	private static final char NOACTION = ' ';
	
	
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
		this.children = new ArrayList<TreeNode<T>>();
		
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
		if (index >= getChildAmount() || index < 0)
			return null;
		
		return this.children.get(index);
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
	 * Parses the tree into a string format using the given object parser.
	 * 
	 * @param parser The parser that will parse node contents
	 * @return A string representing this tree
	 */
	public String toString(ObjectParser<T> parser)
	{
		String data = parser.parseToString(getContent());
		if (getChildAmount() > 0)
		{
			data += "<";
			
			for (int i = 0; i < getChildAmount(); i++)
			{
				if (i != 0)
					data += ",";
				data += getChild(i).toString(parser);
			}
			
			data += ">";
		}
		
		return data;
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
	
	/* TODO: Create filter methods (recursive filter)
	public List<TreeNode<T>> findChildren(Filter<T> condition)
	{
		return Filter.filterTreeNodesByContent(getChildren(), condition);
	}
	*/
	
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
	
	/**
	 * Parses a string into tree format
	 * @param s The string that will be parsed into a tree format. Nodes under a node should 
	 * be indicated with '<' and '>' while nodes under the same parent should be separated 
	 * with ','. For example "a<b,c>d,e<f<g>>" would be a valid tree.
	 * @param parent The parent node for the new tree
	 * @return The parent node for convenience
	 */
	public static TreeNode<String> constructFromString(String s, TreeNode<String> parent)
	{
		return constructFromString(s, new ObjectParser.StringParser(), parent);
	}
	
	/**
	 * Parses a string into a tree format.
	 * @param s The string that will be parsed into a tree format. Nodes under a node should 
	 * be indicated with '<' and '>' while nodes under the same parent should be separated 
	 * with ','. For example "a<b,c>d,e<f<g>>" would be a valid tree.
	 * @param parser The parser that can construct node data from strings
	 * @param parent The node that will act as a parent for the new nodes (is returned when 
	 * the method ends)
	 * @return The parent node for convenience
	 */
	public static <T> TreeNode<T> constructFromString(String s, ObjectParser<T> parser, 
			TreeNode<T> parent)
	{
		if (countSubStrings(s, "<") != countSubStrings(s, ">"))
			throw new IllegalArgumentException("Could not create a validation tree based on " + 
					s + ". The amount of opened and closed elements is unequal");
		
		String remainingString = new String(s);
		TreeNode<T> tree = parent;
		Stack<TreeNode<T>> parents = new Stack<>();
		parents.push(tree);
		
		// Parses each node separately (a node ends at ',' '<' or '>')
		while (remainingString.length() > 0)
		{
			int nextComma = remainingString.indexOf(CONTINUELAYER);
			int nextNewLayer = remainingString.indexOf(NEWLAYER);
			int nextOldLayer = remainingString.indexOf(PREVIOUSLAYER);
			
			// Finds out how the node ends and at which point
			int nodeEndsAt = -1;
			char nodeEndsWith = NOACTION;
			
			if (nextComma != -1)
			{
				nodeEndsAt = nextComma;
				nodeEndsWith = CONTINUELAYER;
			}
			if (nextNewLayer != -1 && (nextNewLayer < nodeEndsAt || nodeEndsAt == -1))
			{
				nodeEndsAt = nextNewLayer;
				nodeEndsWith = NEWLAYER;
			}
			if (nextOldLayer != -1 && (nextOldLayer < nodeEndsAt || nodeEndsAt == -1))
			{
				nodeEndsAt = nextOldLayer;
				nodeEndsWith = PREVIOUSLAYER;
			}
			if (nodeEndsAt == -1)
				nodeEndsAt = remainingString.length();
			
			// Creates the new node (if there is one)
			TreeNode<T> newNode = null;
			if (nodeEndsAt != 0)
				newNode = new TreeNode<>(parser.parseFromString(remainingString.substring(0, 
						nodeEndsAt)), parents.peek());
			
			// Performs a specific action (depends on the action that ended the current node)
			switch (nodeEndsWith)
			{
				case PREVIOUSLAYER: parents.pop(); break;
				case NEWLAYER:
					if (newNode != null)
						parents.push(newNode);
					else
						System.err.println("Failed to parse " + remainingString);
					break;
			}
			
			// Creates a new remaining tree string
			if (remainingString.length() < nodeEndsAt + 1)
				remainingString = new String();
			else
				remainingString = remainingString.substring(nodeEndsAt + 1);
		}
		
		return tree;
	}
	
	/**
	 * Parses a string type tree into a string
	 * @param tree The tree that will be written into a string
	 * @return A string parsed from the tree
	 */
	public static String treeToString(TreeNode<String> tree)
	{
		return tree.toString(new ObjectParser.StringParser());
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
	}
}