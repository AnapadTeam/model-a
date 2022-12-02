package tech.anapad.modela.usb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;

import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.Thread.sleep;
import static tech.anapad.modela.util.exception.ExceptionUtil.ignoreException;
import static tech.anapad.modela.util.file.FileUtil.exists;
import static tech.anapad.modela.util.file.FileUtil.makePath;
import static tech.anapad.modela.util.file.FileUtil.removePath;
import static tech.anapad.modela.util.file.FileUtil.symlink;
import static tech.anapad.modela.util.file.FileUtil.writeBytes;
import static tech.anapad.modela.util.file.FileUtil.writeString;

/**
 * {@link USBController} is a controller for the USB HID interface and communication channel.
 */
public class USBController {

    private static final Logger LOGGER = LoggerFactory.getLogger(USBController.class);

    // Define configfs string constants
    private static final String KERNEL_CONFIG_USB_GADGET = "/sys/kernel/config/usb_gadget/";
    private static final String GADGET_NAME = "model_a/";
    private static final String GADGET_PATH = KERNEL_CONFIG_USB_GADGET + GADGET_NAME;
    private static final String GADGET_EN_STRINGS_PATH = GADGET_PATH + "strings/0x409/";
    private static final String GADGET_CONFIG_1_PATH = GADGET_PATH + "configs/c.1/";
    private static final String GADGET_CONFIG_1_EN_STRINGS_PATH = GADGET_CONFIG_1_PATH + "strings/0x409/";
    private static final String GADGET_HID_FUNCTIONS_0_NAME = "hid.0";
    private static final String GADGET_HID_FUNCTIONS_0_PATH = GADGET_PATH + "functions/" +
            GADGET_HID_FUNCTIONS_0_NAME + "/";
    private static final String GADGET_UDC_PATH = GADGET_PATH + "UDC";
    private static final String GADGET_UDC = "fe980000.usb"; // Name of the RPi CM4 UDC
    private static final String GADGET_HID_DEVICE_PATH = "/dev/hidg0";

    private final ModelA modelA;
    private FileOutputStream hidDeviceOutputStream;

    /**
     * Instantiates a new {@link USBController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public USBController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link USBController}.
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void start() throws Exception {
        LOGGER.info("Starting USBController...");
        initializeUSBGadget();
        LOGGER.info("Started USBController.");
    }

    /**
     * Creates the USB HID interface via Linux GadgetFS.
     *
     * @throws InterruptedException thrown for {@link InterruptedException}s
     * @throws IOException          thrown for {@link IOException}s
     */
    private void initializeUSBGadget() throws InterruptedException, IOException {
        LOGGER.info("Initializing USB Gadget via Linux configfs...");

        // The following USB HID report descriptor contains two usages with the first being
        // the mouse and the second being the keyboard.
        final int[] hidReportDescriptor = new int[]{
                0x05, 0x01, // Usage Page (Generic Desktop)
                0x09, 0x02, // Usage (Mouse)
                0xA1, 0x01, // Collection (Application)
                0x09, 0x01, //   Usage (Pointer)
                0xA1, 0x00, //   Collection (Physical)
                0x05, 0x09, //     Usage Page (Button)
                0x19, 0x01, //     Usage Minimum (0x01)
                0x29, 0x03, //     Usage Maximum (0x03)
                0x15, 0x00, //     Logical Minimum (0)
                0x25, 0x01, //     Logical Maximum (1)
                0x95, 0x03, //     Report Count (3)
                0x75, 0x01, //     Report Size (1)
                0x81, 0x02, //     Input (Data,Var,Abs)
                0x95, 0x01, //     Report Count (1)
                0x75, 0x05, //     Report Size (5)
                0x81, 0x03, //     Input (Const,Array,Abs)
                0x05, 0x01, //     Usage Page (Generic Desktop)
                0x09, 0x30, //     Usage (X)
                0x09, 0x31, //     Usage (Y)
                0x09, 0x38, //     Usage (Wheel)
                0x15, 0x81, //     Logical Minimum (-127)
                0x25, 0x7F, //     Logical Maximum (127)
                0x75, 0x08, //     Report Size (8)
                0x95, 0x03, //     Report Count (3)
                0x81, 0x06, //     Input (Data,Var,Rel)
                0xC0, //         End Collection
                0xC0, //        End Collection
                0x05, 0x01,  // Usage Page (Generic Desktop)
                0x09, 0x06,  // Usage (Keyboard)
                0xA1, 0x01,  // Collection (Application)
                0x05, 0x07,  //   Usage Page (Keyboard)
                0x19, 0xE0,  //   Usage Minimum (Keyboard LeftControl)
                0x29, 0xE7,  //   Usage Maximum (Keyboard Right GUI)
                0x15, 0x00,  //   Logical Minimum (0)
                0x25, 0x01,  //   Logical Maximum (1)
                0x75, 0x01,  //   Report Size (1)
                0x95, 0x08,  //   Report Count (8)
                0x81, 0x02,  //   Input (Data,Var,Abs)
                0x95, 0x01,  //   Report Count (1)
                0x75, 0x08,  //   Report Size (8)
                0x81, 0x03,  //   Input (Const,Var,Abs)
                0x95, 0x05,  //   Report Count (5)
                0x75, 0x01,  //   Report Size (1)
                0x05, 0x08,  //   Usage Page (LEDs)
                0x19, 0x01,  //   Usage Minimum (Num Lock)
                0x29, 0x05,  //   Usage Maximum (Kana)
                0x91, 0x02,  //   Output (Data,Var,Abs)
                0x95, 0x01,  //   Report Count (1)
                0x75, 0x03,  //   Report Size (3)
                0x91, 0x03,  //   Output (Const,Var,Abs)
                0x95, 0x06,  //   Report Count (6)
                0x75, 0x08,  //   Report Size (8)
                0x15, 0x00,  //   Logical Minimum (0)
                0x25, 0x65,  //   Logical Maximum (101)
                0x05, 0x07,  //   Usage Page (Keyboard)
                0x19, 0x00,  //   Usage Minimum (Reserved (no event indicated))
                0x29, 0x65,  //   Usage Maximum (Keyboard Application)
                0x81, 0x00,  //   Input (Data,Ary,Abs)
                0xC0 //         End Collection
        };
        // Convert report descriptor ints to bytes
        final byte[] hidReportDescriptorBytes = new byte[hidReportDescriptor.length];
        for (int index = 0; index < hidReportDescriptor.length; index++) {
            hidReportDescriptorBytes[index] = (byte) (hidReportDescriptor[index] & 0xFF);
        }

        // Create configfs USB gadget files
        if (exists(GADGET_PATH)) {
            LOGGER.warn("Removing stale USB gadget path: {}", GADGET_PATH);
            removeUSBGadget();
        }
        final int gadgetPathAttemptsMax = 20;
        int gadgetPathAttempts = 0;
        while (gadgetPathAttempts < gadgetPathAttemptsMax) {
            if (makePath(GADGET_PATH)) {
                break;
            }
            sleep(250);
            gadgetPathAttempts++;
        }
        if (gadgetPathAttempts == gadgetPathAttemptsMax) {
            throw new RuntimeException("Gadget path creation attempts exceeded maximum!");
        }

        // Populate configs USB gadget files

        writeString(GADGET_PATH + "idVendor", "0x1d6b"); // Linux Foundation
        writeString(GADGET_PATH + "idProduct", "0x0104"); // Multifunction Composite Gadget
        writeString(GADGET_PATH + "bcdDevice", "0x0000"); // v0.0.0
        writeString(GADGET_PATH + "bcdUSB", "0x0200"); // USB2
        writeString(GADGET_PATH + "max_speed", "full-speed"); // USB Full-Speed 12Mb/s

        makePath(GADGET_EN_STRINGS_PATH);
        writeString(GADGET_EN_STRINGS_PATH + "serialnumber", "A00000000");
        writeString(GADGET_EN_STRINGS_PATH + "manufacturer", "Anapad Team");
        writeString(GADGET_EN_STRINGS_PATH + "product", "Model A Anapad");

        makePath(GADGET_CONFIG_1_EN_STRINGS_PATH);
        writeString(GADGET_CONFIG_1_EN_STRINGS_PATH + "configuration", "Model A Anapad Config");
        writeString(GADGET_CONFIG_1_PATH + "MaxPower", "250"); // 250mA

        makePath(GADGET_HID_FUNCTIONS_0_PATH);
        writeString(GADGET_HID_FUNCTIONS_0_PATH + "protocol", "0"); // 1 = keyboard, 2 = mouse
        writeString(GADGET_HID_FUNCTIONS_0_PATH + "subclass", "1"); // 0 = no boot, 1 = boot
        writeString(GADGET_HID_FUNCTIONS_0_PATH + "report_length", "12"); // Report length is 12 bytes
        writeBytes(GADGET_HID_FUNCTIONS_0_PATH + "report_desc", hidReportDescriptorBytes);
        symlink(GADGET_CONFIG_1_PATH + GADGET_HID_FUNCTIONS_0_NAME, GADGET_HID_FUNCTIONS_0_PATH);

        writeString(GADGET_UDC_PATH, GADGET_UDC); // Enable the gadget

        LOGGER.info("Initialized USB Gadget via Linux configfs.");
    }

    /**
     * Stops {@link USBController}.
     */
    public void stop() {
        LOGGER.info("Stopping USBController...");

        if (hidDeviceOutputStream != null) {
            LOGGER.info("Closing HID device output stream...");
            try {
                hidDeviceOutputStream.close();
                LOGGER.info("Closed HID device output stream.");
            } catch (IOException ioException) {
                LOGGER.error("Could not close HID device output stream!", ioException);
            }
        }

        removeUSBGadget();

        LOGGER.info("Stopped USBController.");
    }

    /**
     * Removes the USB HID interface via Linux GadgetFS. This method with never thrown an {@link Exception}.
     */
    private void removeUSBGadget() {
        LOGGER.info("Removing USB Gadget from Linux configfs...");
        ignoreException(() -> writeString(GADGET_UDC_PATH, ""));
        ignoreException(() -> removePath(GADGET_CONFIG_1_PATH + GADGET_HID_FUNCTIONS_0_NAME));
        ignoreException(() -> removePath(GADGET_CONFIG_1_EN_STRINGS_PATH));
        ignoreException(() -> removePath(GADGET_CONFIG_1_PATH));
        ignoreException(() -> removePath(GADGET_HID_FUNCTIONS_0_PATH));
        ignoreException(() -> removePath(GADGET_EN_STRINGS_PATH));
        ignoreException(() -> removePath(GADGET_PATH));
        LOGGER.info("Removed USB Gadget from Linux configfs.");
    }

    /**
     * Write an {@link Report}. <code>10</code> attempts are made with a <code>100ms</code> period before giving up and
     * throwing an {@link IOException}.
     *
     * @param report the {@link Report}
     *
     * @throws InterruptedException thrown for {@link InterruptedException}s
     * @throws IOException          thrown for {@link IOException}s
     */
    public void writeReport(Report report) throws InterruptedException, IOException {
        if (hidDeviceOutputStream == null) {
            hidDeviceOutputStream = new FileOutputStream(GADGET_HID_DEVICE_PATH);
        }
        final int hidDeviceWriteAttemptsMax = 10;
        int hidDeviceWriteAttempts = 0;
        while (hidDeviceWriteAttempts < hidDeviceWriteAttemptsMax) {
            try {
                hidDeviceOutputStream.write(report.toByteArray());
                break;
            } catch (Exception exception) {
                LOGGER.warn("Failed to write to USB HID device file on attempt {}.",
                        hidDeviceWriteAttempts + 1, exception);
            }
            sleep(100);
            hidDeviceWriteAttempts++;
        }
        if (hidDeviceWriteAttempts == hidDeviceWriteAttemptsMax) {
            throw new IOException("USB HID device file write attempts exceeded maximum!");
        }
    }

    public ModelA getModelA() {
        return modelA;
    }
}
