package tech.anapad.modela.loadsurface.sample;

import tech.anapad.modela.util.location.Location;

/**
 * {@link Sample} represents a sample of a load surface.
 */
public class Sample {

    private int index;
    private Location location;
    private int rawSample;
    private double filteredSample;
    private double filteredBaselineSample;
    private double percentOffsetSample;

    /**
     * {@link Builder} is an object builder for {@link Sample}.
     */
    public static class Builder {

        private final Sample sample;

        /**
         * Instantiates a new {@link Builder}.
         */
        public Builder() {
            this.sample = new Sample();
        }

        public Builder index(int index) {
            sample.index = index;
            return this;
        }

        public Builder location(Location location) {
            sample.location = location;
            return this;
        }

        public Builder rawSample(int rawSample) {
            sample.rawSample = rawSample;
            return this;
        }

        public Builder filteredSample(double filteredSample) {
            sample.filteredSample = filteredSample;
            return this;
        }

        public Builder filteredBaselineSample(double filteredBaselineSample) {
            sample.filteredBaselineSample = filteredBaselineSample;
            return this;
        }

        public Builder percentOffsetSample(double percentOffsetSample) {
            sample.percentOffsetSample = percentOffsetSample;
            return this;
        }

        public Sample build() {
            return sample;
        }
    }

    public int getIndex() {
        return index;
    }

    public Location getLocation() {
        return location;
    }

    public int getRawSample() {
        return rawSample;
    }

    public double getFilteredSample() {
        return filteredSample;
    }

    public double getFilteredBaselineSample() {
        return filteredBaselineSample;
    }

    public double getPercentOffsetSample() {
        return percentOffsetSample;
    }
}
