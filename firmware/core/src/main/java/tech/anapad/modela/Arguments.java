package tech.anapad.modela;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * {@link Arguments} contains the arguments parsed from the CLI.
 */
public final class Arguments {

    private final String[] cliArguments;
    private JCommander jCommander;

    @Parameter(names = {"-h", "--help"},
            description = "Prints the usage.")
    private boolean printHelp = false;

    @Parameter(names = {"--runProduction"},
            description = "Run this firmware in a production-level environment. Without this argument, the firmware " +
                    "defaults to the development-level environment.")
    private boolean runProduction = false;

    /**
     * Instantiates a new {@link Arguments}.
     *
     * @param cliArguments the CLI arguments
     */
    public Arguments(String[] cliArguments) {
        this.cliArguments = cliArguments;
    }

    /**
     * Parses the arguments.
     *
     * @throws ParameterException       thrown for {@link ParameterException}s from {@link JCommander}
     * @throws IllegalArgumentException thrown for {@link IllegalArgumentException}s which occurs when parsing
     *                                  succeeded, but the arguments parsed are illegal
     */
    public void parse() throws ParameterException, IllegalArgumentException {
        jCommander = JCommander.newBuilder()
                .programName("modela")
                .addObject(this)
                .build();
        jCommander.parse(cliArguments);
    }

    public JCommander getJCommander() {
        return jCommander;
    }

    public boolean printHelp() {
        return printHelp;
    }

    public boolean runProduction() {
        return runProduction;
    }
}
