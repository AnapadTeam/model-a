package tech.anapad.modela.util.i2c;

import tech.anapad.modela.util.math.BitUtil;

import static tech.anapad.modela.util.i2c.I2CNative.readRegisterByte;
import static tech.anapad.modela.util.i2c.I2CNative.writeRegisterByte;
import static tech.anapad.modela.util.math.BitUtil.getBits;
import static tech.anapad.modela.util.math.BitUtil.setBits;

/**
 * {@link I2CUtil} contains utility functions of I2C interfacing.
 */
public final class I2CUtil {

    /**
     * Sets bits in an I2C slave.
     *
     * @param fd                    the low-level I2C device file descriptor
     * @param slaveAddress          the slave address
     * @param registerAddress       the register address
     * @param is8BitRegisterAddress <code>true</code> for 8 bit register address, <code>false</code> for 16 bit
     *                              register address
     * @param value                 the value to set
     * @param msb                   the MSB (0 - 31) (inclusive)
     * @param lsb                   the LSB (0 - 31) (inclusive)
     *
     * @throws Exception thrown for {@link Exception}s
     * @see BitUtil
     * @see I2CNative
     */
    public static void setRegisterBits(int fd, short slaveAddress, short registerAddress,
            boolean is8BitRegisterAddress, int value, int msb, int lsb) throws Exception {
        byte registerByte = readRegisterByte(fd, slaveAddress, registerAddress, is8BitRegisterAddress);
        registerByte = (byte) setBits(registerByte, value, msb, lsb);
        writeRegisterByte(fd, slaveAddress, registerAddress, registerByte, is8BitRegisterAddress);
    }

    /**
     * Calls {@link #setRegisterBits(int, short, short, boolean, int, int, int)} to asset the given bit index.
     */
    public static void setRegisterBit(int fd, short slaveAddress, short registerAddress,
            boolean is8BitRegisterAddress, int index) throws Exception {
        setRegisterBits(fd, slaveAddress, registerAddress, is8BitRegisterAddress, 1, index, index);
    }

    /**
     * Calls {@link #setRegisterBits(int, short, short, boolean, int, int, int)} to reset the given bit index.
     */
    public static void resetRegisterBit(int fd, short slaveAddress, short registerAddress,
            boolean is8BitRegisterAddress, int index) throws Exception {
        setRegisterBits(fd, slaveAddress, registerAddress, is8BitRegisterAddress, 0, index, index);
    }

    /**
     * Gets bits from an I2C slave.
     *
     * @param fd                    the low-level I2C device file descriptor
     * @param slaveAddress          the slave address
     * @param registerAddress       the register address
     * @param is8BitRegisterAddress <code>true</code> for 8 bit register address, <code>false</code> for 16 bit
     *                              register address
     * @param msb                   the MSB (0 - 31) (inclusive)
     * @param lsb                   the LSB (0 - 31) (inclusive)
     *
     * @return the register bits
     * @throws Exception thrown for {@link Exception}s
     * @see BitUtil
     * @see I2CNative
     */
    public static int getRegisterBits(int fd, short slaveAddress, short registerAddress,
            boolean is8BitRegisterAddress, int msb, int lsb) throws Exception {
        return getBits(readRegisterByte(fd, slaveAddress, registerAddress, is8BitRegisterAddress), msb, lsb);
    }

    /**
     * Calls {@link #getRegisterBits(int, short, short, boolean, int, int)} with the given bit index.
     */
    public static boolean getRegisterBit(int fd, short slaveAddress, short registerAddress,
            boolean is8BitRegisterAddress, int index) throws Exception {
        return getRegisterBits(fd, slaveAddress, registerAddress, is8BitRegisterAddress, index, index) == 1;
    }
}
