package tech.anapad.modela.util.i2c;

import static java.lang.System.loadLibrary;

/**
 * {@link I2CNative} is used to interface with the I2C protocol using low-level native C functions via JNI.
 */
public final class I2CNative {

    static {
        // Load the .so shared library that should be placed in '/lib' and be named exactly "libmodela.so"
        loadLibrary("modela");
    }

    /**
     * Starts the low-level I2C interface.
     *
     * @param i2cDeviceIndex the I2C linux sysfs device index (e.g. for "/dev/i2c-1" pass <code>1</code> here)
     *
     * @return the device file descriptor integer of the low-level linux sysfs device path to be used in subsequent I2C
     * requests
     * @throws Exception thrown for {@link Exception}s
     */
    public static native int start(int i2cDeviceIndex) throws Exception;

    /**
     * Stops the low-level I2C interface.
     *
     * @param fd the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public static native void stop(int fd) throws Exception;

    /**
     * Writes a byte an I2C slave.
     *
     * @param fd           the low-level I2C device file descriptor
     * @param slaveAddress the slave address
     * @param data         the byte to write
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public static native void writeByte(int fd, short slaveAddress, byte data) throws Exception;

    /**
     * Writes a byte to a register of an I2C slave.
     *
     * @param fd                    the low-level I2C device file descriptor
     * @param slaveAddress          the slave address
     * @param registerAddress       the register address
     * @param registerData          the register data
     * @param is8BitRegisterAddress <code>true</code> for 8 bit register address, <code>false</code> for 16 bit
     *                              register address
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public static native void writeRegisterByte(int fd, short slaveAddress, short registerAddress, byte registerData,
            boolean is8BitRegisterAddress) throws Exception;

    /**
     * Writes an array of bytes to the registers of an I2C slave.
     *
     * @param fd                    the low-level I2C device file descriptor
     * @param slaveAddress          the slave address
     * @param registerAddress       the register address
     * @param registerData          the register data
     * @param is8BitRegisterAddress <code>true</code> for 8 bit register address, <code>false</code> for 16 bit
     *                              register address
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public static native void writeRegisterBytes(int fd, short slaveAddress, short registerAddress,
            byte[] registerData, boolean is8BitRegisterAddress) throws Exception;

    /**
     * Reads a byte from an I2C slave.
     *
     * @param fd           the low-level I2C device file descriptor
     * @param slaveAddress the slave address
     *
     * @return the read byte
     * @throws Exception thrown for {@link Exception}s
     */
    public static native byte readByte(int fd, short slaveAddress) throws Exception;

    /**
     * Reads a byte from a register of an I2C slave.
     *
     * @param fd                    the low-level I2C device file descriptor
     * @param slaveAddress          the slave address
     * @param registerAddress       the register address
     * @param is8BitRegisterAddress <code>true</code> for 8 bit register address, <code>false</code> for 16 bit
     *                              register address
     *
     * @return the read register byte
     * @throws Exception thrown for {@link Exception}s
     */
    public static native byte readRegisterByte(int fd, short slaveAddress, short registerAddress,
            boolean is8BitRegisterAddress) throws Exception;

    /**
     * Reads an array of bytes from the registers of an I2C slave.
     *
     * @param fd                    the low-level I2C device file descriptor
     * @param slaveAddress          the slave address
     * @param registerAddress       the register address
     * @param readSize              the number of bytes to read
     * @param is8BitRegisterAddress <code>true</code> for 8 bit register address, <code>false</code> for 16 bit
     *                              register address
     *
     * @return the read register byte array
     * @throws Exception thrown for {@link Exception}s
     */
    public static native byte[] readRegisterBytes(int fd, short slaveAddress, short registerAddress, int readSize,
            boolean is8BitRegisterAddress) throws Exception;
}
