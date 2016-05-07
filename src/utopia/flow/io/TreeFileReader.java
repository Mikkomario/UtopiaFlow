package utopia.flow.io;

import java.util.List;

import utopia.flow.recording.ObjectParser;
import utopia.flow.structure.TreeNode;

/**
 * TreeFileReader collects the contents of a file into a tree format. It shouldn't be used 
 * with overly large files.
 * @author Mikko Hilpinen
 * @param <T> The type of object held in the read tree
 * @since 21.11.2014
 * @deprecated Replaced with new generic classes and xml element parsing
 */
public class TreeFileReader<T> extends ModeUsingFileReader
{
	// ATTRIBUTES	--------------------------------
	
	private TreeNode<T> root, currentParent;
	private int lastDepth;
	private ObjectParser<T> parser;
	
	
	// CONSTRUCTOR	--------------------------------
	
	/**
	 * Creates a new file reader that uses the given mode indicators
	 * 
	 * @param parent The node that will serve as the parent / root node for the read tree
	 * @param parser The parser that can parse desired content from strings
	 * @param modeIndicators The strings that indicate a line with a mode introduction. The 
	 * indicators should be in order depth-wise.
	 */
	public TreeFileReader(TreeNode<T> parent, ObjectParser<T> parser, String[] modeIndicators)
	{
		super(modeIndicators);
		
		// Initializes attributes
		this.root = parent;
		this.currentParent = this.root;
		this.lastDepth = 0;
		this.parser = parser;
	}
	
	/**
	 * Creates a new file reader that uses the default mode indicators. The default indicators 
	 * are: "&0:", "&1:", ... , "&9:".
	 * @param parent The node that will serve as the parent / root node for the read tree
	 * @param parser The parser that can parse desired content from strings
	 */
	public TreeFileReader(TreeNode<T> parent, ObjectParser<T> parser)
	{
		// Initializes attributes
		this.root = parent;
		this.currentParent = this.root;
		this.lastDepth = 0;
		this.parser = parser;
	}
	
	
	// IMPLEMENTED METHODS	------------------------

	@Override
	protected void onLine(String line, List<String> modes)
	{
		// Adds content under the latest node
		new TreeNode<>(this.parser.parseFromString(line)).setParent(this.currentParent);
	}

	@Override
	protected void onMode(String newMode, List<String> modes)
	{
		// Finds the correct parent for the new node
		TreeNode<T> parent = this.currentParent;
		while (this.lastDepth - modes.size() >= 0)
		{
			parent = parent.getParent();
			this.lastDepth --;
		}
		
		// Adds a new parent node to the tree
		this.currentParent = new TreeNode<T>(this.parser.parseFromString(newMode), parent);
		this.lastDepth ++;
	}
	
	
	// GETTERS & SETTERS	---------------------------------
	
	/**
	 * @return A tree constructed from the contents of the file (the same as the parent node 
	 * given in the constructor).
	 */
	public TreeNode<T> getDocument()
	{
		return this.root;
	}
}
