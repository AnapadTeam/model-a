package tech.anapad.modela.hapticsboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.util.i2c.I2CNative;

import static java.lang.Thread.sleep;
import static tech.anapad.modela.util.i2c.I2CNative.readRegisterByte;
import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.i2c.I2CUtil.getRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.resetRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.setRegisterBit;

/**
 * {@link HapticsBoardController} is a controller for the haptics board.
 */
public class HapticsBoardController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HapticsBoardController.class);
    private static final int I2C_DEVICE_INDEX = 4;
    private static final short DRV2605L_I2C_ADDRESS = 0x5A;
    private static final short TCA9534_P1_I2C_ADDRESS = 0x20;
    private static final short TCA9534_P2_I2C_ADDRESS = 0x21;
    private static final short TCA9534_P3_I2C_ADDRESS = 0x22;
    private static final short TCA9534_P4_I2C_ADDRESS = 0x23;
    private static final short TCA9534_P5_I2C_ADDRESS = 0x24;

    private final ModelA modelA;

    private Integer i2cFD;

    /**
     * Instantiates a new {@link HapticsBoardController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public HapticsBoardController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link HapticsBoardController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void start() throws Exception {
        LOGGER.info("Starting HapticsBoardController...");

        LOGGER.info("Starting I2C-{}...", I2C_DEVICE_INDEX);
        i2cFD = I2CNative.start(I2C_DEVICE_INDEX);
        LOGGER.info("Started I2C-{}...", I2C_DEVICE_INDEX);

        //configureIOExpander(TCA9534_P1_I2C_ADDRESS);
        //configureIOExpander(TCA9534_P2_I2C_ADDRESS);
        //configureIOExpander(TCA9534_P3_I2C_ADDRESS);
        //configureIOExpander(TCA9534_P4_I2C_ADDRESS);
        //configureIOExpander(TCA9534_P5_I2C_ADDRESS);

        //setRegisterBit(i2cFD, TCA9534_P1_I2C_ADDRESS, (short) 0x01, true, 0);
        //drv2605Calibrate();

        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x01, (byte) 0x05, true);
        LOGGER.info("Set DRV2605L to RTP mode.");

        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 0x00, true);
        LOGGER.info("Set RTP register to zero.");

        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x17, (byte) 0xFF, true);
        LOGGER.info("Set DRV2605L overdrive voltage-clamp to max value.");

        setRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1A, true, 7);
        LOGGER.info("Set DRV2605L into LRA mode.");

        resetRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1D, true, 0);
        LOGGER.info("Set DRV2605L into open-loop LRA mode.");

        //setRegisterBit(i2cFD, TCA9534_P5_I2C_ADDRESS, (short) 0x01, true, 0);
        //setRegisterBit(i2cFD, TCA9534_P5_I2C_ADDRESS, (short) 0x01, true, 1);
        //setRegisterBit(i2cFD, TCA9534_P5_I2C_ADDRESS, (short) 0x01, true, 2);
        //setRegisterBit(i2cFD, TCA9534_P5_I2C_ADDRESS, (short) 0x01, true, 3);
        //setRegisterBit(i2cFD, TCA9534_P5_I2C_ADDRESS, (short) 0x01, true, 4);
        //setRegisterBit(i2cFD, TCA9534_P5_I2C_ADDRESS, (short) 0x01, true, 5);
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 127, true);
        sleep(1000);
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 0, true);
        //for (int i = 0; i < 1000; i++) {
        //    writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 127, true);
        //    sleep(4);
        //    writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 0, true);
        //    sleep(4);
        //}

        LOGGER.info("Started HapticsBoardController.");
    }

    private void configureIOExpander(short address) throws Exception {
        writeRegisterByte(i2cFD, address, (short) 0x01, (byte) 0x00, true);
        writeRegisterByte(i2cFD, address, (short) 0x03, (byte) 0x00, true);
    }

    private void drv2605Calibrate() throws Exception {
        // Put device into calibration mode
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x01, (byte) 0x07, true);

        setRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1A, true, 7);
        LOGGER.info("Set DRV2605L into LRA mode.");

        setRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1D, true, 0);
        LOGGER.info("Set DRV2605L into open-loop LRA mode.");

        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x0C, (byte) 0x01, true); // GO bit

        while (readRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x0C, true) == 1) {
        }

        boolean fail = getRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x00, true, 3);
        if (fail) {
            LOGGER.info("FAIL");
        } else {
            LOGGER.info("PASS");
        }

        LOGGER.info("{}", getRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x00, true, 1));
        LOGGER.info("{}", getRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x00, true, 0));
    }

    /**
     * Stops {@link HapticsBoardController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void stop() throws Exception {
        LOGGER.info("Stopping HapticsBoardController...");

        if (i2cFD != null) {
            LOGGER.info("Stopping I2C-{}...", I2C_DEVICE_INDEX);
            I2CNative.stop(i2cFD);
            LOGGER.info("Stopped I2C-{}...", I2C_DEVICE_INDEX);
        }

        LOGGER.info("Stopped HapticsBoardController.");
    }
}
