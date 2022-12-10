package tech.anapad.modela.view.views.debug.touch;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.view.ViewController;
import tech.anapad.modela.view.views.AbstractView;

import java.util.List;

import static javafx.scene.paint.Color.gray;
import static javafx.scene.paint.Color.rgb;
import static javafx.scene.paint.CycleMethod.NO_CYCLE;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;
import static tech.anapad.modela.view.util.palette.Mode.LIGHT;
import static tech.anapad.modela.view.util.palette.Palette.MODE_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.TEXT_COLOR_PROPERTY;

/**
 * {@link TouchesView} is an {@link AbstractView} used for rendering touches on the screen as a debugging measure.
 */
public class TouchesView extends AbstractView {

    private static final Color TRANSLUCENT_WHITE = gray(1, 0.6);
    private static final Color TRANSLUCENT_BLACK = gray(1, 0.2);
    @SuppressWarnings("PointlessArithmeticExpression")
    private static final Stop[] LINEAR_GRADIENT_STOPS = new Stop[]{
            new Stop(0d * (1d / 3d), rgb(237, 55, 58)),
            new Stop(1d * (1d / 3d), rgb(199, 89, 190)),
            new Stop(2d * (1d / 3d), rgb(54, 124, 224)),
            new Stop(3d * (1d / 3d), rgb(51, 221, 106))};
    private static final LinearGradient LINEAR_GRADIENT =
            new LinearGradient(0, 0, 1, 1, true, NO_CYCLE, LINEAR_GRADIENT_STOPS);

    private final ViewController viewController;
    private final Canvas canvas;
    private final GraphicsContext graphics;

    /**
     * Instantiates a new {@link TouchesView}.
     *
     * @param viewController the {@link ViewController}
     */
    public TouchesView(ViewController viewController) {
        this.viewController = viewController;

        // Set clip
        nodeGroup.setClip(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT));

        // Create nodes
        Rectangle background = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, LINEAR_GRADIENT);
        canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        graphics = canvas.getGraphicsContext2D();

        // Add nodes
        nodeGroup.getChildren().addAll(background, canvas);
    }

    /**
     * Called to process touchscreen touches.
     *
     * @param touches the {@link Touch}es
     */
    public void processRawTouches(List<Touch> touches) {
        // Clear canvas
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Touch touch : touches) {
            // Draw touch area
            final double radius = touch.getSize() * 10;
            final double radiusHalf = radius / 2;
            final double drawX = touch.getX();
            final double drawY = touch.getY();
            graphics.setFill(MODE_PROPERTY.get() == LIGHT ? TRANSLUCENT_WHITE : TRANSLUCENT_BLACK);
            graphics.fillOval(drawX - radiusHalf, drawY - radiusHalf, radius, radius);

            // Draw touch number
            graphics.setFill(TEXT_COLOR_PROPERTY.get());
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
