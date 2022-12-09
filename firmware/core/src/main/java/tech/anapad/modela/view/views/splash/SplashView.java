package tech.anapad.modela.view.views.splash;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tech.anapad.modela.view.views.AbstractView;

import static javafx.util.Duration.millis;
import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;

/**
 * {@link SplashView} is an {@link AbstractView} for the splash screen.
 */
public class SplashView extends AbstractView {

    private final EventHandler<ActionEvent> doneHandler;

    /**
     * Instantiates a new {@link SplashView}.
     *
     * @param doneHandler the {@link EventHandler} for when this {@link SplashView} is done rendering
     */
    public SplashView(EventHandler<ActionEvent> doneHandler) {
        this.doneHandler = doneHandler;
    }

    @Override
    public void start() {
        final Rectangle background = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT, Color.BLACK);

        final ImageView splashImage = new ImageView("image/icon/white_splash_screen.png");
        splashImage.setOpacity(0.0);
        final FadeTransition splashImageFadeIn = new FadeTransition(millis(750), splashImage);
        splashImageFadeIn.setFromValue(0.0);
        splashImageFadeIn.setToValue(1.0);
        final PauseTransition splashImagePause = new PauseTransition(millis(1500));
        final FadeTransition splashImageFadeOut = new FadeTransition(millis(500), splashImage);
        splashImageFadeOut.setFromValue(1.0);
        splashImageFadeOut.setToValue(0.0);
        final SequentialTransition splashImageTransition = new SequentialTransition(splashImage,
                splashImageFadeIn, splashImagePause, splashImageFadeOut);

        nodeGroup.getChildren().add(new StackPane(background, splashImage));

        splashImageTransition.play();
        splashImageTransition.setOnFinished(doneHandler);
    }

    @Override
    public void stop() {
        nodeGroup.getChildren().clear();
    }
}
