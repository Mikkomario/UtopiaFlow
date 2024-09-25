package utopia.java.flow.util;

import utopia.java.flow.async.ThreadPool;

import java.time.Duration;
import java.util.concurrent.Executor;

/**
 * Provides access to commonly shared values
 * @author Mikko Hilpinen
 * @since 24.09.2024
 */
public class Common
{
    // ATTRIBUTES   ------------------------------

    private static Executor exc = null;


    // OTHER    ----------------------------------

    /**
     * @return Commonly shared execution context
     */
    public static Executor getExc() {
        if (exc == null) {
            System.err.println("WARNING: Common.setExc(Executor) has not been called before .getExc() was called. Sets up a placeholder thread-pool.");
            exc = new ThreadPool("Common.default", 0, 500, Duration.ofSeconds(30),
                    Throwable::printStackTrace);
        }
        return exc;
    }
    /**
     * Specifies the commonly shared execution context
     * @param exc Execution context to use from now on
     */
    public static void setExc(Executor exc) { Common.exc = exc; }
}
