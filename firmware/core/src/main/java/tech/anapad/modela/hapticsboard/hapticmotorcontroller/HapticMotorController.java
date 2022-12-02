package tech.anapad.modela.hapticsboard.hapticmotorcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.i2c.I2CUtil.resetRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.setRegisterBit;

/**
 * {@link HapticMotorController} represents the DRV2605 haptic motor controller chip.
 */
public class HapticMotorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HapticMotorController.class);

    /**
     * Configures this {@link HapticMotorController}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void configure(int i2cFD) throws Exception {
        //writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x01, (byte) 0x05, true);
        //LOGGER.info("Set RTP mode.");
        //writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, (byte) 0x00, true);
        //LOGGER.info("Set RTP register to zero.");
        //writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x17, (byte) 0xFF, true);
        //LOGGER.info("Set overdrive voltage-clamp to max value.");
        //setRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1A, true, 7);
        //LOGGER.info("Set LRA mode.");
        //resetRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1D, true, 0);
        //LOGGER.info("Set open-loop LRA mode.");
    }
}
