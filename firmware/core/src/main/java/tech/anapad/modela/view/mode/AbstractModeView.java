package tech.anapad.modela.view.mode;

import javafx.scene.image.ImageView;
import tech.anapad.modela.touchscreen.Touch;
import tech.anapad.modela.view.ViewController;

/**
 * {@link AbstractModeView} is an abstract class for mode views.
 */
public abstract class AbstractModeView {

    protected final ViewController viewController;
    protected final ImageView modeImageView;

    /**
     * Instantiates a new {@link AbstractModeView}.
     *
     * @param viewController the {@link ViewController}
     */
    public AbstractModeView(ViewController viewController, String modeViewImageClasspath) {
        this.viewController = viewController;

        modeImageView = new ImageView(modeViewImageClasspath);
        modeImageView.setX(0);
        modeImageView.setY(0);
        modeImageView.setVisible(false);
    }

    /**
     * Called when mode is active and there are touchscreen touches to process.
     *
     * @param touches the {@link Touch}es
     */
    public abstract void processTouches(Touch[] touches);

    public ViewController getViewController() {
        return viewController;
    }

    public ImageView getModeImageView() {
        return modeImageView;
    }
}
