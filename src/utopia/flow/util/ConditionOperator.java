package utopia.flow.util;

/**
 * These operators can be used in logical conditions
 * @author Mikko Hilpinen
 * @since 29.4.2016
 */
public enum ConditionOperator
{
	/**
	 * True when all conditions are true
	 */
	AND,
	/**
	 * True when any condition is true
	 */
	OR,
	/**
	 * True when one condition is false and another true. Shouldn't be used with more than 
	 * two operands.
	 */
	XOR;
	
	
	// OTHER METHODS	-----------
	
	/**
	 * Checks the final logical value of two values operated by this operator
	 * @param first The first logical value
	 * @param second The second logical value
	 * @return The logical result of the operation
	 */
	public boolean operate(boolean first, boolean second)
	{
		switch (this)
		{
			case AND: return first && second;
			case OR: return first || second;
			case XOR: return first != second;
		}
		
		return false;
	}
	
	/**
	 * Checks the final logical value of multiple values operated by this operator
	 * @param values The values that are operated
	 * @return The locical result of the operation
	 */
	public boolean operate(boolean... values)
	{
		switch (this)
		{
			case AND:
				for (boolean value : values)
				{
					if (!value)
						return false;
				}
				return true;
			case OR:
				for (boolean value : values)
				{
					if (value)
						return true;
				}
				return false;
			case XOR:
				if (values.length < 2)
					return true;
				else
				{
					boolean status = values[0];
					for (int i = 1; i < values.length; i++)
					{
						if (values[i] == status)
							return false;
						status = values[i];
					}
					return true;
				}
		}
		
		return false;
	}
}
