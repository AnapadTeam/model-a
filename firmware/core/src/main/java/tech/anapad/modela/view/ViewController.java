package tech.anapad.modela.view;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.touchscreen.TouchscreenController;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.view.debug.TouchesView;

import java.util.List;

import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;
import static javafx.scene.Cursor.NONE;

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

    private final ModelA modelA;
    private TouchscreenController touchscreenController;
    private Stage stage;
    private Scene scene;
    private Group nodeGroup;
    private TouchesView touchesView;
    private double touchXMultiplier;
    private double touchYMultiplier;

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
        touchscreenController.getResolutionListeners().add((resolution) -> {
            touchXMultiplier = VIEW_WIDTH / (double) resolution.getX();
            touchYMultiplier = VIEW_HEIGHT / (double) resolution.getY();
        });
        touchscreenController.getTouchListeners().add(touches -> Platform.runLater(() -> processTouches(touches)));

        // Create group for nodes
        nodeGroup = new Group();
        nodeGroup.setRotate(180d); // Flip view upside down

        // Create TouchesView as needed
        if (!modelA.getArguments().runProduction()) {
            touchesView = new TouchesView(this);
            nodeGroup.getChildren().add(touchesView.getCanvas());
        }

        // Set up scene/stage
        final Scene scene = new Scene(nodeGroup, VIEW_WIDTH, VIEW_HEIGHT);
        scene.setFill(Color.rgb(235, 236, 240));
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
                        .x((int) round((double) touch.getX() * touchXMultiplier))
                        .y((int) round((double) touch.getY() * touchYMultiplier))
                        .size(touch.getSize())
                        .build())
                .collect(toList());

        // Pass touches to views
        if (touchesView != null) {
            touchesView.processTouches(multipliedTouches);
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
