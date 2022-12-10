package tech.anapad.modela.view.views.anapad;

import javafx.scene.shape.Rectangle;
import tech.anapad.modela.loadsurface.sample.SampleResult;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.usb.USBController;
import tech.anapad.modela.util.location.Location;
import tech.anapad.modela.view.ViewController;
import tech.anapad.modela.view.views.AbstractView;

import java.util.List;
import java.util.Set;

import static java.lang.Byte.MAX_VALUE;
import static tech.anapad.modela.usb.mapping.MouseButton.LEFT;
import static tech.anapad.modela.usb.mapping.MouseButton.RIGHT;
import static tech.anapad.modela.util.math.MathUtil.clamp;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_PIXEL_DENSITY;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;
import static tech.anapad.modela.view.util.palette.Palette.BACKGROUND_COLOR_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.FOREGROUND_COLOR_PROPERTY;

/**
 * {@link TrackpadView} is an {@link AbstractView} of an anapad.
 */
public class TrackpadView extends AbstractView {

    private final ViewController viewController;
    private final USBController usbController;
    private final Rectangle trackpad;

    int touchscreenTouchLastX = -1;
    int touchscreenTouchLastY = -1;
    boolean touchscreenTouchDownDeltaNonZero = false;
    boolean touchscreenMultiTouchedDown = false;
    int touchscreenLastTouchCount = 0;
    int touchscreenWheelMoveCount = 0;
    final int wheelMoveCountIncrementThreshold = 50;
    boolean canPressAgain = true;

    /**
     * Instantiates a new {@link TrackpadView}.
     *
     * @param viewController the {@link ViewController}
     */
    public TrackpadView(ViewController viewController) {
        this.viewController = viewController;
        this.usbController = viewController.getModelA().getUSBController();

        final Rectangle background = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT);
        background.fillProperty().bind(BACKGROUND_COLOR_PROPERTY);
        nodeGroup.getChildren().add(background);

        trackpad = new Rectangle(108.758 * VIEW_PIXEL_DENSITY, 66.864 * VIEW_PIXEL_DENSITY);
        trackpad.fillProperty().bind(FOREGROUND_COLOR_PROPERTY);
        trackpad.setArcWidth(5.0 * VIEW_PIXEL_DENSITY);
        trackpad.setArcHeight(5.0 * VIEW_PIXEL_DENSITY);
        trackpad.setTranslateX(177.078 * VIEW_PIXEL_DENSITY);
        trackpad.setTranslateY(5.67 * VIEW_PIXEL_DENSITY);
        nodeGroup.getChildren().add(trackpad);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    /**
     * Called to process touchscreen touches.
     *
     * @param touchess the {@link Touch}es
     */
    public void processRawTouches(List<Touch> touchess) {
        final Touch[] touches = touchess.toArray(new Touch[0]);

        if (!touchscreenMultiTouchedDown) {
            touchscreenMultiTouchedDown = touches.length >= 2;
        }

        if (touches.length > 0) {
            final int x = touches[0].getX();
            final int y = touches[0].getY();
            if (!(trackpad.getBoundsInParent().contains(x, y))) {
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
                if (touchscreenMultiTouchedDown) {
                    if ((touchscreenWheelMoveCount += Math.abs(deltaY)) >= wheelMoveCountIncrementThreshold) {
                        touchscreenWheelMoveCount = 0;
                        usbController.setNextMouseMovement((byte) 0, (byte) 0, (byte) -clamp(deltaY, -1, 1));
                    }
                } else {
                    usbController.setNextMouseMovement((byte) multipliedDeltaX, (byte) multipliedDeltaY,
                            (byte) 0);
                }
                try {
                    usbController.flush(100, 10);
                } catch (Exception e) {
                    e.printStackTrace();
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

                final SampleResult sampleResult;
                try {
                    sampleResult =
                            viewController.getModelA().getLoadSurfaceController().getPercentOffsetSampleFuture().get();
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return;
                }
                double value = sampleResult.weightedPercentOffset(Location.loc(x, y));
                if (value > 0.006 && canPressAgain) {
                    canPressAgain = false;
                    if (touches.length == 2) {
                        usbController.setActiveMouseButtons(Set.of(RIGHT), true);
                        try {
                            usbController.flush(100, 10);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        usbController.setActiveMouseButtons(Set.of(RIGHT), false);
                        try {
                            usbController.flush(100, 10);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        viewController.getModelA().getHapticsBoardController()
                                .scheduleLRAImpulse(Location.loc(x, y), 130.0, MAX_VALUE, 0, 30);
                    } else {
                        usbController.setActiveMouseButtons(Set.of(LEFT), true);
                        try {
                            usbController.flush(100, 10);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        usbController.setActiveMouseButtons(Set.of(LEFT), false);
                        try {
                            usbController.flush(100, 10);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        viewController.getModelA().getHapticsBoardController()
                                .scheduleLRAImpulse(Location.loc(x, y), 130.0, MAX_VALUE, 0, 30);
                    }
                } else if (value < 0.004) {
                    canPressAgain = true;
                }
            }
        } else {

            //final HIDReport report = new HIDReport();
            //
            //// Handle non-zero delta touch down/up (aka button clicks)
            if (touchscreenTouchDownDeltaNonZero) {
                touchscreenTouchDownDeltaNonZero = false;
            }
            //} else if (touchscreenLastTouchCount != 0) {
            //    if (touchscreenMultiTouchedDown) {
            //        report.setButtons((byte) (0x01 << 1));
            //        LOGGER.info("Touchpad right-click sent.");
            //    } else {
            //        report.setButtons((byte) 0x01);
            //        LOGGER.info("Touchpad left-click sent.");
            //    }
            //    try {
            //        usbController.writeHIDReport(report);
            //    } catch (InterruptedException | IOException e) {
            //        LOGGER.error("Could not write HID report!", e);
            //        return;
            //    }
            //    report.setButtons((byte) 0);
            //}
            //
            // Reset variables
            touchscreenTouchLastX = -1;
            touchscreenTouchLastY = -1;
            touchscreenMultiTouchedDown = false;

            //try {
            //    usbController.writeHIDReport(report);
            //} catch (InterruptedException | IOException e) {
            //    LOGGER.error("Could not write HID report!", e);
            //    return;
            //}
        }

        touchscreenLastTouchCount = touches.length;
    }

    public ViewController getViewController() {
        return viewController;
    }
}
