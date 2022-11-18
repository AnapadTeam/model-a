package tech.anapad.modela.view.mode;

import tech.anapad.modela.touchscreen.model.Touch;
import tech.anapad.modela.view.ViewController;

import static tech.anapad.modela.view.ViewController.VIEW_IMAGE_CLASSPATH;

/**
 * {@link TrackpadModeView} is a {@link AbstractModeView} for trackpad mode.
 */
public class TrackpadModeView extends AbstractModeView {

    /**
     * Instantiates a new {@link TrackpadModeView}.
     *
     * @param viewController the {@link ViewController}
     */
    public TrackpadModeView(ViewController viewController) {
        super(viewController, VIEW_IMAGE_CLASSPATH + "mode_trackpad.png");
    }

    @Override
    public void processTouches(Touch[] touches) {

    }
}
