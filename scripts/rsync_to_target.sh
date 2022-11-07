#!/bin/bash

PROJECT_ROOT="$(dirname "$0")/../"
cd "${PROJECT_ROOT}"

if [[ $# -ne 1 ]]; then
    echo "You must supply the IP address of the target as an argument."
    exit 1
fi

rsync -alPvz --delete --exclude-from="rsync_to_target_exclude.txt" firmware "root@${2}:~"
