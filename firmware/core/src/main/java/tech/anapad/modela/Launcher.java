package tech.anapad.modela;

import com.beust.jcommander.ParameterException;

import static ch.qos.logback.classic.ClassicConstants.CONFIG_FILE_PROPERTY;
import static java.lang.System.setProperty;
import static javafx.application.Application.launch;
import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.sendSystemOutAndErrToSLF4J;

/**
 * {@link Launcher} launches the application.
 */
public class Launcher {

    /**
     * The entry point of application.
     *
     * @param cliArguments the input arguments
     */
    public static void main(String[] cliArguments) {
        final Arguments arguments = parseArguments(cliArguments);
        if (arguments == null) {
            return;
        }
        configureLogging(arguments);
        printTitle();
        ModelA.arguments = arguments;
        launch(ModelA.class, cliArguments);
    }

    /**
     * Parses the given <code>cliArguments</code> into {@link Arguments}.
     *
     * @param cliArguments the CLI arguments
     *
     * @return the {@link Arguments} or <code>null</code> if parsing failed and program execution should stop
     */
    private static Arguments parseArguments(String[] cliArguments) {
        final Arguments arguments = new Arguments(cliArguments);
        try {
            arguments.parse();
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

        if (arguments.printHelp()) {
            arguments.getJCommander().usage();
            return null;
        } else {
            return arguments;
        }
    }

    /**
     * Configures SLF4J/Logback logging.
     *
     * @param arguments the {@link Arguments}
     */
    private static void configureLogging(Arguments arguments) {
        // TODO setProperty for Logback is not working
        if (arguments.runProduction()) {
            setProperty(CONFIG_FILE_PROPERTY, "logback/logback.production.xml");
        } else {
            setProperty(CONFIG_FILE_PROPERTY, "logback/logback.development.xml");
        }
        sendSystemOutAndErrToSLF4J();
    }

    /**
     * Prints <pre>Model A</pre> in ANSI big text to the console.
     */
    private static void printTitle() {
        System.out.println("\n\n" +
                "███    ███  ██████  ██████  ███████ ██           █████ \n" +
                "████  ████ ██    ██ ██   ██ ██      ██          ██   ██\n" +
                "██ ████ ██ ██    ██ ██   ██ █████   ██          ███████\n" +
                "██  ██  ██ ██    ██ ██   ██ ██      ██          ██   ██\n" +
                "██      ██  ██████  ██████  ███████ ███████     ██   ██" +
                "\n" +
                "\n" +
                "           The the first anapad ever created           " +
                "\n");
    }
}
