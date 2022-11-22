package tech.anapad.modela.view;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.touchscreen.TouchscreenController;
import tech.anapad.modela.touchscreen.model.Touch;
import tech.anapad.modela.view.debug.TouchesView;
import tech.anapad.modela.view.mode.AbstractModeView;
import tech.anapad.modela.view.mode.KeyboardModeView;
import tech.anapad.modela.view.mode.TrackpadModeView;
import tech.anapad.modela.view.model.AnapadMode;

import static java.lang.Math.round;
import static javafx.scene.Cursor.NONE;
import static javafx.util.Duration.millis;
import static tech.anapad.modela.view.model.AnapadMode.TRACKPAD;

/**
 * {@link ViewController} is a controller for the view.
 */
public class ViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);

    /**
     * The static absolute width of the view.
     */
    public static final int VIEW_WIDTH = 1920;

    /**
     * The static absolute height of the view.
     */
    public static final int VIEW_HEIGHT = 515;

    /**
     * The classpath of the <code>view/image/</code> folder.
     */
    public static final String VIEW_IMAGE_CLASSPATH = "view/image/";

    private final ModelA modelA;
    private TouchscreenController touchscreenController;
    private KeyboardModeView keyboardModeView;
    private TrackpadModeView trackpadModeView;
    private TouchesView touchesView;
    private Stage stage;
    private Scene scene;
    private Group group;
    private AnapadMode currentAnapadMode;
    private double touchXMultiplier;
    private double touchYMultiplier;
    private boolean modeTransitioning;

    /**
     * Instantiates a new {@link ViewController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public ViewController(ModelA modelA) {
        this.modelA = modelA;
    }

    /**
     * Starts {@link ViewController}.
     */
    public void start(Stage stage) {
        LOGGER.info("Starting ViewController...");

        // Add touchscreen initialized listener for resolution
        touchscreenController = modelA.getTouchscreenController();
        touchscreenController.getInitializedListeners().add(() -> {
            touchXMultiplier = VIEW_WIDTH / (double) touchscreenController.getXResolution();
            touchYMultiplier = VIEW_HEIGHT / (double) touchscreenController.getYResolution();
        });
        touchscreenController.getTouchListeners().add(touches -> Platform.runLater(() -> processTouches(touches)));

        // Create group for nodes
        group = new Group();
        group.setRotate(180d); // Flip view upside down

        // Create mode views
        keyboardModeView = new KeyboardModeView(this);
        trackpadModeView = new TrackpadModeView(this);
        group.getChildren().addAll(keyboardModeView.getModeImageView(), trackpadModeView.getModeImageView());

        // Create TouchesView as needed
        if (!modelA.getArguments().runProduction()) {
            touchesView = new TouchesView(this);
            group.getChildren().add(touchesView.getCanvas());
        }

        // Set up scene/stage
        final Scene scene = new Scene(group, VIEW_WIDTH, VIEW_HEIGHT);
        scene.setFill(Color.rgb(235, 236, 240));
        scene.setCursor(NONE);
        stage.setScene(scene);
        stage.show();

        setMode(TRACKPAD);

        LOGGER.info("Started ViewController.");
    }

    /**
     * Stops {@link ViewController}.
     */
    public void stop() {}

    /**
     * Sets this {@link ViewController} to the given {@link AnapadMode}.
     *
     * @param anapadMode the {@link AnapadMode}
     */
    public void setMode(AnapadMode anapadMode) {
        if (currentAnapadMode == anapadMode || modeTransitioning) {
            return;
        }

        // Determine which modes are fading in and out
        final AbstractModeView fadeInModeView;
        final AbstractModeView fadeOutModeView;
        switch (anapadMode) {
            case KEYBOARD -> {
                fadeInModeView = keyboardModeView;
                if (currentAnapadMode == null) {
                    fadeOutModeView = null;
                } else {
                    fadeOutModeView = trackpadModeView;
                }
            }
            case TRACKPAD -> {
                fadeInModeView = trackpadModeView;
                if (currentAnapadMode == null) {
                    fadeOutModeView = null;
                } else {
                    fadeOutModeView = keyboardModeView;
                }
            }
            default -> throw new UnsupportedOperationException();
        }

        // Play opacity/fade transitions
        final Duration fadeDuration = currentAnapadMode == null ? millis(1500) : millis(200);
        final FadeTransition fadeIn = new FadeTransition(fadeDuration, fadeInModeView.getModeImageView());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        fadeInModeView.getModeImageView().setVisible(true);
        fadeIn.setOnFinished(event -> modeTransitioning = false);
        modeTransitioning = true;
        if (fadeOutModeView != null) {
            final FadeTransition fadeOut = new FadeTransition(fadeDuration, fadeOutModeView.getModeImageView());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.play();
            fadeOut.setOnFinished(event -> fadeOutModeView.getModeImageView().setVisible(false));
        }

        currentAnapadMode = anapadMode;
    }

    /**
     * Called to process touchscreen touches.
     *
     * @param touches the {@link Touch}es
     */
    private void processTouches(Touch[] touches) {
        // Apply touch multiplier
        for (Touch touch : touches) {
            touch.setX((int) round((double) touch.getX() * touchXMultiplier));
            touch.setY((int) round((double) touch.getY() * touchYMultiplier));
        }

        // Pass touches to views
        switch (currentAnapadMode) {
            case KEYBOARD -> keyboardModeView.processTouches(touches);
            case TRACKPAD -> trackpadModeView.processTouches(touches);
            default -> throw new UnsupportedOperationException();
        }
        if (touchesView != null) {
            touchesView.processTouches(touches);
        }
    }

    public ModelA getModelA() {
        return modelA;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public Group getGroup() {
        return group;
    }

    public KeyboardModeView getKeyboardModeView() {
        return keyboardModeView;
    }

    public TrackpadModeView getTrackpadModeView() {
        return trackpadModeView;
    }

    public AnapadMode getCurrentAnapadMode() {
        return currentAnapadMode;
    }

    public double getTouchXMultiplier() {
        return touchXMultiplier;
    }

    public double getTouchYMultiplier() {
        return touchYMultiplier;
    }
}
