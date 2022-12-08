package tech.anapad.modela.view.debug.forcehaptics;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.anapad.modela.hapticsboard.lra.LRA;
import tech.anapad.modela.loadsurface.sample.SampleResult;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.util.location.Location;
import tech.anapad.modela.view.AbstractView;
import tech.anapad.modela.view.ViewController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.paint.Color.rgb;
import static javafx.scene.shape.StrokeType.OUTSIDE;
import static javafx.scene.text.TextAlignment.CENTER;
import static tech.anapad.modela.hapticsboard.lra.LRA.DIAMETER;
import static tech.anapad.modela.util.location.Location.loc;
import static tech.anapad.modela.util.math.MathUtil.clamp;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_PIXEL_DENSITY;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;

/**
 * {@link ForceHapticsView} is an {@link AbstractView} for viewing force sensing and haptics as a debugging measure.
 */
public class ForceHapticsView extends AbstractView {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForceHapticsView.class);
    private static final Color LRA_STATIONARY_COLOR = rgb(237, 11, 14);
    private static final Color LRA_ACTUATED_COLOR = rgb(250, 120, 10);
    private static final double LRA_ACTUATION_RADIUS = 130.0;
    private static final double MAX_LOAD_SURFACE_PERCENT_OFFSET = 0.08;

    private final ViewController viewController;

    private Map<LRA, Circle> circlesOfLRAs;
    private Circle touchCircle;
    private Circle forceCircle;

    /**
     * Instantiates a new {@link ForceHapticsView}.
     *
     * @param viewController the {@link ViewController}
     */
    public ForceHapticsView(ViewController viewController) {
        this.viewController = viewController;
        nodeGroup.setClip(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT));
    }

    @Override
    public void start() {
        nodeGroup.getChildren().add(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, BLACK));

        circlesOfLRAs = new HashMap<>();
        for (LRA lra : viewController.getModelA().getHapticsBoardController().getLRAList()) {
            final double x = lra.getLocation().getX();
            final double y = lra.getLocation().getY();

            final Circle circle = new Circle(DIAMETER * VIEW_PIXEL_DENSITY / 2.0, LRA_STATIONARY_COLOR);
            circle.setTranslateX(x);
            circle.setTranslateY(y);
            nodeGroup.getChildren().add(circle);
            circlesOfLRAs.put(lra, circle);

            final Label label = new Label();
            label.setText(lra.getReference().getColumn().toString() + lra.getReference().getRow().toString());
            label.setTextAlignment(CENTER);
            label.layoutXProperty().bind(label.widthProperty().divide(-2));
            label.layoutYProperty().bind(label.heightProperty().divide(-2));
            label.setTranslateX(x);
            label.setTranslateY(y);
            label.setTextFill(WHITE);
            nodeGroup.getChildren().add(label);
        }

        touchCircle = new Circle(LRA_ACTUATION_RADIUS, TRANSPARENT);
        touchCircle.setStroke(gray(0.5, 0.5));
        touchCircle.setStrokeWidth(5.0);
        touchCircle.setStrokeType(OUTSIDE);
        nodeGroup.getChildren().add(touchCircle);

        forceCircle = new Circle(0, gray(0.5, 0.5));
        nodeGroup.getChildren().add(forceCircle);
    }

    @Override
    public void stop() {
        nodeGroup.getChildren().clear();

        touchCircle = null;

        circlesOfLRAs.clear();
        circlesOfLRAs = null;
    }

    /**
     * Called to process touchscreen touches.
     *
     * @param touches the {@link Touch}es
     */
    public void processTouches(List<Touch> touches) {
        final SampleResult sampleResult;
        try {
            sampleResult = viewController.getModelA().getLoadSurfaceController().getPercentOffsetSampleFuture().get();
        } catch (Exception exception) {
            LOGGER.error("Error reading load surface future!", exception);
            return;
        }

        if (touches.size() >= 1) {
            final Location touchLocation = loc(touches.get(0).getX(), touches.get(0).getY());
            if (!touchCircle.isVisible()) {
                touchCircle.setVisible(true);
                forceCircle.setVisible(true);
            }
            touchCircle.setCenterX(touchLocation.getX());
            touchCircle.setCenterY(touchLocation.getY());
            forceCircle.setCenterX(touchLocation.getX());
            forceCircle.setCenterY(touchLocation.getY());

            final double weightPercentOffsetRatio = clamp(
                    sampleResult.weightedPercentOffset(touchLocation), 0, MAX_LOAD_SURFACE_PERCENT_OFFSET) /
                    MAX_LOAD_SURFACE_PERCENT_OFFSET;
            forceCircle.setRadius(weightPercentOffsetRatio * LRA_ACTUATION_RADIUS);
            try {
                final List<LRA> actuatedLRAs = viewController.getModelA().getHapticsBoardController().setLRAsWithin(
                        touchLocation, LRA_ACTUATION_RADIUS, (byte) (weightPercentOffsetRatio * Byte.MAX_VALUE));
                for (Map.Entry<LRA, Circle> circleOfLRA : circlesOfLRAs.entrySet()) {
                    final LRA lra = circleOfLRA.getKey();
                    final Circle circle = circleOfLRA.getValue();
                    if (actuatedLRAs.contains(lra)) {
                        circle.setFill(LRA_ACTUATED_COLOR);
                    } else {
                        circle.setFill(LRA_STATIONARY_COLOR);
                    }
                }
            } catch (Exception exception) {
                LOGGER.error("Error set haptics board LRAs!", exception);
            }
        } else {
            if (touchCircle.isVisible()) {
                touchCircle.setVisible(false);
                forceCircle.setVisible(false);
            }
            for (Circle lraCircle : circlesOfLRAs.values()) {
                lraCircle.setFill(LRA_STATIONARY_COLOR);
            }
            try {
                viewController.getModelA().getHapticsBoardController().stopAllLRAs();
            } catch (Exception exception) {
                LOGGER.error("Error stopping haptics board LRAs!", exception);
            }
        }
    }
}
