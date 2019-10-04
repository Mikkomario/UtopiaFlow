package utopia.java.flow.generics;

/**
 * This variable parser generates basic variables, but only of a single type
 * @author Mikko Hilpinen
 * @param <VariableType> The type of variable parsed by this parser
 * @since 7.5.2016
 */
public class SingleTypeVariableParser<VariableType extends Variable> implements VariableParser<VariableType>
{
	// ATTRIBUTES	------------------
	
	private DataType targetType;
	private VariableParser<VariableType> parser;
	
	
	// CONSTRUCTOR	------------------
	
	/**
	 * Creates a new variable parser
	 * @param type The type of variable parsed by this parser
	 * @param parser The parser that is able to actually generate the variables
	 */
	public SingleTypeVariableParser(DataType type, VariableParser<VariableType> parser)
	{
		this.targetType = type;
		this.parser = parser;
	}
	
	/**
	 * Creates a new single type variable parser that generates basic variables
	 * @param type The target type of the variables
	 * @return A variable parser that parses only single type variables
	 */
	public static SingleTypeVariableParser<Variable> createBasicSingleTypeVariableParser(DataType type)
	{
		return new SingleTypeVariableParser<>(type, new BasicVariableParser());
	}
	
	
	// IMPLEMENTED METHODS	---------
	
	@Override
	public VariableType generate(String variableName)
			throws VariableParser.VariableGenerationFailedException
	{
		return this.parser.generate(variableName, Value.NullValue(this.targetType));
	}

	@Override
	public VariableType generate(String variableName, Value value)
			throws VariableParser.VariableGenerationFailedException
	{
		return this.parser.generate(variableName, value.castTo(this.targetType));
	}

	@Override
	public VariableType copy(VariableType variable)
			throws VariableParser.VariableGenerationFailedException
	{
		return this.parser.copy(variable);
	}
}