package utopia.flow.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import utopia.flow.structure.TreeNode;

/**
 * This class is supposed to make writing new files easy.
 * 
 * @author Mikko Hilpinen
 * @since 22.11.2014
 * @deprecated Replaced with {@link FileUtils}
 */
public class FileOutputAccessor
{
	// CONSTRUCTOR	------------------------------------
	
	private FileOutputAccessor()
	{
		// The constructor is hidden since the interface is static
	}
	
	// OTHER METHODS	---------------------------------
	
	/**
	 * Overwrites a file with the given name, if there is one. Otherwise creates a new file. 
	 * The returned writer should be closed with a separate method.
	 * 
	 * @param fileName The name of the file that will be overwritten / created. 
	 * ("data/" will be automatically included)
	 * @return The writer that has access to the file
	 * @see #closeWriter(Writer)
	 */
	public static BufferedWriter openFile(String fileName)
	{
		// Generates a new filename
		String saveName = "data/" + fileName;
		
		// Creates the new Savefile or overwrites the old one
		File savefile = new File(saveName);
		try
		{
			// Overwrites the old file
			if (!savefile.createNewFile())
			{
				savefile.delete();
				savefile.createNewFile();
			}
		}
		catch (IOException ie)
		{
			System.err.println("Error in creating / overwriting the file " + 
					saveName);
		}
		
		// Writes the data into the file
		BufferedWriter writer = null;
		
		try
		{
			writer = new BufferedWriter(new FileWriter(savefile));
		}
		catch (IOException e)
		{
			System.err.println("Failed to create the fileWriter");
			e.printStackTrace();
		}
		
		return writer;
	}
	
	/**
	 * Writes a new line into the file using the given writer
	 * @param writer The writer that will write the new line
	 * @param newLine The new line that will be written
	 */
	public static void writeLine(BufferedWriter writer, String newLine)
	{
		// Writes the data into the file
    	try
		{
			writer.write(newLine);
			writer.newLine();
		}
    	catch (IOException e)
		{
			System.err.println("Failed to write a line.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes multiple lines to the file at once
	 * @param writer The writer that will be used for writing the data
	 * @param lines The lines that will be written to the file
	 */
	public static void writeLines(BufferedWriter writer, List<String> lines)
	{
		for (String line : lines)
		{
			writeLine(writer, line);
		}
	}
	
	/**
	 * Writes the given lines into a file. The previous file with the given name will be 
	 * overwritten.
	 * 
	 * @param fileName The name of the file the lines will be written into ("data/" 
	 * automatically included)
	 * @param lines The lines that will be written into the file
	 */
	public static void writeLines(String fileName, List<String> lines)
	{
		BufferedWriter writer = null;
		try
		{
			writer = openFile(fileName);
			writeLines(writer, lines);
		}
		finally
		{
			closeWriter(writer);
		}
	}
	
	/**
	 * Writes a tree into the file.
	 * 
	 * @param writer The writer that will write the contents of the tree
	 * @param tree The tree that will be written into the file
	 * @param modeIndicators The modeIndicators which are used for identifying the different 
	 * modes
	 */
	public static <T> void writeTree(BufferedWriter writer, TreeNode<T> tree, 
			String[] modeIndicators)
	{
		int currentDepth = 0;
		TreeNode<T> currentNode = tree;
		boolean allWritten = false;
		
		// Writes nodes from left to right, increasing the depth when possible
		while (!allWritten)
		{
			// Writes the current node down
			String newLine = currentNode.getContent().toString();
			if (currentNode.hasChildren())
			{
				if (currentDepth >= modeIndicators.length)
					throw new IllegalArgumentException("Not enough modeIndicators provided");
				newLine = modeIndicators[currentDepth] + newLine;
			}
			
			writeLine(writer, newLine);
			
			// If there are children left, handles them
			if (currentNode.hasChildren())
			{
				currentNode = currentNode.getChild(0);
				currentDepth ++;
				continue;
			}
			// If there are no children left, handles right siblings
			TreeNode<T> sibling = currentNode.getRightSibling();
			if (sibling != null)
				currentNode = sibling;
			// If there are no siblings left, finds a parents sibling
			// Until there are none left
			else
			{
				while (sibling == null && !allWritten)
				{
					currentNode = currentNode.getParent();
					currentDepth --;
					
					// No parents left
					if (currentNode == null)
						allWritten = true;
					else
						sibling = currentNode.getRightSibling();
				}
				
				currentNode = sibling;
			}
		}
	}
	
	/**
	 * Writes the tree using the default mode indicators. The default indicators are "&0:", 
	 * "&1:" and so on
	 * @param writer The writer that is used for writing the data
	 * @param tree The tree that will be written
	 */
	public static <T> void writeTree(BufferedWriter writer, TreeNode<T> tree)
	{
		writeTree(writer, tree, createCustomIndicatorsFor(tree));
	}
	
	/**
	 * Writes the contents of a tree into a file. The previous data will be overwritten
	 * @param fileName The name of the new file ("data/" is automatically included)
	 * @param tree The tree that will be written
	 * @param modeIndicators The modeIndicators that are used for differentiating different 
	 * mode levels
	 */
	public static <T> void writeTree(String fileName, TreeNode<T> tree, String[] modeIndicators)
	{
		BufferedWriter writer = null;
		try
		{
			writer = openFile(fileName);
			writeTree(writer, tree, modeIndicators);
		}
		finally
		{
			closeWriter(writer);
		}
	}
	
	/**
	 * Writes the contents of a tree into a file. The previous data will be overwritten. 
	 * Default mode indicators ("&0:", "&1:", ...) will be used.
	 * @param fileName The name of the file ("data/" is automatically included)
	 * @param tree The tree that will be written into the file
	 */
	public static <T> void writeTree(String fileName, TreeNode<T> tree)
	{
		writeTree(fileName, tree, createCustomIndicatorsFor(tree));
	}
	
	/**
	 * Closes the given writer
	 * @param writer The writer that will be closed
	 */
	public static void closeWriter(Writer writer)
	{
		// Closes the writer
        try
        {
            if (writer != null)
            {
                writer.flush();
                writer.close();
            }
        }
        catch (IOException ex)
        {
            System.err.println("Failed to close the file writer");
            ex.printStackTrace();
        }
	}
	
	private static String[] createCustomIndicatorsFor(TreeNode<?> tree)
	{
		String[] modeIndicators = new String[tree.getDepth()];
		for (int i = 0; i < modeIndicators.length; i++)
		{
			modeIndicators[i] = "&" + i + ":";
		}
		
		return modeIndicators;
	}
}
