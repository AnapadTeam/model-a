package tech.anapad.modela.util.exception;

/**
 * {@link ExceptionRunnable} is the same as a {@link Runnable}, but allows for checked {@link Exception}s.
 */
@FunctionalInterface
public interface ExceptionRunnable {

    /**
     * Same as {@link Runnable#run()} but with a checked {@link Exception}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    void run() throws Exception;
}
