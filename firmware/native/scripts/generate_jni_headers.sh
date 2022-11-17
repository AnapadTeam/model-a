#!/bin/bash

SOURCE_DIRECTORY="src/"
if [[ ! -d "$SOURCE_DIRECTORY" ]]; then
    echo "This script must be called from the project directory (e.g. where the \"${SOURCE_DIRECTORY}\" directory is)."
    exit 1
fi

CORE_DIRECTORY="../core/"
TARGET_C_HEADER_FILE="src/jni/"

javac "${CORE_DIRECTORY}/src/main/java/tech/anapad/modela/util/i2c/I2CNative.java" -h "${TARGET_C_HEADER_FILE}"
