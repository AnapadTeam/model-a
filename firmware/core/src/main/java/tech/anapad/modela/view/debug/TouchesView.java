package tech.anapad.modela.view.debug;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.view.ViewController;

import static javafx.scene.paint.Color.BLACK;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;

/**
 * {@link TouchesView} is used for rendering touches on the screen as a debugging measure.
 */
public class TouchesView {

    private static final Color TRANSLUCENT_BLACK = Color.gray(0, 0.5);

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
        canvas = new Canvas(VIEW_WIDTH, VIEW_HEIGHT);
        graphics = canvas.getGraphicsContext2D();
    }

    /**
     * Called to process touchscreen touches.
     *
     * @param touches the {@link Touch}es
     */
    public void processTouches(Touch[] touches) {
        // Clear canvas
        graphics.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Touch touch : touches) {
            // Draw touch area
            final double radius = touch.getSize() * 5;
            final double radiusHalf = radius / 2;
            final double drawX = touch.getX();
            final double drawY = touch.getY();
            graphics.setFill(TRANSLUCENT_BLACK);
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
