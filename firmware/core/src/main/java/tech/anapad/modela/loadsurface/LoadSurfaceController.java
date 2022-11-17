package tech.anapad.modela.loadsurface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;

/**
 * {@link LoadSurfaceController} is a controller for the load surface array.
 */
public class LoadSurfaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSurfaceController.class);

    private final ModelA modelA;

    /**
     * Instantiates a new {@link LoadSurfaceController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public LoadSurfaceController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link LoadSurfaceController}.
     */
    public void start() {

    }

    /**
     * Stops {@link LoadSurfaceController}.
     */
    public void stop() {

    }
}
