package tech.anapad.modela.hapticsboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;

/**
 * {@link HapticsBoardController} is a controller for the haptics board.
 */
public class HapticsBoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HapticsBoardController.class);

    private final ModelA modelA;

    /**
     * Instantiates a new {@link HapticsBoardController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public HapticsBoardController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link HapticsBoardController}.
     */
    public void start() {

    }

    /**
     * Stops {@link HapticsBoardController}.
     */
    public void stop() {

    }
}
