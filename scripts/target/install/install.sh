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

PROJECT_ROOT=$(realpath "$(dirname "$0")/../../../")
cd "${PROJECT_ROOT}"
chown -R root:root .

echo "Upgrading packages..."
apt update
apt upgrade -y
apt autoremove --purge -y
echo "Upgraded packages."

echo "Installing build tools..."
apt install cmake -y
echo "Installed build tools."

echo "Installing OpenJFX and hardware-accelerated rendering libraries used by JavaFX..."
apt install openjfx libegl-mesa0 libegl1 libgbm1 libgles2 libpangoft2-1.0-0 -y
mkdir -p /opt/java/
cp scripts/target/install/assets/libgluon_drm-1.1.7.so /opt/java/
chmod 755 /opt/java/libgluon_drm-1.1.7.so
echo "Installed OpenJFX hardware-accelerated rendering libraries used by JavaFX."

echo "Installing Java..."
curl -sL https://github.com/shyiko/jabba/raw/master/install.sh | bash && . ~/.jabba/jabba.sh
jabba install liberica@1.14.0-2 # Install JDK 14 via Jabba
jabba use liberica@1.14.0-2
echo "Installed Java."

echo "Building and installing native firmware..."
cd "${PROJECT_ROOT}"
cd firmware/native/
rm -rf build/
mkdir build/
cmake -B build/ -S .
make -C build/
cp ./build/libmodela.so /lib/
echo "Built and installed native firmware."

echo "Building and installing core firmware..."
cd "${PROJECT_ROOT}"
cd firmware/core/
rm -rf build/
./gradlew build --no-daemon
echo "Built and installed core firmware."

echo "Enabling libcomposite module..."
modprobe libcomposite
echo "Enabled libcomposite module."

echo "Updating /boot/config.txt..."
cd "${PROJECT_ROOT}"
cp scripts/target/install/assets/config.txt /boot/config.txt
chmod 755 /boot/config.txt
echo "Updated /boot/config.txt."

echo "Disabling boot messages and login prompt..."
set +e
grep -q "consoleblank=1" "/boot/cmdline.txt"
if [[ $? == 1 ]]; then
    sed -i '$s/$/ consoleblank=1 logo.nologo quiet loglevel=0 plymouth.enable=0 vt.global_cursor_default=0 plymouth.ignore-serial-consoles splash fastboot noatime nodiratime noram/' /boot/cmdline.txt
fi
set -e
systemctl stop getty@tty1.service
systemctl disable getty@tty1.service
echo "Disabled boot messages and login prompt."

echo "Setting firmware as startup program..."
cd "${PROJECT_ROOT}"
cp scripts/target/install/assets/model-a.service /etc/systemd/system
chmod 644 /etc/systemd/system/model-a.service
systemctl daemon-reload
systemctl enable model-a.service
echo "Set firmware as startup program."

echo "Rebooting in 5 seconds..."
sleep 5
reboot
