#!/bin/bash

set -e # Exit script on command failure

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_ROOT_DIRECTORY="${SCRIPT_DIRECTORY}/../../../"

if [[ "${EUID}" -ne 0 ]]; then
    echo "This script must be run as the root user."
    exit 1
fi

source ~/.jabba/jabba.sh
jabba use liberica@1.14.0-2

export ENABLE_GLUON_COMMERCIAL_EXTENSIONS=true
exec "${PROJECT_ROOT_DIRECTORY}/firmware/core/build/install/model-a/bin/model-a" $@
