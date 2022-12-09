package tech.anapad.modela.view.component.button;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import static tech.anapad.modela.view.ViewController.VIEW_PIXEL_DENSITY;
import static tech.anapad.modela.view.util.palette.Palette.ACCENT_COLOR_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.ACCENT_DEEP_COLOR_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.FOREGROUND_COLOR_PROPERTY;

/**
 * {@link ContentButton} represents a button with content;
 */
public class ContentButton extends Button {

    protected final Rectangle background;
    protected final Runnable onTouchDownHandler;
    protected final Runnable onTouchUpHandler;
    protected final Runnable onPressDownHandler;
    protected final Runnable onPressUpHandler;
    protected final Runnable onForcePressDownHandler;
    protected final Runnable onForcePressUpHandler;

    /**
     * Instantiates a new {@link ContentButton}.
     *
     * @param width   the width in mm
     * @param height  the height in mm
     * @param arcSize the arc size in mm
     */
    public ContentButton(double width, double height, double arcSize) {
        this(width, height, arcSize, null, null, null, null, null, null, null);
    }

    /**
     * Instantiates a new {@link ContentButton}.
     *
     * @param width              the width in mm
     * @param height             the height in mm
     * @param arcSize            the arc size in mm
     * @param content            the content
     * @param onPressDownHandler the press down handler
     * @param onPressUpHandler   the press up handler
     */
    public ContentButton(double width, double height, double arcSize, Node content,
            Runnable onPressDownHandler, Runnable onPressUpHandler) {
        this(width, height, arcSize, content, null, null, onPressDownHandler, onPressUpHandler, null, null);
    }

    /**
     * Instantiates a new {@link ContentButton}.
     *
     * @param width                   the width in mm
     * @param height                  the height in mm
     * @param arcSize                 the arc size in mm
     * @param content                 the content
     * @param onTouchDownHandler      the touch down handler
     * @param onTouchUpHandler        the touch up handler
     * @param onPressDownHandler      the press down handler
     * @param onPressUpHandler        the press up handler
     * @param onForcePressDownHandler the force press down handler
     * @param onForcePressUpHandler   the force press up handler
     */
    public ContentButton(double width, double height, double arcSize, Node content,
            Runnable onTouchDownHandler, Runnable onTouchUpHandler,
            Runnable onPressDownHandler, Runnable onPressUpHandler,
            Runnable onForcePressDownHandler, Runnable onForcePressUpHandler) {
        background = new Rectangle();
        background.setWidth(width * VIEW_PIXEL_DENSITY);
        background.setHeight(height * VIEW_PIXEL_DENSITY);
        background.setArcWidth(arcSize * VIEW_PIXEL_DENSITY);
        background.setArcHeight(arcSize * VIEW_PIXEL_DENSITY);
        background.fillProperty().bind(FOREGROUND_COLOR_PROPERTY);
        if (content != null) {
            getChildren().add(new StackPane(background, content));
        }

        this.onTouchDownHandler = onTouchDownHandler;
        this.onTouchUpHandler = onTouchUpHandler;
        this.onPressDownHandler = onPressDownHandler;
        this.onPressUpHandler = onPressUpHandler;
        this.onForcePressDownHandler = onForcePressDownHandler;
        this.onForcePressUpHandler = onForcePressUpHandler;
    }

    /**
     * Sets the content of this {@link ContentButton}.
     *
     * @param content the {@link Node} content
     */
    public void setContent(Node content) {
        getChildren().clear();
        getChildren().add(new StackPane(background, content));
    }

    @Override
    public void onTouchDown() {
        super.onTouchDown();
        if (onTouchDownHandler != null) {
            onTouchDownHandler.run();
        }
    }

    @Override
    public void onTouchUp() {
        super.onTouchUp();
        if (onTouchUpHandler != null) {
            onTouchUpHandler.run();
        }
    }

    @Override
    public void onPressDown() {
        super.onPressDown();
        background.fillProperty().unbind();
        background.fillProperty().set(ACCENT_COLOR_PROPERTY.get());
        if (onPressDownHandler != null) {
            onPressDownHandler.run();
        }
    }

    @Override
    public void onPressUp() {
        super.onPressUp();
        background.fillProperty().unbind();
        background.fillProperty().bind(FOREGROUND_COLOR_PROPERTY);
        if (onPressUpHandler != null) {
            onPressUpHandler.run();
        }
    }

    @Override
    public void onForcePressDown() {
        super.onForcePressDown();
        background.fillProperty().unbind();
        background.fillProperty().set(ACCENT_DEEP_COLOR_PROPERTY.get());
        if (onForcePressDownHandler != null) {
            onForcePressDownHandler.run();
        }
    }

    @Override
    public void onForcePressUp() {
        super.onForcePressUp();
        background.fillProperty().unbind();
        if (isPressedDown) {
            background.fillProperty().set(ACCENT_COLOR_PROPERTY.get());
        } else {
            background.fillProperty().bind(FOREGROUND_COLOR_PROPERTY);
        }
        if (onForcePressUpHandler != null) {
            onForcePressUpHandler.run();
        }
    }
}
