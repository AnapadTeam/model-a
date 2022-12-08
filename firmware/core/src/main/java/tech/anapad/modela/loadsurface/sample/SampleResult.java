package tech.anapad.modela.loadsurface.sample;

import tech.anapad.modela.util.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Math.min;

/**
 * {@link SampleResult} represents a complete sample result.
 */
public class SampleResult {

    private double percentOffsetSampleAverage;
    private List<Sample> samples = new ArrayList<>();

    /**
     * {@link Builder} is an object builder for {@link SampleResult}.
     */
    public static class Builder {

        private final SampleResult sampleResult;

        /**
         * Instantiates a new {@link Sample.Builder}.
         */
        public Builder() {
            this.sampleResult = new SampleResult();
        }

        public Builder percentOffsetSampleAverage(double percentOffsetSampleAverage) {
            sampleResult.percentOffsetSampleAverage = percentOffsetSampleAverage;
            return this;
        }

        public Builder samples(List<Sample> samples) {
            sampleResult.samples = samples;
            return this;
        }

        public SampleResult build() {
            return sampleResult;
        }
    }

    /**
     * Gets the {@link Sample#getPercentOffsetSample()} for all {@link #getSamples()}, but weight proportionally to the
     * distance between a {@link Sample#getLocation()} and the given <code>location</code>.
     *
     * @param location the {@link Location}
     *
     * @return the weighted percent offset
     */
    public double weightedPercentOffset(Location location) {
        final Map<Double, Double> distancesOfSamples = new HashMap<>();
        double minDistance = MAX_VALUE;
        for (Sample sample : samples) {
            final double distance = location.distance(sample.getLocation());
            minDistance = min(distance, minDistance);
            distancesOfSamples.put(sample.getPercentOffsetSample(), distance);
        }
        double weightedPercentOffsetSum = 0;
        double weightSum = 0;
        for (Map.Entry<Double, Double> distanceOfSample : distancesOfSamples.entrySet()) {
            final double percentOffsetSample = distanceOfSample.getKey();
            final double distance = distanceOfSample.getValue();
            final double weight = minDistance / distance;
            weightedPercentOffsetSum += percentOffsetSample * weight;
            weightSum += weight;
        }
        return weightedPercentOffsetSum / weightSum;
    }

    public double getPercentOffsetSampleAverage() {
        return percentOffsetSampleAverage;
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
