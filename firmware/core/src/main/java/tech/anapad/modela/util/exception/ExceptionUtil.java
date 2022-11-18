package tech.anapad.modela.util.exception;

/**
 * {@link ExceptionUtil} is a utility class for {@link Exception}s.
 */
public final class ExceptionUtil {

    /**
     * Calls the given {@link ExceptionRunnable} with an ignoring catch-all exception handler.
     *
     * @param exceptionRunnable the {@link ExceptionRunnable}
     */
    public static void ignoreException(ExceptionRunnable exceptionRunnable) {
        try {
            exceptionRunnable.run();
        } catch (Exception ignored) {}
    }
}
