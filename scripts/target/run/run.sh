#!/bin/bash

set -e # Exit script on command failure

if [[ "${EUID}" -ne 0 ]]; then
    echo "This script must be run as the root user."
    exit 1
fi

source ~/.jabba/jabba.sh
jabba use liberica@1.14.0-2

export ENABLE_GLUON_COMMERCIAL_EXTENSIONS=true
exec ./build/install/modela/bin/modela $@
