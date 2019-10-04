package utopia.java.flow.structure;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import utopia.java.flow.util.StringRepresentable;

/**
 * Eithers are used for holding a single value with one of two types. Usually the "Right" side is used for a success / 
 * default value while the "Left" side is used for a failure / alternative value.
 * @author Mikko Hilpinen
 * @param <Left> The left (failure / alternative) type
 * @param <Right> The right (success / default) type
 * @since 19.7.2018
 */
public class Either<Left, Right> implements StringRepresentable
{
	// ATTRIBUTES	---------------------
	
	private Option<Left> left;
	private Option<Right> right;
	
	
	/// CONSTRUCTOR	---------------------
	
	private Either(Option<Left> left, Option<Right> right)
	{
		this.left = left;
		this.right = right;
	}

	/**
	 * Creates a new left side either
	 * @param item The left item
	 * @return A new left side either
	 */
	public static <Left, Right> Either<Left, Right> left(Left item)
	{
		return new Either<>(Option.some(item), Option.none());
	}
	
	/**
	 * Creates a new right side either
	 * @param item The right item
	 * @return a new right side either
	 */
	public static <Left, Right> Either<Left, Right> right(Right item)
	{
		return new Either<>(Option.none(), Option.some(item));
	}
	
	
	// IMPLEMENTED	--------------------
	
	@Override
	public String toString()
	{
		if (isLeft())
			return "Left(" + this.left.get() + ")";
		else
			return "Right(" + this.right.get() + ")";
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.left == null) ? 0 : this.left.hashCode());
		result = prime * result + ((this.right == null) ? 0 : this.right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Either))
			return false;
		Either<?, ?> other = (Either<?, ?>) obj;
		if (this.left == null)
		{
			if (other.left != null)
				return false;
		}
		else if (!this.left.equals(other.left))
			return false;
		if (this.right == null)
		{
			if (other.right != null)
				return false;
		}
		else if (!this.right.equals(other.right))
			return false;
		return true;
	}
	
	
	// ACCESSORS	--------------------
	
	/**
	 * @return The left side item. None if this is a right side either
	 */
	public Option<Left> left()
	{
		return this.left;
	}

	/**
	 * @return The right side item. None if this is a left side either.
	 */
	public Option<Right> right()
	{
		return this.right;
	}
	
	
	// OTHER	-------------------------
	
	/**
	 * @return Whether this is a left side either
	 */
	public boolean isLeft()
	{
		return this.left.isDefined();
	}
	
	/**
	 * @return Whether this is a right side either
	 */
	public boolean isRight()
	{
		return this.right.isDefined();
	}
	
	/**
	 * @return This either but switched around (left becomes right and right becomes left)
	 */
	public Either<Right, Left> reversed()
	{
		return new Either<>(this.right, this.left);
	}
	
	/**
	 * If this either is a right side either, tries to make it a left side either with a back up value
	 * @param getBackupLeft A function for the back up value
	 * @return This either or a backup either
	 */
	public Either<Left, Right> withBackupLeft(Supplier<? extends Option<? extends Left>> getBackupLeft)
	{
		if (isLeft())
			return this;
		else
		{
			Option<? extends Left> backUp = getBackupLeft.get();
			if (backUp.isDefined())
				return left(backUp.get());
			else
				return this;
		}
	}
	
	/**
	 * If this either is a left side either, tries to make it a right side either with a back up value
	 * @param getBackupRight A function for the back up value
	 * @return This either or a backup either
	 */
	public Either<Left, Right> withBackupRight(Supplier<? extends Option<? extends Right>> getBackupRight)
	{
		if (isRight())
			return this;
		else
		{
			Option<? extends Right> backUp = getBackupRight.get();
			if (backUp.isDefined())
				return right(backUp.get());
			else
				return this;
		}
	}
	
	/**
	 * Handles the value in this either
	 * @param leftHandler A handler for the value if on the left side
	 * @param rightHandler A handler for the value if on the right side
	 */
	public void handle(Consumer<? super Left> leftHandler, Consumer<? super Right> rightHandler)
	{
		left().forEach(leftHandler);
		right().forEach(rightHandler);
	}
	
	/**
	 * Maps the value in this either
	 * @param leftMap The mapping function used if this either is left
	 * @param rightMap The mapping function used if this either is right
	 * @return The mapped value
	 */
	public <B> B handleMap(Function<? super Left, ? extends B> leftMap, Function<? super Right, ? extends B> rightMap)
	{
		if (isRight())
			return rightMap.apply(right.get());
		else
			return leftMap.apply(left.get());
	}
	
	/**
	 * Retrieves left value and uses right value as backup
	 * @param rightMap A mapping function for the right value
	 * @return The left value or right value mapped
	 */
	public Left mapToLeft(Function<? super Right, ? extends Left> rightMap)
	{
		if (isLeft())
			return left.get();
		else
			return rightMap.apply(right.get());
	}
	
	/**
	 * Retrieves right value and uses left value as backup
	 * @param leftMap A mapping function for left value
	 * @return Right value or left value mapped
	 */
	public Right mapToRight(Function<? super Left, ? extends Right> leftMap)
	{
		if (isRight())
			return right.get();
		else
			return leftMap.apply(left.get());
	}
	
	/**
	 * Transforms the left side value of this either
	 * @param f A mapping function for the left side value
	 * @return A new either with transformed value
	 */
	public <B> Either<B, Right> mapLeft(Function<? super Left, B> f)
	{
		return new Either<>(left().map(f), right());
	}
	
	/**
	 * Transforms the right side value of this either
	 * @param f A mapping function for the right side value
	 * @return A new either with transformed value
	 */
	public <B> Either<Left, B> mapRight(Function<? super Right, B> f)
	{
		return new Either<>(left(), right().map(f));
	}
	
	/**
	 * Transforms the values in this either
	 * @param leftMap A mapping function for the left side value
	 * @param rightMap A mapping function for the right side value
	 * @return A mapped either
	 */
	public <L, R> Either<L, R> map(Function<? super Left, L> leftMap, Function<? super Right, R> rightMap)
	{
		return new Either<>(left().map(leftMap), right().map(rightMap));
	}
	
	/**
	 * Produces a single value from this either
	 * @param leftMap A function used for mapping if using left side
	 * @param rightMap A function used for mapping if using right side
	 * @return The mapped value
	 */
	public <B> B toValue(Function<? super Left, ? extends B> leftMap, Function<? super Right, ? extends B> rightMap)
	{
		if (this.left.isDefined())
			return this.left.map(leftMap).get();
		else
			return this.right.map(rightMap).get();
	}
}
