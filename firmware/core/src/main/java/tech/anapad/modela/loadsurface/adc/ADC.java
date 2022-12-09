package tech.anapad.modela.loadsurface.adc;

import tech.anapad.modela.hapticsboard.hapticmotorcontroller.HapticMotorController;
import tech.anapad.modela.util.filter.LowPassFilter;
import tech.anapad.modela.util.location.Location;

import static java.lang.Math.abs;
import static java.lang.Thread.sleep;
import static tech.anapad.modela.util.i2c.I2CNative.readRegisterBytes;
import static tech.anapad.modela.util.i2c.I2CUtil.getRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.resetRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.setRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.setRegisterBits;

/**
 * {@link ADC} represents the NAU7802 24-bit ADC chip.
 */
public class ADC {

    private static final short NAU7802_I2C_ADDRESS = 0x2A;
    private static final short NAU7802_REGISTER_PU_CONTROL = 0x00;
    private static final short NAU7802_REGISTER_CONTROL_1 = 0x01;
    private static final short NAU7802_REGISTER_CONTROL_2 = 0x02;
    private static final short NAU7802_REGISTER_RESULT_START = 0x12;
    private static final short NAU7802_REGISTER_RESULT_END = 0x14;
    private static final short NAU7802_REGISTER_RESULT_LENGTH = NAU7802_REGISTER_RESULT_END -
            NAU7802_REGISTER_RESULT_START + 1;
    private static final double NAU7802_RESULT_MAX_VALUE = Math.pow(2, 24) / 2; // Result is a signed 24-bit value
    private static final short NAU7802_REGISTER_POWER_CONTROL = 0x1C;

    private final int i2cFD;
    private final int index;
    private final Location loadSurfaceLocation;
    private final LowPassFilter baselineSamplesFilter;
    private final LowPassFilter allSamplesFilter;

    private int lastSample;

    /**
     * Instantiates a new {@link HapticMotorController}.
     *
     * @param i2cFD               the low-level I2C device file descriptor
     * @param index               the index
     * @param loadSurfaceLocation the load surface {@link Location}
     */
    public ADC(int i2cFD, int index, Location loadSurfaceLocation) {
        this.i2cFD = i2cFD;
        this.index = index;
        this.loadSurfaceLocation = loadSurfaceLocation;
        baselineSamplesFilter = new LowPassFilter(100);
        allSamplesFilter = new LowPassFilter(25);
    }

    /**
     * Configures this {@link ADC} and calls {@link #calibrate()}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void configure() throws Exception {
        // Reset registers
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 0); // RR
        sleep(1);
        resetRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 0); // RR

        // Power up digital
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 1); // PUD

        // Configuration
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 7); // AVDDS = Internal LDO
        setRegisterBits(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_CONTROL_1, true, 0b111, 2, 0); // GAINS = x128
        setRegisterBits(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_CONTROL_1, true, 0b100, 5, 3); // VLDO = 3.3V
        setRegisterBits(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_CONTROL_2, true, 0b111, 6, 4); // CRS = 320 sps
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_POWER_CONTROL, true, 7); // PGA_CAP_EN

        // Power up analog
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 2); // PUA
        while (!getRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 3)) {} // PUR

        // Calibration
        calibrate();
    }

    /**
     * Calibrates this {@link ADC}.
     *
     * @return <code>true</code> if calibration succeeded, <code>false</code> otherwise
     * @throws Exception thrown for {@link Exception}s
     */
    public boolean calibrate() throws Exception {
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_CONTROL_2, true, 2); // CALS
        while (getRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_CONTROL_2, true, 2)) {} // CALS
        if (getRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_CONTROL_2, true, 3)) { // CAL_ERR
            return false;
        } else {
            return true;
        }
    }

    /**
     * Synchronizes the start of this {@link ADC}'s samples to the exact time of this method call.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void synchronizeSampleCycle() throws Exception {
        setRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 4); // CS
    }

    /**
     * Triggers an {@link ADC} sample.
     *
     * @return the sample integer value
     * @throws Exception thrown for {@link Exception}s
     */
    public int sample() throws Exception {
        while (!getRegisterBit(i2cFD, NAU7802_I2C_ADDRESS, NAU7802_REGISTER_PU_CONTROL, true, 5)) {} // CR
        final byte[] sampleBytes = readRegisterBytes(i2cFD, NAU7802_I2C_ADDRESS,
                NAU7802_REGISTER_RESULT_START, NAU7802_REGISTER_RESULT_LENGTH, true);
        int sampleValue = (sampleBytes[0] & 0xFF) << 16 | (sampleBytes[1] & 0xFF) << 8 | (sampleBytes[2] & 0xFF);
        // Shift left, then sign-extended shift right the value by 8 bits so the sign bit is in correct place
        sampleValue = sampleValue << 8;
        sampleValue = sampleValue >> 8;
        lastSample = sampleValue;
        return sampleValue;
    }

    /**
     * Calls {@link #sample()} and filters it via {@link LowPassFilter#filter(double, boolean)} for both baseline and
     * all samples and returns the percent off the baseline.
     *
     * @param applyToBaseline <code>true</code> if this sample should also apply to the baseline samples,
     *                        <code>false</code> otherwise. Baseline samples are used for differentiating between an
     *                        ADC sample that is regular versus an ADC sample that is irregular (e.g. when the load
     *                        surface Wheatstone bridge has force applied to it).
     *
     * @return a double between <code>0.0</code> and <code>1.0</code> that represents the percent the sample is off the
     * baseline
     * @throws Exception thrown for {@link Exception}s
     */
    public double samplePercentOffset(boolean applyToBaseline) throws Exception {
        final double sample = sample();
        final double filteredSample = allSamplesFilter.filter(sample);
        if (applyToBaseline) {
            baselineSamplesFilter.filter(sample);
        }
        final double baselineFilteredSample = baselineSamplesFilter.getValue();
        return abs((filteredSample - baselineFilteredSample)) / NAU7802_RESULT_MAX_VALUE;
    }

    /**
     * Calls {@link LowPassFilter#getValue()} for {@link #baselineSamplesFilter}.
     *
     * @return a double
     */
    public double getLastBaselineSamplesFilterValue() {
        return baselineSamplesFilter.getValue();
    }

    /**
     * Calls {@link LowPassFilter#getValue()} for {@link #allSamplesFilter}.
     *
     * @return a double
     */
    public double getLastAllSamplesFilterValue() {
        return allSamplesFilter.getValue();
    }

    public int getIndex() {
        return index;
    }

    public Location getLoadSurfaceLocation() {
        return loadSurfaceLocation;
    }

    public int getLastSample() {
        return lastSample;
    }
}
