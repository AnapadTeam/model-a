#!/bin/bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_ROOT_DIRECTORY="${SCRIPT_DIRECTORY}/../../../"

if [[ $# -ne 1 ]]; then
    echo "You must supply the IP address of the target as an argument."
    exit 1
fi

rsync -alPz --delete --exclude-from="${SCRIPT_DIRECTORY}/to_target_exclude.txt" "${PROJECT_ROOT_DIRECTORY}" "root@${1}:~/modela/"
ssh "root@${1}" '/bin/bash -c "chown -R root:root ~/modela/"'
