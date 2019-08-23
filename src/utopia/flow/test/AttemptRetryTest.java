package utopia.flow.test;

import java.time.Duration;

import utopia.flow.async.Attempt;
import utopia.flow.async.Completion;
import utopia.flow.async.Volatile;
import utopia.flow.structure.Try;
import utopia.flow.util.Test;
import utopia.flow.util.WaitUtils;

/**
 * Tests attempt with multiple tries
 * @author Mikko Hilpinen
 * @since 23.8.2019
 */
public class AttemptRetryTest
{
	@SuppressWarnings("javadoc")
	public static void main(String[] args)
	{
		Attempt<Integer> a1 = makeAttempt(1, 2);
		Attempt<Integer> a2 = makeAttempt(3, 5);
		Attempt<Integer> a3 = makeAttempt(2, 2);
		Attempt<Integer> a4 = makeAttempt(3, 2);
		
		a1.doOnceFulfilled(i -> Test.checkEquals(i.getSuccess(), 1));
		a2.doOnceFulfilled(i -> Test.checkEquals(i.getSuccess(), 3));
		a3.doOnceFulfilled(i -> Test.checkEquals(i.getSuccess(), 2));
		a4.doOnceFulfilled(i -> Test.check(i.isFailure()));
		
		Completion.ofMany(a1, a2, a3, a4).waitFor();
		System.out.println("All tests completed");
	}

	private static Attempt<Integer> makeAttempt(int requiredAttempts, int maxAttempts)
	{
		Volatile<Integer> tries = new Volatile<>(0);
		return Attempt.tryAsynchronous(() -> 
		{
			WaitUtils.wait(Duration.ofMillis(100), new Object());
			int newTryCount = tries.updateAndGet(i -> i + 1);
			if (newTryCount >= requiredAttempts)
				return Try.success(newTryCount);
			else
				return Try.failure(new NullPointerException("Not enough tries"));
			
		}, maxAttempts, Duration.ofMillis(100));
	}
}
