package tech.anapad.modela.usb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;

/**
 * {@link USBController} is a controller for the USB HID interface and communication channel.
 */
public class USBController {

    private static final Logger LOGGER = LoggerFactory.getLogger(USBController.class);

    private final ModelA modelA;

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
     */
    public void start() {
        LOGGER.info("Starting USBController...");
        initializeUSBGadget();
        LOGGER.info("Started USBController.");
    }

    /**
     * Creates the USB HID interface via Linux GadgetFS.
     */
    private void initializeUSBGadget() {
        // Define configfs string constants
        final String kernelConfigPath = "/sys/kernel/config/usb_gadget/";
        final String gadgetName = "modela/";
        final String gadgetPath = kernelConfigPath + gadgetName;
        final String enStringsPath = gadgetPath + "strings/0x409/";
        final String config1Path = gadgetPath + "configs/c.1/";
        final String config1EnStringsPath = config1Path + "strings/0x409/";
        final String functions0Name = "hid.0";
        final String functions0Path = gadgetPath + "functions/" + functions0Name + "/";
        final String udcName = "fe980000.usb"; // Name of the RPi CM4 UDC

        // The following USB HID report descriptor contains two usages with the first being
        // the mouse and the second being the keyboard.
        final int[] hidReportDescriptor = new int[]{
                0x05, 0x01, // Usage Page (Generic Desktop)
                0x09, 0x02, // Usage (Mouse)
                0xa1, 0x01, // Collection (Application)
                0x09, 0x01, //   Usage (Pointer)
                0xa1, 0x00, //   Collection (Physical)
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
                0x25, 0x7f, //     Logical Maximum (127)
                0x75, 0x08, //     Report Size (8)
                0x95, 0x03, //     Report Count (3)
                0x81, 0x06, //     Input (Data,Var,Rel)
                0xc0, //         End Collection
                0xc0, //        End Collection
                0x05, 0x01,  // Usage Page (Generic Desktop)
                0x09, 0x06,  // Usage (Keyboard)
                0xa1, 0x01,  // Collection (Application)
                0x05, 0x07,  //   Usage Page (Keyboard)
                0x19, 0xe0,  //   Usage Minimum (Keyboard LeftControl)
                0x29, 0xe7,  //   Usage Maximum (Keyboard Right GUI)
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
                0xc0 //         End Collection
        };
        // Concert report descriptor ints to bytes
        final byte[] hidReportDescriptorBytes = new byte[hidReportDescriptor.length];
        for (int index = 0; index < hidReportDescriptor.length; index++) {
            hidReportDescriptorBytes[index] = (byte) (hidReportDescriptor[index] & 0xFF);
        }
    }

    /**
     * Stops {@link USBController}.
     */
    public void stop() {

    }

    public ModelA getModelA() {
        return modelA;
    }
}
