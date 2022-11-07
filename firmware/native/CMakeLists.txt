cmake_minimum_required(VERSION 3.18)
set(CMAKE_C_STANDARD 99)
project(model_a)

# Replace the path below with the path to the Linux kernel include directory (e.g. linux-5.18.9/include/) if you would
# like to have IDE code hints for Linux headers and such.
#include_directories(<path here>)

# Include Java JNI headers
set(JAVA_INCLUDE_PATH "$ENV{JAVA_HOME}/include")
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# Build shared library
add_library(modela SHARED
        src/util/lang/lang.h
        src/util/i2c/i2c.h
        src/util/i2c/i2c.c)
