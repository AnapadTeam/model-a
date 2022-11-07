#!/bin/bash

set -e # Exit script on command failure

if [[ "${EUID}" -ne 0 ]]; then
    echo "This script must be run as the root user."
    exit 1
fi

ping -q -w 1 -c 1 `ip r | grep default | cut -d ' ' -f 3` > /dev/null
if [[ $? -ne 0 ]]; then
    echo "This script requires an internet connection."
    exit 1
fi

PROJECT_ROOT="$(dirname "$0")/../../../"
cd "${PROJECT_ROOT}"

echo "Upgrading packages..."
apt update
apt upgrade -y
apt autoremove --purge -y
echo "Upgraded packages."

echo "Installing hardware-accelerated rendering libraries used by JavaFX..."
apt install libegl-mesa0 libegl1 libgbm1 libgles2 libpangoft2-1.0-0 -y
mkdir -p /opt/java/
cp core/scripts/install/assets/libgluon_drm-1.1.7.so /opt/java/
echo "Installed hardware-accelerated rendering libraries used by JavaFX."

echo "Installing Java..."
curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
jabba install liberica@1.14.0-2 # Install JDK 14 via Jabba
jabba use liberica@1.14.0-2
echo "Installed Java."

echo "Building and installing native firmware..."
cd native/
mkdir -p build/
cmake -B build/ -S .
make -C build/
cp ./build/libmodela.so /lib/
cd "${PROJECT_ROOT}"
echo "Built and installed native firmware."

echo "Building and installing core firmware..."
cd core
./gradlew build
echo "Built and installed core firmware."

echo "Updating /boot/config.txt..."
cp core/scripts/install/assets/config.txt /boot/config.txt
echo "Updated /boot/config.txt."

echo "Disabling boot messages and login prompt..."
if [[ ! grep -q "consoleblank=1" "/boot/cmdline.txt" ]]; then
    sed -i '$ s/$/ consoleblank=1 logo.nologo quiet loglevel=0 plymouth.enable=0 vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fastboot noatime nodiratime noram/' /boot/cmdline.txt
fi
systemctl stop getty@tty1.service
systemctl disable getty@tty1.service
echo "Disabled boot messages and login prompt."

echo "Rebooting in 5 seconds..."
sleep 5
reboot
