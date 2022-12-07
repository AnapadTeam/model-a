package tech.anapad.modela.loadsurface.sample;

import java.util.ArrayList;
import java.util.List;

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

    public double getPercentOffsetSampleAverage() {
        return percentOffsetSampleAverage;
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
