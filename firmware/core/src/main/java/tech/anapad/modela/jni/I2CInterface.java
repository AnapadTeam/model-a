package tech.anapad.modela.jni;

/**
 * {@link I2CInterface} is used to interface with the I2C protocol using low-level C functions via JNI.
 */
public class I2CInterface {

    static {
        // Load the .so shared library that should be placed in '/lib' and be named exactly "libmodela.so"
        System.loadLibrary("modela");
    }

    /**
     * Starts the low-level I2C interface.
     *
     * @param devPath the linux sysfs device path
     *
     * @return the file descriptor integer of the low-level linux sysfs device path which is used in subsequent I2C
     * requests
     * @throws Exception thrown for {@link Exception}s
     */
    public static native int i2cStart(String devPath) throws Exception;

    /**
     * Stops the low-level I2C interface.
     *
     * @param devPath the linux sysfs device path
     *
     * @return the file descriptor integer of the low-level linux sysfs device path which is used in subsequent I2C
     * requests
     * @throws Exception thrown for {@link Exception}s
     */
    public static native int i2cStop(String devPath) throws Exception;

    /**
     * Writes a byte an I2C slave.
     *
     * @param fd           the low-level I2C device file descriptor
     * @param slaveAddress the slave address
     * @param data         the byte to write
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public static native void i2cWriteByte(int fd, short slaveAddress, byte data) throws Exception;

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
    public static native void i2cWriteRegisterByte(int fd, short slaveAddress, short registerAddress, byte registerData,
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
    public static native void i2cWriteRegisterBytes(int fd, short slaveAddress, short registerAddress,
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
    public static native byte i2cReadByte(int fd, short slaveAddress) throws Exception;

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
    public static native byte i2cReadRegisterByte(int fd, short slaveAddress, short registerAddress,
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
    public static native byte[] i2cReadRegisterBytes(int fd, short slaveAddress, short registerAddress, int readSize,
            boolean is8BitRegisterAddress) throws Exception;
}
