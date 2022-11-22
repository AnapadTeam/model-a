package tech.anapad.modela.loadsurface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.util.i2c.I2CNative;

/**
 * {@link LoadSurfaceController} is a controller for the load surface array.
 */
public class LoadSurfaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSurfaceController.class);
    private static final int I2C_DEVICE_INDEX = 1;

    private final ModelA modelA;

    private Integer i2cFD;

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
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void start() throws Exception {
        LOGGER.info("Starting LoadSurfaceController...");

        LOGGER.info("Starting I2C-{}...", I2C_DEVICE_INDEX);
        i2cFD = I2CNative.start(I2C_DEVICE_INDEX);
        LOGGER.info("Started I2C-{}...", I2C_DEVICE_INDEX);

        LOGGER.info("Started LoadSurfaceController.");
    }

    /**
     * Stops {@link LoadSurfaceController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void stop() throws Exception {
        LOGGER.info("Stopping LoadSurfaceController...");

        if (i2cFD != null) {
            LOGGER.info("Stopping I2C-{}...", I2C_DEVICE_INDEX);
            I2CNative.stop(i2cFD);
            LOGGER.info("Stopped I2C-{}...", I2C_DEVICE_INDEX);
        }

        LOGGER.info("Stopped LoadSurfaceController.");
    }
}
