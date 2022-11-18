package tech.anapad.modela.view.mode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.touchscreen.model.Touch;
import tech.anapad.modela.usb.USBController;
import tech.anapad.modela.usb.model.HIDReport;
import tech.anapad.modela.view.ViewController;

import java.io.IOException;

import static tech.anapad.modela.view.ViewController.VIEW_IMAGE_CLASSPATH;
import static tech.anapad.modela.view.model.AnapadMode.KEYBOARD;
import static tech.anapad.modela.view.model.AnapadMode.TRACKPAD;

/**
 * {@link KeyboardModeView} is a {@link AbstractModeView} for keyboard mode.
 */
public class KeyboardModeView extends AbstractModeView {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeyboardModeView.class);

    private final USBController usbController;

    // TODO Below is just test code. Rewrite!!!

    /**
     * Instantiates a new {@link KeyboardModeView}.
     *
     * @param viewController the {@link ViewController}
     */
    public KeyboardModeView(ViewController viewController) {
        super(viewController, VIEW_IMAGE_CLASSPATH + "mode_keyboard.png");
        usbController = viewController.getModelA().getUSBController();
    }

    @Override
    public void processTouches(Touch[] touches) {
        if (touches.length > 0) {
            final int x = touches[0].getX();
            final int y = touches[0].getY();
            if (x > 173 && x < 276 && y > 180 && y < 281) {
                final HIDReport report = new HIDReport();
                report.setKeycode1((byte) 0x04); // 'a' key
                try {
                    usbController.writeHIDReport(report);
                } catch (InterruptedException | IOException e) {
                    LOGGER.error("Could not write HID report!", e);
                }
            } else if (x > 820 && x < 1058 && y > 96 && y < 420) {
                viewController.setMode(TRACKPAD);
            }
        } else {
            try {
                usbController.writeHIDReport(new HIDReport());
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Could not write HID report!", e);
            }
        }
    }
}
