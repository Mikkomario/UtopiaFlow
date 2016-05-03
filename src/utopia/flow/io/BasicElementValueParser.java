package utopia.flow.io;

import java.util.ArrayList;
import java.util.Collection;

import utopia.flow.generics.BasicDataType;
import utopia.flow.generics.DataType;
import utopia.flow.generics.Model;
import utopia.flow.generics.ModelDeclaration;
import utopia.flow.generics.Value;
import utopia.flow.generics.Variable;
import utopia.flow.generics.VariableDeclaration;
import utopia.flow.structure.Element;
import utopia.flow.structure.TreeNode;

/**
 * This parser handles the special parsing cases of the basic data types.
 * @author Mikko Hilpinen
 * @since 3.5.2016
 * @see BasicDataType
 */
public class BasicElementValueParser implements ElementValueParser
{
	// IMPLEMENTED METHODS	---------------

	@Override
	public DataType[] getParsedTypes()
	{
		return new DataType[] {BasicDataType.MODEL, BasicDataType.VARIABLE, 
				BasicDataType.MODEL_DECLARATION, BasicDataType.VARIABLE_DECLARATION};
	}

	@Override
	public TreeNode<Element> writeValue(Value value)
			throws ElementValueParsingFailedException
	{
		// Variables contain a single element with the variable value inside
		if (value.getType().equals(BasicDataType.VARIABLE))
			return variableToElement(value.toVariable());
		// Models, in turn, are parsed into multiple variable elements (wrapped under an 
		// empty element)
		else if (value.getType().equals(BasicDataType.MODEL))
		{
			Model<? extends Variable> model = value.toModel();
			TreeNode<Element> root = new TreeNode<>(new Element("model"));
			for (Variable attribute : model.getAttributes())
			{
				root.addChild(variableToElement(attribute));
			}
			return root;
		}
		// Variable declarations are parsed like variables, except that their default values 
		// are written in place of real values
		else if (value.getType().equals(BasicDataType.VARIABLE_DECLARATION))
			return variableDeclarationToElement(value.toVariableDeclaration());
		// Model declarations follow the same rule as models, except with variables replaced 
		// with variable declarations
		else if (value.getType().equals(BasicDataType.MODEL_DECLARATION))
		{
			ModelDeclaration declaration = value.toModelDeclaration();
			TreeNode<Element> root = new TreeNode<>(new Element("model"));
			for (VariableDeclaration dec : declaration.getAttributeDeclarations())
			{
				root.addChild(variableDeclarationToElement(dec));
			}
			return root;
		}
		else
			throw new ElementValueParsingFailedException("Unsupported data type " + value.getType());
	}

	@Override
	public Value readValue(TreeNode<Element> element, DataType targetType) throws 
			ElementValueParsingFailedException
	{
		if (targetType.equals(BasicDataType.VARIABLE))
			return Value.Variable(elementToVariable(element));
		else if (targetType.equals(BasicDataType.VARIABLE_DECLARATION))
			return Value.VariableDeclaration(elementToVariableDeclaration(element));
		else if (targetType.equals(BasicDataType.MODEL))
		{
			Model<Variable> model = Model.createBasicModel();
			for (TreeNode<Element> child : element.getChildren())
			{
				model.addAttribute(elementToVariable(child), true);
			}
			return Value.Model(model);
		}
		else if (targetType.equals(BasicDataType.MODEL_DECLARATION))
		{
			Collection<VariableDeclaration> variableDeclarations = new ArrayList<>();
			for (TreeNode<Element> child : element.getChildren())
			{
				variableDeclarations.add(elementToVariableDeclaration(child));
			}
			return Value.ModelDeclaration(new ModelDeclaration(variableDeclarations));
		}
		else
			throw new ElementValueParsingFailedException("Unsupported Datatype " + targetType);
	}
	
	
	// OTHER METHODS	--------------
	
	private static TreeNode<Element> variableToElement(Variable var)
	{
		return new TreeNode<>(new Element(var.getName(), var.getValue()));
	}
	
	private static TreeNode<Element> variableDeclarationToElement(VariableDeclaration dec)
	{
		return new TreeNode<>(new Element(dec.getName(), dec.getDefaultValue()));
	}
	
	private static Variable elementToVariable(TreeNode<Element> element)
	{
		return new Variable(element.getContent().getName(), 
				element.getContent().getContent());
	}
	
	private static VariableDeclaration elementToVariableDeclaration(TreeNode<Element> element)
	{
		return new VariableDeclaration(element.getContent().getName(), 
				element.getContent().getContent());
	}
}
