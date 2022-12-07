package tech.anapad.modela.loadsurface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.loadsurface.adc.ADC;
import tech.anapad.modela.loadsurface.i2cmultiplexer.Channel;
import tech.anapad.modela.loadsurface.i2cmultiplexer.I2CMultiplexer;
import tech.anapad.modela.loadsurface.sample.Sample;
import tech.anapad.modela.loadsurface.sample.SampleResult;
import tech.anapad.modela.util.i2c.I2CNative;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableMap;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._0;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._1;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._2;
import static tech.anapad.modela.loadsurface.i2cmultiplexer.Channel._3;
import static tech.anapad.modela.util.location.Location.loc;

/**
 * {@link LoadSurfaceController} is a controller for the load surface array.
 */
public class LoadSurfaceController implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadSurfaceController.class);
    private static final int I2C_DEVICE_INDEX = 1;
    private static final int MAX_SAMPLE_FAILURES = 100;

    private final ModelA modelA;
    private final List<Consumer<SampleResult>> sampleResultListeners;
    private final List<CompletableFuture<SampleResult>> sampleResultFutures;
    private final List<Runnable> failureListeners;

    private Integer i2cFD;
    private I2CMultiplexer i2CMultiplexer;
    private Map<Channel, ADC> adcsOfChannels;
    private Thread sampleThread;
    private volatile boolean sampleLoop;
    private int sampleFailures;
    private SampleResult lastSampleResult;

    /**
     * Instantiates a new {@link LoadSurfaceController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public LoadSurfaceController(ModelA modelA) {
        this.modelA = modelA;
        sampleResultListeners = synchronizedList(new ArrayList<>());
        sampleResultFutures = synchronizedList(new ArrayList<>());
        failureListeners = synchronizedList(new ArrayList<>());
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
        final LinkedHashMap<Channel, ADC> tempADCsOfChannels = new LinkedHashMap<>();
        tempADCsOfChannels.put(_0, new ADC(i2cFD, 1, loc(94.752, 20.751)));
        tempADCsOfChannels.put(_1, new ADC(i2cFD, 2, loc(214.548, 20.751)));
        tempADCsOfChannels.put(_2, new ADC(i2cFD, 3, loc(94.752, 62.249)));
        tempADCsOfChannels.put(_3, new ADC(i2cFD, 4, loc(214.548, 62.249)));
        adcsOfChannels = unmodifiableMap(tempADCsOfChannels);
        for (Entry<Channel, ADC> adcOfChannel : adcsOfChannels.entrySet()) {
            final Channel channel = adcOfChannel.getKey();
            final ADC adc = adcOfChannel.getValue();
            i2CMultiplexer.setChannel(channel);
            adc.configure();
            adc.configure(); // TODO calling this twice is a temporary fix to actually force x128 PGA on the ADC chip
            LOGGER.info("Configured ADC: {}", adc.getIndex());
        }
        for (Entry<Channel, ADC> adcOfChannel : adcsOfChannels.entrySet()) {
            final Channel channel = adcOfChannel.getKey();
            final ADC adc = adcOfChannel.getValue();
            i2CMultiplexer.setChannel(channel);
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

            // Sample ADCs
            final List<Sample> samples = new ArrayList<>();
            for (Entry<Channel, ADC> adcOfChannel : adcsOfChannels.entrySet()) {
                try {
                    // Select ADC channel
                    i2CMultiplexer.setChannel(adcOfChannel.getKey());

                    // Acquire sample
                    final ADC adc = adcOfChannel.getValue();
                    final Sample.Builder sampleBuilder = new Sample.Builder();
                    sampleBuilder.index(adc.getIndex());
                    sampleBuilder.location(adc.getLoadSurfaceLocation());
                    final double percentOffsetSample =
                            adc.samplePercentOffset(!modelA.getTouchscreenController().didLatestSampleHaveTouches());
                    sampleBuilder.rawSample(adc.getLastSample());
                    sampleBuilder.filteredSample(adc.getLastAllSamplesFilterValue());
                    sampleBuilder.filteredBaselineSample(adc.getLastBaselineSamplesFilterValue());
                    sampleBuilder.percentOffsetSample(percentOffsetSample);
                    samples.add(sampleBuilder.build());
                } catch (Exception exception) {
                    if (++sampleFailures > MAX_SAMPLE_FAILURES) {
                        LOGGER.error("Sample failures exceeded {}!", MAX_SAMPLE_FAILURES, exception);
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

            // Build result
            final SampleResult sampleResult = new SampleResult.Builder()
                    .percentOffsetSampleAverage(samples.stream()
                            .mapToDouble(Sample::getPercentOffsetSample)
                            .average()
                            .orElseThrow())
                    .samples(samples)
                    .build();
            lastSampleResult = sampleResult;

            // Call sample listeners
            synchronized (sampleResultListeners) {
                sampleResultListeners.forEach(listener -> listener.accept(sampleResult));
            }

            // Call futures
            synchronized (sampleResultFutures) {
                sampleResultFutures.forEach(future -> future.complete(sampleResult));
            }
            sampleResultFutures.clear();
        }
    }

    /**
     * Gets a new {@link CompletableFuture} which is completed when the next percent offset sample cycle is complete.
     *
     * @return a {@link SampleResult} {@link CompletableFuture}
     */
    public CompletableFuture<SampleResult> getPercentOffsetSampleFuture() {
        final CompletableFuture<SampleResult> future = new CompletableFuture<>();
        sampleResultFutures.add(future);
        return future;
    }

    public List<Consumer<SampleResult>> getSampleResultListeners() {
        return sampleResultListeners;
    }

    public List<CompletableFuture<SampleResult>> getSampleResultFutures() {
        return sampleResultFutures;
    }

    public List<Runnable> getFailureListeners() {
        return failureListeners;
    }

    public Map<Channel, ADC> getADCsOfChannels() {
        return adcsOfChannels;
    }

    public SampleResult getLastSampleResult() {
        return lastSampleResult;
    }
}
