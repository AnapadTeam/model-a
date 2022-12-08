package tech.anapad.modela;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.hapticsboard.HapticsBoardController;
import tech.anapad.modela.loadsurface.LoadSurfaceController;
import tech.anapad.modela.touchscreen.TouchscreenController;
import tech.anapad.modela.usb.USBController;
import tech.anapad.modela.view.ViewController;

import static javafx.application.Platform.exit;

/**
 * {@link ModelA} is the main entrypoint for the Model A core firmware.
 */
@SuppressWarnings("ClassEscapesDefinedScope")
public class ModelA extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelA.class);
    protected static Arguments arguments;

    private boolean stopCalled;
    private ViewController viewController;
    private USBController usbController;
    private TouchscreenController touchscreenController;
    private HapticsBoardController hapticsBoardController;
    private LoadSurfaceController loadSurfaceController;

    /**
     * Starts {@link ModelA}.
     */
    @Override
    public void start(Stage stage) {
        stopCalled = false;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));

        LOGGER.info("Starting...");
        try {
            //usbController = new USBController(this);
            touchscreenController = new TouchscreenController(this);
            hapticsBoardController = new HapticsBoardController(this);
            loadSurfaceController = new LoadSurfaceController(this);
            viewController = new ViewController(this);
            //usbController.start();
            touchscreenController.start();
            hapticsBoardController.start();
            loadSurfaceController.start();
            viewController.start(stage);
        } catch (Exception exception) {
            LOGGER.error("A fatal error occurred while starting!", exception);
            stop();
            return;
        }
        LOGGER.info("Started.");
    }

    /**
     * Stops {@link ModelA}.
     */
    @Override
    public void stop() {
        if (stopCalled) {
            return;
        }
        stopCalled = true;

        LOGGER.info("Stopping...");
        // Stop the instances in reverse order
        if (loadSurfaceController != null) {
            try {
                loadSurfaceController.stop();
            } catch (Exception exception) {
                LOGGER.error("Error stopping LoadSurfaceController!", exception);
            }
        }
        if (hapticsBoardController != null) {
            try {
                hapticsBoardController.stop();
            } catch (Exception exception) {
                LOGGER.error("Error stopping HapticsBoardController!", exception);
            }
        }
        if (touchscreenController != null) {
            try {
                touchscreenController.stop();
            } catch (Exception exception) {
                LOGGER.error("Error stopping TouchscreenController!", exception);
            }
        }
        if (usbController != null) {
            try {
                usbController.stop();
            } catch (Exception exception) {
                LOGGER.error("Error stopping USBController!", exception);
            }
        }
        if (viewController != null) {
            try {
                viewController.stop();
            } catch (Exception exception) {
                LOGGER.error("Error stopping ViewController!", exception);
            }
        }
        LOGGER.info("Stopped.");
        exit();
    }

    public Arguments getArguments() {
        return arguments;
    }

    public ViewController getViewController() {
        return viewController;
    }

    public USBController getUSBController() {
        return usbController;
    }

    public TouchscreenController getTouchscreenController() {
        return touchscreenController;
    }

    public HapticsBoardController getHapticsBoardController() {
        return hapticsBoardController;
    }

    public LoadSurfaceController getLoadSurfaceController() {
        return loadSurfaceController;
    }
}
