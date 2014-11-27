package flow_io;

import java.io.FileNotFoundException;
import java.util.List;

import flow_structure.TreeNode;

/**
 * TreeFileReader collects the contents of a file into a tree format. It shouldn't be used 
 * with overly large files.
 * 
 * @author Mikko Hilpinen
 * @since 21.11.2014
 */
public class TreeFileReader extends ModeUsingFileReader
{
	// ATTRIBUTES	--------------------------------
	
	private TreeNode<String> root, currentParent;
	private int lastDepth;
	
	
	// CONSTRUCTOR	--------------------------------
	
	/**
	 * Creates a new file reader that uses the given mode indicators
	 * 
	 * @param modeIndicators The strings that indicate a line with a mode introduction. The 
	 * indicators should be in order depth-wise.
	 */
	public TreeFileReader(String[] modeIndicators)
	{
		super(modeIndicators);
		
		// Initializes attributes
		this.root = new TreeNode<String>("root", null);
		this.currentParent = this.root;
		this.lastDepth = 0;
	}
	
	/**
	 * Creates a new file reader that uses the default mode indicators. The default indicators 
	 * are: "&0:", "&1:", ... , "&9:".
	 */
	public TreeFileReader()
	{
		super();
		
		// Initializes attributes
		this.root = new TreeNode<String>("Document", null);
		this.currentParent = this.root;
		this.lastDepth = 0;
	}
	
	
	// IMPLEMENTED METHODS	------------------------

	@Override
	protected void onLine(String line, List<String> modes)
	{
		// Adds content under the latest node
		new TreeNode<String>(line, this.currentParent);
	}

	@Override
	protected void onMode(String newMode, List<String> modes)
	{
		// Finds the correct parent for the new node
		TreeNode<String> parent = this.currentParent;
		while (this.lastDepth - modes.size() >= 0)
		{
			parent = parent.getParent();
			this.lastDepth --;
		}
		
		// Adds a new parent node to the tree
		this.currentParent = new TreeNode<String>(newMode, parent);
		this.lastDepth ++;
	}
	
	@Override
	public void readFile(String fileName, String commentIndicator) throws FileNotFoundException
	{
		// Clears the previous tree first
		this.root = new TreeNode<String>("Document", null);
		this.currentParent = this.root;
		this.lastDepth = 0;
		
		super.readFile(fileName, commentIndicator);
	}
	
	
	// GETTERS & SETTERS	---------------------------------
	
	/**
	 * @return A tree constructed from the contents of the file. The root node will always 
	 * be named "Document".
	 */
	public TreeNode<String> getDocument()
	{
		return this.root;
	}
}
