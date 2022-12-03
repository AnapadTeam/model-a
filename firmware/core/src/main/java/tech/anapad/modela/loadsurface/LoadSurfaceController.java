package tech.anapad.modela.loadsurface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.loadsurface.adc.ADC;
import tech.anapad.modela.loadsurface.i2cmultiplexer.Channel;
import tech.anapad.modela.loadsurface.i2cmultiplexer.I2CMultiplexer;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;
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
    private static final int MAX_SAMPLE_FAILURES = 100;

    private final ModelA modelA;
    private final List<Consumer<List<Double>>> percentOffsetSampleListeners;
    private final List<CompletableFuture<List<Double>>> percentOffsetSampleFutures;
    private final List<Runnable> failureListeners;

    private Integer i2cFD;
    private I2CMultiplexer i2CMultiplexer;
    private Map<Channel, ADC> adcsOfChannels;
    private Thread sampleThread;
    private volatile boolean sampleLoop;
    private int sampleFailures;
    private List<Double> latestSamples;

    /**
     * Instantiates a new {@link LoadSurfaceController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public LoadSurfaceController(ModelA modelA) {
        this.modelA = modelA;
        percentOffsetSampleListeners = synchronizedList(new ArrayList<>());
        failureListeners = synchronizedList(new ArrayList<>());
        percentOffsetSampleFutures = synchronizedList(new ArrayList<>());
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
        adcsOfChannels = new LinkedHashMap<>(of(
                _0, new ADC(i2cFD, 1),
                _1, new ADC(i2cFD, 2),
                _2, new ADC(i2cFD, 3),
                _3, new ADC(i2cFD, 4)));
        for (Entry<Channel, ADC> adcOfChannel : adcsOfChannels.entrySet()) {
            i2CMultiplexer.setChannel(adcOfChannel.getKey());
            adcOfChannel.getValue().configure();
            LOGGER.info("Configured ADC: {}", adcOfChannel.getValue().getIndex());
        }
        for (Entry<Channel, ADC> adcOfChannel : adcsOfChannels.entrySet()) {
            i2CMultiplexer.setChannel(adcOfChannel.getKey());
            adcOfChannel.getValue().synchronizeSampleCycle();
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

            // Sample ADCs
            final List<Double> samples = new ArrayList<>();
            for (Entry<Channel, ADC> adcOfChannel : adcsOfChannels.entrySet()) {
                try {
                    i2CMultiplexer.setChannel(adcOfChannel.getKey());
                    samples.add(adcOfChannel.getValue()
                            .samplePercentOffset(!modelA.getTouchscreenController().didLatestSampleHaveTouches()));
                } catch (Exception exception) {
                    if (++sampleFailures > MAX_SAMPLE_FAILURES) {
                        LOGGER.error("Sample failures exceeded {}!", MAX_SAMPLE_FAILURES);
                        LOGGER.info("Stopping sample loop and calling failure listeners...");
                        synchronized (failureListeners) {
                            failureListeners.forEach(Runnable::run);
                        }
                        LOGGER.info("Called failure listeners.");
                        return;
                    } else {
                        continue;
                    }
                }
                sampleFailures = 0;
            }
            latestSamples = samples;

            // Call sample listeners
            synchronized (percentOffsetSampleListeners) {
                percentOffsetSampleListeners.forEach(listener -> listener.accept(samples));
            }

            // Call futures
            synchronized (percentOffsetSampleFutures) {
                percentOffsetSampleFutures.forEach(future -> future.complete(samples));
            }
            percentOffsetSampleFutures.clear();
        }
    }

    /**
     * Gets a new {@link CompletableFuture} which is completed when the next percent offset sample cycle is complete.
     *
     * @return a {@link CompletableFuture}
     */
    public CompletableFuture<List<Double>> getPercentOffsetSampleFuture() {
        final CompletableFuture<List<Double>> future = new CompletableFuture<>();
        percentOffsetSampleFutures.add(future);
        return future;
    }

    public List<Consumer<List<Double>>> getPercentOffsetSampleListeners() {
        return percentOffsetSampleListeners;
    }

    public List<Runnable> getFailureListeners() {
        return failureListeners;
    }

    public List<Double> getLatestSamples() {
        return latestSamples;
    }
}
