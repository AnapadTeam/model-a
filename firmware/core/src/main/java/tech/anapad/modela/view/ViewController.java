package tech.anapad.modela.view;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.view.mode.AbstractModeView;
import tech.anapad.modela.view.mode.KeyboardModeView;
import tech.anapad.modela.view.mode.TrackpadModeView;
import tech.anapad.modela.view.model.AnapadMode;

import static javafx.scene.Cursor.NONE;
import static javafx.scene.paint.Color.BLACK;
import static javafx.util.Duration.millis;
import static tech.anapad.modela.view.model.AnapadMode.KEYBOARD;

/**
 * {@link ViewController} is a controller for the view.
 */
public class ViewController {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewController.class);

    private final ModelA modelA;
    private final KeyboardModeView keyboardModeView;
    private final TrackpadModeView trackpadModeView;
    private Stage stage;
    private Scene scene;
    private Group group;
    private AnapadMode currentAnapadMode;

    /**
     * Instantiates a new {@link ViewController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public ViewController(ModelA modelA) {
        this.modelA = modelA;
        keyboardModeView = new KeyboardModeView(this);
        trackpadModeView = new TrackpadModeView(this);
    }

    /**
     * Starts {@link ViewController}.
     */
    public void start(Stage stage) {
        LOGGER.info("Starting ViewController...");

        // Create group for nodes
        group = new Group();
        group.setRotate(180d); // Flip view upside down
        group.getChildren().addAll(keyboardModeView.getModeImageView(), trackpadModeView.getModeImageView());

        // Set up scene/stage
        final Scene scene = new Scene(group, VIEW_WIDTH, VIEW_HEIGHT);
        scene.setFill(BLACK);
        scene.setCursor(NONE);
        stage.setScene(scene);
        stage.show();

        setMode(KEYBOARD);

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
        if (currentAnapadMode == anapadMode) {
            return;
        }

        // Determine which modes are fading in and out
        final AbstractModeView fadeInModeView;
        final AbstractModeView fadeOutModeView;
        switch (anapadMode) {
            case KEYBOARD -> {
                fadeInModeView = keyboardModeView;
                if (currentAnapadMode == null) {
                    fadeOutModeView = trackpadModeView;
                } else {
                    fadeOutModeView = null;
                }
            }
            case TRACKPAD -> {
                fadeInModeView = trackpadModeView;
                if (currentAnapadMode == null) {
                    fadeOutModeView = keyboardModeView;
                } else {
                    fadeOutModeView = null;
                }
            }
            default -> throw new UnsupportedOperationException();
        }

        // Play opacity/fade transitions
        final Duration fadeDuration = currentAnapadMode == null ? millis(1000) : millis(250);
        final FadeTransition fadeIn = new FadeTransition(fadeDuration, fadeInModeView.getModeImageView());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        fadeInModeView.getModeImageView().setVisible(true);
        if (fadeOutModeView != null) {
            final FadeTransition fadeOut = new FadeTransition(fadeDuration, fadeOutModeView.getModeImageView());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.play();
            fadeOut.setOnFinished(event -> fadeOutModeView.getModeImageView().setVisible(false));
        }

        currentAnapadMode = anapadMode;
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
}
