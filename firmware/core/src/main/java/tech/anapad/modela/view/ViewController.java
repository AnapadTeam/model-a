package tech.anapad.modela.view;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.ModelA;
import tech.anapad.modela.loadsurface.sample.SampleResult;
import tech.anapad.modela.touchscreen.driver.Resolution;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.util.location.Location;
import tech.anapad.modela.view.component.button.Button;
import tech.anapad.modela.view.views.AbstractView;
import tech.anapad.modela.view.views.debug.forcehaptics.ForceHapticsView;
import tech.anapad.modela.view.views.debug.loadsurface.LoadSurfacesView;
import tech.anapad.modela.view.views.debug.touch.TouchesView;
import tech.anapad.modela.view.views.menu.MenuButton;
import tech.anapad.modela.view.views.menu.MenuView;
import tech.anapad.modela.view.views.splash.SplashView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.Byte.MAX_VALUE;
import static java.lang.Math.round;
import static java.util.stream.Collectors.toList;
import static javafx.application.Platform.runLater;
import static javafx.scene.Cursor.NONE;
import static javafx.scene.paint.Color.BLACK;
import static javafx.util.Duration.millis;
import static tech.anapad.modela.util.location.Location.loc;
import static tech.anapad.modela.view.util.palette.Palette.BACKGROUND_COLOR_PROPERTY;

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

    /**
     * The multiplier for a {@link Touch#getSize()}.
     */
    public static final double TOUCH_SIZE_MULTIPLIER = 1.25;

    private final ModelA modelA;
    private final Consumer<List<Touch>> touchesListener;
    private Stage stage;
    private Scene scene;
    private Group nodeGroup;
    private double touchXMultiplier;
    private double touchYMultiplier;

    private AbstractView activeView;
    private boolean viewTransitioning;
    private SplashView splashView;
    private MenuButton menuButton;
    private MenuView menuView;
    private TouchesView touchesView;
    private LoadSurfacesView loadSurfacesView;
    private ForceHapticsView forceHapticsView;

    /**
     * Instantiates a new {@link ViewController}.
     *
     * @param modelA the {@link ModelA} instance
     */
    public ViewController(ModelA modelA) {
        this.modelA = modelA;
        touchesListener = touches -> runLater(() -> processTouches(touches));
        viewTransitioning = false;
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
        modelA.getTouchscreenController().getTouchListeners().add(touchesListener);

        // Create group for all nodes
        nodeGroup = new Group();
        nodeGroup.setRotate(180d); // Flip view upside down

        // Background rectangle
        final Rectangle backgroundRectangle = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT);
        backgroundRectangle.fillProperty().bind(BACKGROUND_COLOR_PROPERTY);
        nodeGroup.getChildren().add(backgroundRectangle);

        // Clip rectangle
        final Rectangle clipRectangle = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT);
        final double clipRectangleRadius = 2.0 * VIEW_PIXEL_DENSITY;
        clipRectangle.setArcWidth(clipRectangleRadius);
        clipRectangle.setArcHeight(clipRectangleRadius);
        nodeGroup.setClip(clipRectangle);

        // Create menu view
        menuView = new MenuView(this);
        menuView.start();
        nodeGroup.getChildren().add(menuView.getNodeGroup());

        // Create menu button
        menuButton = new MenuButton(this);
        menuButton.translateXProperty().bind(menuButton.widthProperty().divide(-2).add(VIEW_WIDTH / 2.0));
        menuButton.translateYProperty().set(0.5 * VIEW_PIXEL_DENSITY);
        menuButton.setOpacity(0.0);
        menuButton.setVisible(false);
        nodeGroup.getChildren().add(menuButton);

        // Create views
        touchesView = new TouchesView(this);
        loadSurfacesView = new LoadSurfacesView(this);
        forceHapticsView = new ForceHapticsView(this);

        // Set up scene/stage
        final Scene scene = new Scene(nodeGroup, VIEW_WIDTH, VIEW_HEIGHT);
        scene.setFill(BLACK);
        scene.setCursor(NONE);
        stage.setScene(scene);
        stage.show();

        // Start splash
        splashView = new SplashView(this::handleSplashViewDone);
        nodeGroup.getChildren().add(splashView.getNodeGroup());
        splashView.start();

        LOGGER.info("Started ViewController.");
    }

    private void handleSplashViewDone(ActionEvent event) {
        nodeGroup.getChildren().remove(splashView.getNodeGroup());
        menuButton.setVisible(true);
        setActiveView(touchesView);
    }

    /**
     * Stops {@link ViewController}.
     */
    public void stop() {
        LOGGER.info("Stopping ViewController...");

        if (activeView != null) {
            runLater(() -> activeView.stop());
        }

        LOGGER.info("Stopped ViewController.");
    }

    /**
     * Sets the {@link #activeView} to the given {@link AbstractView}.
     *
     * @param newView the {@link AbstractView}
     */
    public synchronized void setActiveView(AbstractView newView) {
        if (newView == activeView) {
            return;
        }
        if (viewTransitioning) {
            return;
        }
        viewTransitioning = true;
        if (activeView != null) {
            final FadeTransition fadeOutTransition = new FadeTransition(millis(250), activeView.getNodeGroup());
            fadeOutTransition.setFromValue(activeView.getNodeGroup().getOpacity());
            fadeOutTransition.setToValue(0.0);
            fadeOutTransition.setOnFinished(event -> handleFadeInActiveView(newView));
            fadeOutTransition.play();
        } else {
            handleFadeInActiveView(newView);
        }
    }

    private void handleFadeInActiveView(AbstractView newView) {
        if (activeView != null) {
            activeView.stop();
            nodeGroup.getChildren().remove(activeView.getNodeGroup());
        }
        nodeGroup.getChildren().add(newView.getNodeGroup());

        if (menuButton.getOpacity() == 0.0) {
            final FadeTransition fadeInTransition = new FadeTransition(millis(250), menuButton);
            fadeInTransition.setFromValue(0.0);
            fadeInTransition.setToValue(1.0);
            fadeInTransition.play();
        }
        menuView.getNodeGroup().toFront();
        menuButton.toFront();

        final FadeTransition fadeInTransition = new FadeTransition(millis(250), newView.getNodeGroup());
        fadeInTransition.setFromValue(0.0);
        fadeInTransition.setToValue(1.0);
        fadeInTransition.play();
        newView.start();
        activeView = newView;
        viewTransitioning = false;
    }

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
                        .size((int) round(((double) touch.getSize()) * TOUCH_SIZE_MULTIPLIER))
                        .build())
                .collect(toList());

        // Get active buttons
        final List<Button> activeButtons = new ArrayList<>();
        if (menuButton != null) {
            activeButtons.add(menuButton);
        }
        if (menuView.isShowing()) {
            for (Node node : menuView.getNodeGroup().getChildren()) {
                if (node instanceof Button) {
                    activeButtons.add((Button) node);
                }
            }
        }
        if (activeView != null) {
            for (Node node : activeView.getNodeGroup().getChildren()) {
                if (node instanceof Button) {
                    activeButtons.add((Button) node);
                }
            }
        }

        // Process touches for buttons
        final Set<Touch> buttonTouches = new HashSet<>();
        if (multipliedTouches.size() == 0) {
            for (Button button : activeButtons) {
                if (button.isForcePressedDown()) {
                    button.onForcePressUp();
                }
                if (button.isPressedDown()) {
                    button.onPressUp();
                }
                if (button.isTouchedDown()) {
                    button.onTouchUp();
                }
            }
        } else {
            final SampleResult sampleResult;
            try {
                sampleResult = modelA.getLoadSurfaceController().getPercentOffsetSampleFuture().get();
            } catch (Exception exception) {
                LOGGER.error("Error reading load surface future!", exception);
                return;
            }

            final Map<Button, Integer> numberOfTouchesNotOnTouchedButtons = new HashMap<>();
            for (Touch touch : multipliedTouches) {
                final Location touchLocation = loc(touch.getX(), touch.getY());
                final double weightedPercentOffset = sampleResult.weightedPercentOffset(touchLocation);

                for (Button button : activeButtons) {
                    if (!button.isVisible()) {
                        continue;
                    }

                    if (button.containsTouch(touch)) {
                        buttonTouches.add(touch);

                        if (!button.isTouchedDown()) {
                            button.onTouchDown();
                        }
                        if (!button.isPressedDown() &&
                                weightedPercentOffset > button.getPressDownThreshold()) {
                            lraImpulse(touchLocation, 30);
                            button.onPressDown();
                        } else if (button.isPressedDown() &&
                                weightedPercentOffset < button.getPressUpThreshold()) {
                            lraImpulse(touchLocation, 15);
                            button.onPressUp();
                        }
                        if (!button.isForcePressedDown() &&
                                weightedPercentOffset > button.getForcePressDownThreshold()) {
                            button.onForcePressDown();
                            lraImpulse(touchLocation, 50);
                        } else if (button.isForcePressedDown() &&
                                weightedPercentOffset < button.getForcePressUpThreshold()) {
                            button.onForcePressUp();
                        }
                    } else {
                        if (button.isTouchedDown()) {
                            numberOfTouchesNotOnTouchedButtons.merge(button, 1, Integer::sum);
                        }
                    }
                }
            }
            for (Map.Entry<Button, Integer> entry : numberOfTouchesNotOnTouchedButtons.entrySet()) {
                final Button notOnTouchedButton = entry.getKey();
                final Integer numberOfTouchesNotOn = entry.getValue();
                if (numberOfTouchesNotOn == multipliedTouches.size()) {
                    notOnTouchedButton.onTouchUp();
                }
            }
        }

        // Pass non-button, raw touches to required views/components
        if (multipliedTouches.size() > 0 && multipliedTouches.size() == buttonTouches.size()) {
            return;
        }
        multipliedTouches.removeAll(buttonTouches);
        if (activeView == touchesView) {
            touchesView.processRawTouches(multipliedTouches);
        } else if (activeView == forceHapticsView) {
            forceHapticsView.processRawTouches(multipliedTouches);
        }
    }

    private void lraImpulse(Location location, long millis) {
        modelA.getHapticsBoardController().scheduleLRAImpulse(location, 130.0, MAX_VALUE, 0, millis);
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

    public AbstractView getActiveView() {
        return activeView;
    }

    public boolean isViewTransitioning() {
        return viewTransitioning;
    }

    public SplashView getSplashView() {
        return splashView;
    }

    public MenuButton getMenuButton() {
        return menuButton;
    }

    public MenuView getMenuView() {
        return menuView;
    }

    public TouchesView getTouchesView() {
        return touchesView;
    }

    public LoadSurfacesView getLoadSurfacesView() {
        return loadSurfacesView;
    }

    public ForceHapticsView getForceHapticsView() {
        return forceHapticsView;
    }
}
