package tech.anapad.modela.loadsurface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.loadsurface.adc.ADC;
import tech.anapad.modela.loadsurface.i2cmultiplexer.Channel;
import tech.anapad.modela.loadsurface.i2cmultiplexer.I2CMultiplexer;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.of;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._0;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._1;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._2;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._3;

/**
 * {@link LoadSurfaceController} is a controller for the load surface array.
 */
public class LoadSurfaceController implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSurfaceController.class);
    private static final int I2C_DEVICE_INDEX = 1;

    private final ModelA modelA;

    private Integer i2cFD;
    private I2CMultiplexer i2CMultiplexer;
    private Map<Channel, ADC> adcsOfMultiplexerChannels;
    private Thread sampleThread;
    private volatile boolean sampleLoop;
    private int sampleFailures;

    /**
     * Instantiates a new {@link LoadSurfaceController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public LoadSurfaceController(ModelA modelA) {
        this.modelA = modelA;
        sampleLoop = false;
        sampleFailures = 0;
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

        LOGGER.info("Testing I2C multiplexer...");
        i2CMultiplexer = new I2CMultiplexer(i2cFD);
        i2CMultiplexer.disable();
        LOGGER.info("Successfully tested I2C multiplexer.");

        LOGGER.info("Configuring ADCs...");
        adcsOfMultiplexerChannels = new LinkedHashMap<>(of(
                _0, new ADC(i2cFD, 1),
                _1, new ADC(i2cFD, 2),
                _2, new ADC(i2cFD, 3),
                _3, new ADC(i2cFD, 4)));
        for (ADC adc : adcsOfMultiplexerChannels.values()) {
            adc.configure();
            LOGGER.info("Configured ADC: {}", adc.getIndex());
        }
        for (ADC adc : adcsOfMultiplexerChannels.values()) {
            adc.synchronizeSampleCycle();
        }
        LOGGER.info("Synchronized ADC sample cycles.");
        LOGGER.info("Configured ADCs.");

        LOGGER.info("Starting sample thread...");
        sampleLoop = true;
        sampleThread = new Thread(this, "LoadSurfaceController Sample Thread");
        sampleThread.start();
        LOGGER.info("Started sample thread.");

        LOGGER.info("Started LoadSurfaceController.");
    }

    /**
     * Stops {@link LoadSurfaceController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void stop() throws Exception {
        LOGGER.info("Stopping LoadSurfaceController...");

        if (sampleThread != null) {
            LOGGER.info("Stopping sample thread...");
            sampleLoop = false;
            sampleThread.join(1000);
            LOGGER.info("Stopped sample thread.");
        }

        if (i2cFD != null) {
            LOGGER.info("Stopping I2C-{}...", I2C_DEVICE_INDEX);
            I2CNative.stop(i2cFD);
            LOGGER.info("Stopped I2C-{}...", I2C_DEVICE_INDEX);
        }

        LOGGER.info("Stopped LoadSurfaceController.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * This run loop is used for reading load surface samples and passing it to listeners.
     */
    @Override
    public void run() {
        while (sampleLoop) {
            // TODO implement a way to add a delay for power saving and such

            // TODO implement loop
        }
    }
}
