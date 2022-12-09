package tech.anapad.modela.view.views.menu;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.shape.Rectangle;
import tech.anapad.modela.view.ViewController;
import tech.anapad.modela.view.component.button.ContentButton;
import tech.anapad.modela.view.views.AbstractView;

import static javafx.scene.paint.Color.TRANSPARENT;
import static javafx.scene.text.FontWeight.BOLD;
import static javafx.scene.text.FontWeight.NORMAL;
import static javafx.util.Duration.ZERO;
import static javafx.util.Duration.millis;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_PIXEL_DENSITY;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;
import static tech.anapad.modela.view.util.LabelUtil.label;
import static tech.anapad.modela.view.util.palette.Mode.DARK;
import static tech.anapad.modela.view.util.palette.Mode.LIGHT;
import static tech.anapad.modela.view.util.palette.Palette.setLightDarkMode;

/**
 * {@link MenuView} is an {@link AbstractView} for the menu.
 */
public class MenuView extends AbstractView {

    public ViewController viewController;

    private Label demosLabel;
    private ContentButton touchesViewButton;
    private ContentButton loadSurfacesViewButton;
    private ContentButton forceHapticsViewButton;
    private Label themeLabel;
    private ContentButton lightModeButton;
    private ContentButton darkModeButton;
    private boolean showing;
    private boolean transitioning;

    /**
     * Instantiates a new {@link MenuView}.
     *
     * @param viewController the {@link ViewController}
     */
    public MenuView(ViewController viewController) {
        this.viewController = viewController;
        showing = false;
        transitioning = false;
    }

    @Override
    public void start() {
        super.start();

        nodeGroup.getChildren().add(new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, TRANSPARENT));

        final double buttonWidthMm = 40.0;
        final double halfButtonWidthPx = buttonWidthMm * VIEW_PIXEL_DENSITY / 2.0;
        final double buttonHeight = 7.0;
        final double buttonArcSize = 4.0;

        final double demosX = VIEW_WIDTH * 0.33;
        demosLabel = label("Demos", BOLD, 25);
        demosLabel.translateXProperty().bind(demosLabel.widthProperty().divide(-2).add(demosX));
        demosLabel.setTranslateY(20.0 * VIEW_PIXEL_DENSITY);
        touchesViewButton = new ContentButton(buttonWidthMm, buttonHeight, buttonArcSize,
                label("Touches", NORMAL, 15), null, () -> {
            viewController.setActiveView(viewController.getTouchesView());
            toggle();
        });
        touchesViewButton.setTranslateX(demosX - halfButtonWidthPx);
        touchesViewButton.setTranslateY(30.0 * VIEW_PIXEL_DENSITY);
        loadSurfacesViewButton = new ContentButton(buttonWidthMm, buttonHeight, buttonArcSize,
                label("Load Surfaces", NORMAL, 15), null, () -> {
            viewController.setActiveView(viewController.getLoadSurfacesView());
            toggle();
        });
        loadSurfacesViewButton.setTranslateX(demosX - halfButtonWidthPx);
        loadSurfacesViewButton.setTranslateY(40.0 * VIEW_PIXEL_DENSITY);
        forceHapticsViewButton = new ContentButton(buttonWidthMm, buttonHeight, buttonArcSize,
                label("Force Haptics", NORMAL, 15), null, () -> {
            viewController.setActiveView(viewController.getForceHapticsView());
            toggle();
        });
        forceHapticsViewButton.setTranslateX(demosX - halfButtonWidthPx);
        forceHapticsViewButton.setTranslateY(50.0 * VIEW_PIXEL_DENSITY);

        final double themeX = VIEW_WIDTH * 0.66;
        themeLabel = label("Theme", BOLD, 25);
        themeLabel.translateXProperty().bind(themeLabel.widthProperty().divide(-2).add(themeX));
        themeLabel.setTranslateY(20.0 * VIEW_PIXEL_DENSITY);
        lightModeButton = new ContentButton(buttonWidthMm, buttonHeight, buttonArcSize,
                label("Light", NORMAL, 15), null, () -> {
            setLightDarkMode(LIGHT);
            toggle();
        });
        lightModeButton.setTranslateX(themeX - halfButtonWidthPx);
        lightModeButton.setTranslateY(30.0 * VIEW_PIXEL_DENSITY);
        darkModeButton = new ContentButton(buttonWidthMm, buttonHeight, buttonArcSize,
                label("Dark", NORMAL, 15), null, () -> {
            setLightDarkMode(DARK);
            toggle();
        });
        darkModeButton.setTranslateX(themeX - halfButtonWidthPx);
        darkModeButton.setTranslateY(40.0 * VIEW_PIXEL_DENSITY);

        getNodeGroup().getChildren().addAll(demosLabel,
                touchesViewButton, loadSurfacesViewButton, forceHapticsViewButton,
                themeLabel, lightModeButton, darkModeButton);
        getNodeGroup().setOpacity(0.0);
    }

    @Override
    public void stop() {
        super.stop();
        getNodeGroup().getChildren().clear();
    }

    /**
     * Shows this {@link MenuView}.
     */
    public synchronized void toggle() {
        if (transitioning) {
            return;
        }
        transitioning = true;

        if (viewController.getActiveView() != null) {
            final BoxBlur boxBlur = new BoxBlur();
            boxBlur.setIterations(2);
            viewController.getActiveView().getNodeGroup().setEffect(boxBlur);
            final double blurSize = 5.0;
            final double blurFrom = showing ? blurSize : 0.0;
            final double blurTo = showing ? 0.0 : blurSize;
            final double periodMillis = 250.0;
            timeline(blurFrom, blurTo, periodMillis, boxBlur.widthProperty());
            timeline(blurFrom, blurTo, periodMillis, boxBlur.heightProperty());
            timeline(showing ? 1.0 : 0.0, showing ? 0.0 : 1.0, periodMillis, nodeGroup.opacityProperty())
                    .setOnFinished(event -> {
                        transitioning = false;
                        if (!showing) {
                            viewController.getActiveView().getNodeGroup().setEffect(null);
                        }
                    });
        }

        showing = !showing;
    }

    private Timeline timeline(double from, double to, double millis, DoubleProperty target) {
        final Timeline timeline = new Timeline();
        final KeyFrame fromFrame = new KeyFrame(ZERO, new KeyValue(target, from));
        final KeyFrame toFrame = new KeyFrame(millis(millis), new KeyValue(target, to));
        timeline.getKeyFrames().addAll(fromFrame, toFrame);
        timeline.play();
        return timeline;
    }

    public boolean isShowing() {
        return showing;
    }
}
