package tech.anapad.modela.touchscreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.loadsurface.LoadSurfaceController;

/**
 * {@link TouchscreenController} is a controller for the GT9110 touchscreen driver board and PCAP panel.
 */
public class TouchscreenController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TouchscreenController.class);

    private final ModelA modelA;

    /**
     * Instantiates a new {@link TouchscreenController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public TouchscreenController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link TouchscreenController}.
     */
    public void start() {

    }

    /**
     * Stops {@link TouchscreenController}.
     */
    public void stop() {

    }
}
