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

    }

    /**
     * Stops {@link USBController}.
     */
    public void stop() {

    }
}
