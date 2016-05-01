package utopia.flow.generics;

/**
 * This variable parser uses an existing model declaration when generating new variables
 * @author Mikko Hilpinen
 * @since 30.4.2016
 * @param <VariableType> The type of variable generated through this parser
 */
public class DeclarationVariableParser<VariableType extends Variable> implements VariableParser<VariableType>
{
	// ATTRIBUTES	---------------
	
	private VariableParser<VariableType> parser;
	private ModelDeclaration declaration;
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new variable parser
	 * @param declaration The declaration the parser uses to define the data types and default 
	 * values of new variables
	 * @param parser The parser used for actually generating the variables
	 */
	public DeclarationVariableParser(ModelDeclaration declaration, VariableParser<VariableType> parser)
	{
		this.declaration = declaration;
		this.parser = parser;
	}
	
	/**
	 * 
	 * @param declaration
	 * @return
	 */
	public static DeclarationVariableParser<Variable> createBasicDeclarationVariableParser(ModelDeclaration declaration)
	{
		return new DeclarationVariableParser<>(declaration, new BasicVariableParser());
	}

	@Override
	public VariableType generate(String variableName)
			throws utopia.flow.generics.VariableParser.VariableGenerationFailedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VariableType generate(String variableName, Value value)
			throws utopia.flow.generics.VariableParser.VariableGenerationFailedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VariableType copy(VariableType variable)
			throws utopia.flow.generics.VariableParser.VariableGenerationFailedException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
