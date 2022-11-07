package tech.anapad.modela;

import com.beust.jcommander.ParameterException;

import static ch.qos.logback.classic.ClassicConstants.CONFIG_FILE_PROPERTY;
import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.sendSystemOutAndErrToSLF4J;

/**
 * {@link Launcher} applies configurations and launches the application.
 */
public final class Launcher {

    /**
     * The entry point of application.
     *
     * @param cliArguments the CLI arguments
     */
    public static void main(String[] cliArguments) {
        final LauncherArguments arguments = parseArguments(cliArguments);
        if (arguments == null) {
            return;
        }
        configureLogging(arguments);
        // TODO
    }

    /**
     * Parses the given <code>cliArguments</code> into {@link LauncherArguments}.
     *
     * @param cliArguments the CLI arguments
     *
     * @return the {@link LauncherArguments} or <code>null</code> if parsing failed and program execution should stop
     */
    private static LauncherArguments parseArguments(String[] cliArguments) {
        final LauncherArguments launcherArguments = new LauncherArguments(cliArguments);
        try {
            launcherArguments.parse();
        } catch (ParameterException exception) {
            exception.usage();
            return null;
        } catch (IllegalArgumentException exception) {
            System.err.printf("An illegal argument was given: %s\n", exception.getMessage());
            return null;
        } catch (Exception exception) {
            System.err.printf("An exception occurred while parsing the given arguments: %s\n", exception.getMessage());
            return null;
        }

        if (launcherArguments.printHelp()) {
            launcherArguments.getJCommander().usage();
            return null;
        } else {
            return launcherArguments;
        }
    }

    /**
     * Configures SLF4J/Logback logging.
     *
     * @param arguments the {@link LauncherArguments}
     */
    private static void configureLogging(LauncherArguments arguments) {
        sendSystemOutAndErrToSLF4J();
        if (arguments.runProduction()) {
            System.setProperty(CONFIG_FILE_PROPERTY, "logback/logback.production.xml");
        } else {
            System.setProperty(CONFIG_FILE_PROPERTY, "logback/logback.development.xml");
        }
    }
}
