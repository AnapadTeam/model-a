/**
 * @file tech_anapad_modela_util_i2c_I2CNative.
 */

#include "tech_anapad_modela_util_i2c_I2CNative.h"
#include "../util/i2c/i2c.h"

JNIEXPORT jint JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_start(JNIEnv* env, jclass class,
        jint i2c_device_index) {
    // Concat device index with device path prefix
    const char* device_path_prefix = "/dev/i2c-";
    char device_path[strlen((const char*) device_path_prefix) + 2];
    snprintf(device_path, SIZE_OF_ARRAY(device_path), "%s%d", device_path_prefix, i2c_device_index);

    // Start I2C
    const uint32_t fd = i2c_start(device_path);
    if (fd < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not start I2C device!");
    }
    return fd;
}

JNIEXPORT void JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_stop(JNIEnv* env, jclass class, jint fd) {
    // Stop I2C
    const int32_t status = i2c_stop(fd);
    if (status < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not stop I2C device!");
    }
}

JNIEXPORT void JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_writeByte(JNIEnv* env, jclass class, jint fd,
        jshort slave_address, jbyte data) {
    const int32_t status = i2c_write_byte(fd, slave_address, data);
    if (status < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not write to I2C device!");
    }
}

JNIEXPORT void JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_writeRegisterByte(JNIEnv* env, jclass class, jint fd,
        jshort slave_address, jshort register_address, jbyte register_data, jboolean is_8_bit_register_address) {
    const int32_t status =
            i2c_write_register_byte(fd, slave_address, register_address, register_data, is_8_bit_register_address);
    if (status < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not write to I2C device!");
    }
}

JNIEXPORT void JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_writeRegisterBytes(JNIEnv* env, jclass class, jint fd,
        jshort slave_address, jshort register_address, jbyteArray register_data, jboolean is_8_bit_register_address) {
    int8_t* register_data_array = (*env)->GetByteArrayElements(env, register_data, 0);
    const int32_t register_data_size = (*env)->GetArrayLength(env, register_data);
    const int32_t status = i2c_write_register_bytes(fd, slave_address, register_address, (uint8_t*) register_data_array,
            register_data_size, is_8_bit_register_address);
    (*env)->ReleaseByteArrayElements(env, register_data, register_data_array, 0);
    if (status < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not write to I2C device!");
    }
}

JNIEXPORT jbyte JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_readByte(JNIEnv* env, jclass class, jint fd,
        jshort slave_address) {
    const int32_t byte = i2c_read_byte(fd, slave_address);
    if (byte < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not read from I2C device!");
    }
    return (int8_t) byte;
}

JNIEXPORT jbyte JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_readRegisterByte(JNIEnv* env, jclass class, jint fd,
        jshort slave_address, jshort register_address, jboolean is_8_bit_register_address) {
    const int32_t register_byte =
            i2c_read_register_byte(fd, slave_address, register_address, is_8_bit_register_address);
    if (register_byte < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not read from I2C device!");
    }
    return (int8_t) register_byte;
}

JNIEXPORT jbyteArray JNICALL Java_tech_anapad_modela_util_i2c_I2CNative_readRegisterBytes(JNIEnv* env, jclass class,
        jint fd, jshort slave_address, jshort register_address, jint read_size, jboolean is_8_bit_register_address) {
    const int8_t register_bytes[read_size];
    const int32_t status = i2c_read_register_bytes(fd, slave_address, register_address, (uint8_t*) register_bytes,
            SIZE_OF_ARRAY(register_bytes), is_8_bit_register_address);
    if (status < 0) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Could not read from I2C device!");
    }
    const jbyteArray register_bytes_java = (*env)->NewByteArray(env, read_size);
    if (register_bytes_java == NULL) {
        (*env)->ThrowNew(env, (*env)->FindClass(env, "java/lang/Exception"), "Out of memory error!");
    }
    (*env)->SetByteArrayRegion(env, register_bytes_java, 0, SIZE_OF_ARRAY(register_bytes), register_bytes);
    return register_bytes_java;
}
