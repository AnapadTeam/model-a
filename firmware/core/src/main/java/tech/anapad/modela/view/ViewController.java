package tech.anapad.modela.view;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.touchscreen.driver.Resolution;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.util.location.Location;
import tech.anapad.modela.view.debug.forcehaptics.ForceHapticsView;
import tech.anapad.modela.view.debug.touches.TouchesView;

import java.util.List;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;
import static javafx.application.Platform.runLater;
import static javafx.scene.Cursor.NONE;
import static javafx.scene.paint.Color.BLACK;
import static tech.anapad.modela.util.location.Location.loc;

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
     * The static absolute pixel density of the active area in <code>pixels per millimeter</code>.
     */
    public static final double VIEW_PIXEL_DENSITY = 6.2;

    /**
     * Gets a {@link Location} relative to the active area in pixels given an X and Y coordinate in millimeters.
     *
     * @param x the X
     * @param y the Y
     *
     * @return the {@link Location}
     */
    public static Location mmLoc(double x, double y) {
        return loc(x * VIEW_PIXEL_DENSITY, y * VIEW_PIXEL_DENSITY);
    }

    private final ModelA modelA;
    private Stage stage;
    private Scene scene;
    private Group nodeGroup;
    private double touchXMultiplier;
    private double touchYMultiplier;
    private TouchesView touchesView;
    private ForceHapticsView forceHapticsView;

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

        // Get resolution and add touch listener
        final Resolution resolution = modelA.getTouchscreenController().getTouchscreenDriver().getLastReadResolution();
        touchXMultiplier = VIEW_WIDTH / (double) resolution.getX();
        touchYMultiplier = VIEW_HEIGHT / (double) resolution.getY();
        modelA.getTouchscreenController().getTouchListeners().add(touches -> runLater(() -> processTouches(touches)));

        // Create group for all nodes
        nodeGroup = new Group();
        nodeGroup.setRotate(180d); // Flip view upside down

        if (!modelA.getArguments().runProduction()) {
            // TODO

            //touchesView = new TouchesView(this);
            //touchesView.start();

            //final LoadSurfacesView loadSurfacesView = new LoadSurfacesView(this);
            //loadSurfacesView.start();
            //nodeGroup.getChildren().add(loadSurfacesView.getNodeGroup());

            forceHapticsView = new ForceHapticsView(this);
            forceHapticsView.start();
            nodeGroup.getChildren().add(forceHapticsView.getNodeGroup());
        }

        // Set up scene/stage
        final Scene scene = new Scene(nodeGroup, VIEW_WIDTH, VIEW_HEIGHT);
        scene.setFill(BLACK);
        scene.setCursor(NONE);
        stage.setScene(scene);
        stage.show();

        LOGGER.info("Started ViewController.");
    }

    /**
     * Stops {@link ViewController}.
     */
    public void stop() {}

    /**
     * Called to process touchscreen touches.
     *
     * @param touches the {@link Touch}es {@link List}
     */
    private void processTouches(List<Touch> touches) {
        // Apply touch multiplier
        final List<Touch> multipliedTouches = touches.stream()
                .map(touch -> new Touch.Builder()
                        .id(touch.getID())
                        .x((int) round(((double) touch.getX()) * touchXMultiplier))
                        .y((int) round(((double) touch.getY()) * touchYMultiplier))
                        .size(touch.getSize())
                        .build())
                .collect(toList());

        // Pass touches to views
        if (touchesView != null) {
            touchesView.processTouches(multipliedTouches);
        }
        if (forceHapticsView != null) {
            forceHapticsView.processTouches(multipliedTouches);
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

    public Group getNodeGroup() {
        return nodeGroup;
    }

    public double getTouchXMultiplier() {
        return touchXMultiplier;
    }

    public double getTouchYMultiplier() {
        return touchYMultiplier;
    }
}
