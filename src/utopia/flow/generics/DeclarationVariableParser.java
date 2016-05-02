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
	private boolean allowOnlyDeclared;
	
	
	// CONSTRUCTOR	---------------
	
	/**
	 * Creates a new variable parser
	 * @param declaration The declaration the parser uses to define the data types and default 
	 * values of new variables
	 * @param allowOnlyDeclaredVariables Should only declared variables be generated, even when 
	 * it would be possible to generate a variable without a matching declaration.
	 * @param parser The parser used for actually generating the variables
	 */
	public DeclarationVariableParser(ModelDeclaration declaration, 
			boolean allowOnlyDeclaredVariables, VariableParser<VariableType> parser)
	{
		this.declaration = declaration;
		this.allowOnlyDeclared = allowOnlyDeclaredVariables;
		this.parser = parser;
	}
	
	/**
	 * Creates a new declaration variable parser that uses a {@link BasicVariableParser} to 
	 * generate the variables
	 * @param declaration The declaration used for defining data types and default values
	 * @param allowOnlyDeclaredVariables Should only declared variables be generated, even when 
	 * it would be possible to generate a variable without a matching declaration.
	 * @return A parser that will generate basic variables based on the declaration
	 */
	public static DeclarationVariableParser<Variable> createBasicDeclarationVariableParser(
			ModelDeclaration declaration, boolean allowOnlyDeclaredVariables)
	{
		return new DeclarationVariableParser<>(declaration, allowOnlyDeclaredVariables, 
				new BasicVariableParser());
	}
	
	
	// IMPLMENENTED METHODS	--------------

	@Override
	public VariableType generate(String variableName)
			throws utopia.flow.generics.VariableParser.VariableGenerationFailedException
	{
		// Checks if the declaration contains a matching variable
		VariableDeclaration declaration = this.declaration.findAttributeDeclaration(variableName);
		
		// If there is no declaration, one may try to generate one anyway or fail straight away
		if (declaration == null)
		{
			if (this.allowOnlyDeclared)
				throw new VariableGenerationFailedException(
						"Declaration doesn't contain variable '" + variableName + "'");
			else
				return this.parser.generate(variableName);
		}
		else
			return declaration.assignDefaultValue(this.parser);
	}

	@Override
	public VariableType generate(String variableName, Value value)
			throws utopia.flow.generics.VariableParser.VariableGenerationFailedException
	{
		VariableDeclaration declaration = this.declaration.findAttributeDeclaration(variableName);
		
		if (declaration == null)
		{
			if (this.allowOnlyDeclared)
				throw new VariableGenerationFailedException(
						"Declaration doesn't contain variable '" + variableName + "'");
			else
				return this.parser.generate(variableName, value);
		}
		else
			return declaration.assignValue(this.parser, value);
	}

	@Override
	public VariableType copy(VariableType variable)
			throws utopia.flow.generics.VariableParser.VariableGenerationFailedException
	{
		return this.parser.copy(variable);
	}
}
