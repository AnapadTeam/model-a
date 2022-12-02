package tech.anapad.modela.hapticsboard.hapticmotorcontroller;

import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.i2c.I2CUtil.resetRegisterBit;
import static tech.anapad.modela.util.i2c.I2CUtil.setRegisterBit;

/**
 * {@link HapticMotorController} represents the DRV2605 haptic motor controller chip.
 */
public class HapticMotorController {

    private static final short DRV2605L_I2C_ADDRESS = 0x5A;

    private final int i2cFD;

    private boolean rtpModeEnabled;
    private byte rtpValue;

    /**
     * Instantiates a new {@link HapticMotorController}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     */
    public HapticMotorController(int i2cFD) {
        this.i2cFD = i2cFD;
        rtpModeEnabled = false;
        rtpValue = 0;
    }

    /**
     * Configures this {@link HapticMotorController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void configure() throws Exception {
        // Zero RTP value
        setRTPValue((byte) 0);
        // Set RTP mode
        setRTPMode(true);
        // Set overdrive voltage-clamp to max value
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x17, (byte) 0xFF, true);
        // Set LRA mode
        setRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1A, true, 7);
        // Set open-loop LRA mode
        resetRegisterBit(i2cFD, DRV2605L_I2C_ADDRESS, (short) 0x1D, true, 0);
        // No need to set DRV2605 resonant frequency as it defaults to 205Hz which is what the resonant frequency
        // of the LRAs on the haptics board are.
    }

    /**
     * Set Real-Time Playback (RTP) mode.
     *
     * @param set <code>true</code> to set, <code>false</code> otherwise
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void setRTPMode(boolean set) throws Exception {
        if (set) {
            writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x01, (byte) 0x05, true);
        } else {
            writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x01, (byte) 0x00, true);
        }
        rtpModeEnabled = set;
    }

    /**
     * Sets the Real-Time Playback (RTP) value.
     *
     * @param value the RTP value
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void setRTPValue(byte value) throws Exception {
        writeRegisterByte(i2cFD, DRV2605L_I2C_ADDRESS, (byte) 0x02, value, true);
        rtpValue = value;
    }

    public boolean isRTPModeEnabled() {
        return rtpModeEnabled;
    }

    public byte getRTPValue() {
        return rtpValue;
    }
}
