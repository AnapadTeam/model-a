package tech.anapad.modela.view.mode;

import tech.anapad.modela.touchscreen.model.Touch;
import tech.anapad.modela.view.ViewController;

import static tech.anapad.modela.view.ViewController.VIEW_IMAGE_CLASSPATH;

/**
 * {@link KeyboardModeView} is a {@link AbstractModeView} for keyboard mode.
 */
public class KeyboardModeView extends AbstractModeView {

    /**
     * Instantiates a new {@link KeyboardModeView}.
     *
     * @param viewController the {@link ViewController}
     */
    public KeyboardModeView(ViewController viewController) {
        super(viewController, VIEW_IMAGE_CLASSPATH + "mode_keyboard.png");
    }

    @Override
    public void processTouches(Touch[] touches) {

    }
}
