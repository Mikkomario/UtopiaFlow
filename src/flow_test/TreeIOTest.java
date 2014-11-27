package flow_test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import flow_io.FileOutputAccessor;
import flow_io.TreeFileReader;
import flow_io.XMLIOAccessor;
import flow_structure.TreeNode;

/**
 * This test tests basic tree functionalities as well as reading and writing them
 * 
 * @author Mikko Hilpinen
 * @since 27.11.2014
 */
public class TreeIOTest
{
	// CONSTRUCTOR	---------------------------------
	
	private TreeIOTest()
	{
		// Static interface
	}

	
	// MAIN METHOD	---------------------------------
	
	/**
	 * Starts the test
	 * @param args not used
	 */
	public static void main(String[] args)
	{
		// Creates a tree
		TreeNode<String> tree1 = createTree(10);
		// Prints the tree
		System.out.println(tree1);
		// Prints tree statistics
		System.out.println("Tree size: " + tree1.size());
		System.out.println("Tree depth: " + tree1.getDepth());
		
		// Constructs another tree from the print
		TreeNode<String> tree2 = TreeNode.constructFromString(tree1.toString());
		// Prints the other tree
		System.out.println("Copy of tree1: " + tree2);
		tree2 = TreeNode.constructFromString(tree1.toString() + tree1.toString());
		System.out.println("Double of tree1: " + tree2);
		
		// Saves the tree into a file
		BufferedWriter textWriter = FileOutputAccessor.openFile("TreeIOTest.txt");
		FileOutputAccessor.writeTree(textWriter, tree1);
		FileOutputAccessor.closeWriter(textWriter);
		
		// Loads a third tree from the file
		TreeFileReader treeReader = new TreeFileReader();
		try
		{
			treeReader.readFile("TreeIOTest.txt", null);
		}
		catch (FileNotFoundException e)
		{
			System.err.println("No such file!");
			e.printStackTrace();
		}
		tree2 = treeReader.getDocument();
		// Prints the third tree
		System.out.println("Tree read from the file: " + tree2);
		
		// Saves the tree into an xml stream
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		XMLStreamWriter xmlWriter = null;
		try
		{
			xmlWriter = XMLIOAccessor.createWriter(stream);
			XMLIOAccessor.writeDocumentStart("root", xmlWriter);
			XMLIOAccessor.writeTree(tree1, xmlWriter);
			XMLIOAccessor.writeDocumentEnd(xmlWriter);
		}
		catch (UnsupportedEncodingException | XMLStreamException e)
		{
			e.printStackTrace();
		}
		finally
		{
			XMLIOAccessor.closeWriter(xmlWriter);
		}
		byte[] xml = stream.toByteArray();
		System.out.println(new String(xml));
		
		// Loads a tree from the stream
		try
		{
			tree2 = XMLIOAccessor.readTree(new ByteArrayInputStream(xml));
			// Prints the fourth tree
			System.out.println("Tree from xml: " + tree2);
		}
		catch (UnsupportedEncodingException | XMLStreamException e)
		{
			e.printStackTrace();
		}
	}
	
	// OTHER METHODS	-----------------------------
	
	private static TreeNode<String> createTree(int nodeAmount)
	{
		Random random = new Random();
		TreeNode<String> tree = new TreeNode<String>("Node0", null);
		int nodesCreated = 1;
		TreeNode<String> currentNode = tree;
		
		while (nodesCreated < nodeAmount)
		{
			// On 0, creates a new node, on other ones, moves to a child
			int newDestination = random.nextInt(currentNode.getChildAmount() + 1);
			if (newDestination == 0)
			{
				new TreeNode<String>("Node" + nodesCreated, currentNode);
				currentNode = tree;
				nodesCreated ++;
			}
			else
				currentNode = currentNode.getChild(newDestination - 1);
		}
		
		return tree;
	}
}
