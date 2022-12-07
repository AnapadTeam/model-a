package tech.anapad.modela.view.debug.touches;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.view.AbstractView;
import tech.anapad.modela.view.ViewController;

import java.util.List;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.gray;
import static javafx.scene.paint.Color.rgb;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;

/**
 * {@link TouchesView} is an {@link AbstractView} used for rendering touches on the screen as a debugging measure.
 */
public class TouchesView extends AbstractView {

    private static final Color TRANSLUCENT_WHITE = gray(1, 0.5);
    @SuppressWarnings("PointlessArithmeticExpression")
    private static final Stop[] LINEAR_GRADIENT_STOPS = new Stop[]{
            new Stop(0d * (1d / 3d), rgb(237, 55, 58)),
            new Stop(1d * (1d / 3d), rgb(199, 89, 190)),
            new Stop(2d * (1d / 3d), rgb(54, 124, 224)),
            new Stop(3d * (1d / 3d), rgb(51, 221, 106))};
    private static final LinearGradient LINEAR_GRADIENT =
            new LinearGradient(0, 0, 1, 1, true, NO_CYCLE, LINEAR_GRADIENT_STOPS);

    private final ViewController viewController;

    private Rectangle background;
    private Canvas canvas;
    private GraphicsContext graphics;

    /**
     * Instantiates a new {@link TouchesView}.
     *
     * @param viewController the {@link ViewController}
     */
    public TouchesView(ViewController viewController) {
        this.viewController = viewController;
    }

    @Override
    public void start() {
        // Create nodes
        background = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, LINEAR_GRADIENT);
        canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        graphics = canvas.getGraphicsContext2D();

        // Add nodes
        nodeGroup.getChildren().addAll(background, canvas);
    }

    @Override
    public void stop() {
        // Remove nodes
        nodeGroup.getChildren().removeAll(background, canvas);

        // Destroy nodes
        background = null;
        canvas = null;
        graphics = null;
    }

    /**
     * Called to process touchscreen touches.
     *
     * @param touches the {@link Touch}es
     */
    public void processTouches(List<Touch> touches) {
        // Clear canvas
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Touch touch : touches) {
            // Draw touch area
            final double radius = touch.getSize() * 5;
            final double radiusHalf = radius / 2;
            final double drawX = touch.getX();
            final double drawY = touch.getY();
            graphics.setFill(TRANSLUCENT_WHITE);
            graphics.fillOval(drawX - radiusHalf, drawY - radiusHalf, radius, radius);

            // Draw touch number
            graphics.setFill(BLACK);
            graphics.setTextAlign(TextAlignment.CENTER);
            graphics.setTextBaseline(VPos.BOTTOM);
            graphics.fillText(String.valueOf(touch.getID()), drawX, drawY - radiusHalf - 10);
        }
    }

    public ViewController getViewController() {
        return viewController;
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
