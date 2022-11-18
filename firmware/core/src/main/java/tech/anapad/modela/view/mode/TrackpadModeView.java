package tech.anapad.modela.view.mode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.touchscreen.model.Touch;
import tech.anapad.modela.usb.USBController;
import tech.anapad.modela.usb.model.HIDReport;
import tech.anapad.modela.view.ViewController;

import java.io.IOException;

import static tech.anapad.modela.util.math.MathUtil.clamp;
import static tech.anapad.modela.view.ViewController.VIEW_IMAGE_CLASSPATH;
import static tech.anapad.modela.view.model.AnapadMode.KEYBOARD;

/**
 * {@link TrackpadModeView} is a {@link AbstractModeView} for trackpad mode.
 */
public class TrackpadModeView extends AbstractModeView {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackpadModeView.class);

    private final USBController usbController;

    // TODO Below is just test code. Rewrite!!!

    int touchscreenTouchLastX = -1;
    int touchscreenTouchLastY = -1;
    boolean touchscreenTouchDownDeltaNonZero = false;
    boolean touchscreenMultiTouchedDown = false;
    int touchscreenLastTouchCount = 0;
    int touchscreenWheelMoveCount = 0;
    final int wheelMoveCountIncrementThreshold = 110;

    /**
     * Instantiates a new {@link TrackpadModeView}.
     *
     * @param viewController the {@link ViewController}
     */
    public TrackpadModeView(ViewController viewController) {
        super(viewController, VIEW_IMAGE_CLASSPATH + "mode_trackpad.png");
        usbController = viewController.getModelA().getUSBController();
    }

    @Override
    public void processTouches(Touch[] touches) {
        if (!touchscreenMultiTouchedDown) {
            touchscreenMultiTouchedDown = touches.length >= 2;
        }

        if (touches.length > 0) {
            final int x = touches[0].getX();
            final int y = touches[0].getY();
            if (!(x > 1066 && x < 1686 && y > 71 && y < 390)) {
                viewController.setMode(KEYBOARD);
                return;
            }

            if (touchscreenTouchLastX == -1 || touchscreenTouchLastY == -1) {
                // Set last touch coordinates to current touch coordinates
                touchscreenTouchLastX = x;
                touchscreenTouchLastY = y;
            } else {
                // Calculate deltas
                int deltaX = x - touchscreenTouchLastX;
                int deltaY = y - touchscreenTouchLastY;
                int multipliedDeltaX = (int) Math.round((double) deltaX * 1.5);
                int multipliedDeltaY = (int) Math.round((double) deltaY * 1.5);

                // Clamp deltas
                deltaX = clamp(deltaX, Byte.MIN_VALUE, Byte.MAX_VALUE);
                deltaY = clamp(deltaY, Byte.MIN_VALUE, Byte.MAX_VALUE);
                multipliedDeltaX = clamp(multipliedDeltaX, Byte.MIN_VALUE, Byte.MAX_VALUE);
                multipliedDeltaY = clamp(multipliedDeltaY, Byte.MIN_VALUE, Byte.MAX_VALUE);

                // Write "trackpad" report
                final HIDReport report = new HIDReport();
                if (touchscreenMultiTouchedDown) {
                    if ((touchscreenWheelMoveCount += Math.abs(deltaY)) >= wheelMoveCountIncrementThreshold) {
                        touchscreenWheelMoveCount = 0;
                        report.setWheel((byte) -clamp(deltaY, -1, 1));
                    }
                } else {
                    report.setX((byte) multipliedDeltaX);
                    report.setY((byte) multipliedDeltaY);
                }
                try {
                    usbController.writeHIDReport(report);
                } catch (InterruptedException | IOException e) {
                    LOGGER.error("Could not write HID report!", e);
                    return;
                }

                // Set last touch coordinates to current touch coordinates
                if (multipliedDeltaX != 0) {
                    touchscreenTouchLastX = x;
                }
                if (multipliedDeltaY != 0) {
                    touchscreenTouchLastY = y;
                }

                // Trigger non-zero delta touch as needed
                if (!touchscreenTouchDownDeltaNonZero && deltaX != 0 && deltaY != 0) {
                    touchscreenTouchDownDeltaNonZero = true;
                }

                LOGGER.debug("Report sent: buttons={} x={} y={} wheel={}\n",
                        report.getButtons(), report.getX(), report.getY(), report.getWheel());
            }
        } else {
            final HIDReport report = new HIDReport();

            // Handle non-zero delta touch down/up (aka button clicks)
            if (touchscreenTouchDownDeltaNonZero) {
                touchscreenTouchDownDeltaNonZero = false;
            } else if (touchscreenLastTouchCount != 0) {
                if (touchscreenMultiTouchedDown) {
                    report.setButtons((byte) (0x01 << 1));
                    LOGGER.info("Touchpad right-click sent.");
                } else {
                    report.setButtons((byte) 0x01);
                    LOGGER.info("Touchpad left-click sent.");
                }
                try {
                    usbController.writeHIDReport(report);
                } catch (InterruptedException | IOException e) {
                    LOGGER.error("Could not write HID report!", e);
                    return;
                }
                report.setButtons((byte) 0);
            }

            // Reset variables
            touchscreenTouchLastX = -1;
            touchscreenTouchLastY = -1;
            touchscreenMultiTouchedDown = false;

            try {
                usbController.writeHIDReport(report);
            } catch (InterruptedException | IOException e) {
                LOGGER.error("Could not write HID report!", e);
                return;
            }
        }

        touchscreenLastTouchCount = touches.length;
    }
}
