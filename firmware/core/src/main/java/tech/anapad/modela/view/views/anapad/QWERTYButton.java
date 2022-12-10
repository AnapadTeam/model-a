package tech.anapad.modela.view.views.anapad;

import javafx.scene.Group;
import tech.anapad.modela.usb.mapping.Keycode;
import tech.anapad.modela.usb.mapping.KeycodeModifier;
import tech.anapad.modela.view.component.button.ContentButton;

import java.util.Set;

import static tech.anapad.modela.view.ViewController.VIEW_PIXEL_DENSITY;

/**
 * {@link QWERTYButton} represents a {@link ContentButton} for a keyboard key.
 */
public class QWERTYButton extends ContentButton {

    private final KeyboardView keyboardView;
    private final Keycode keycodePrimary;
    private final KeycodeModifier keycodeModifier;

    /**
     * Instantiates a new {@link QWERTYButton}.
     *
     * @param keyboardView      the {@link KeyboardView}
     * @param keycodePrimary  the {@link Keycode}
     * @param keycodeModifier the {@link KeycodeModifier}
     * @param x               the X
     * @param y               the Y
     * @param width           the width
     * @param height          the height
     * @param arcSize         the arc size
     */
    public QWERTYButton(KeyboardView keyboardView, Keycode keycodePrimary, KeycodeModifier keycodeModifier, double x,
            double y, double width, double height, double arcSize) {
        super(width, height, arcSize);
        this.keyboardView = keyboardView;
        this.keycodePrimary = keycodePrimary;
        this.keycodeModifier = keycodeModifier;
        setTranslateX(x * VIEW_PIXEL_DENSITY);
        setTranslateY(y * VIEW_PIXEL_DENSITY);
        setContent(new Group()); // TODO fix this hack
    }

    @Override
    public void onPressDown() {
        super.onPressDown();
        if (keycodePrimary != null) {
            keyboardView.getViewController().getModelA().getUSBController()
                    .updateActiveKeycodes(Set.of(keycodePrimary), true);
        } else if (keycodeModifier != null) {
            keyboardView.getViewController().getModelA().getUSBController()
                    .updateActiveKeycodeModifiers(Set.of(keycodeModifier), true);
        }

        if (keycodeModifier == KeycodeModifier.LEFT_SHIFT || keycodeModifier == KeycodeModifier.RIGHT_SHIFT) {
            keyboardView.toggleVisibleCases();
        }

        try {
            keyboardView.getViewController().getModelA().getUSBController().flush(100, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPressUp() {
        super.onPressUp();
        if (keycodePrimary != null) {
            keyboardView.getViewController().getModelA().getUSBController()
                    .updateActiveKeycodes(Set.of(keycodePrimary), false);
        } else if (keycodeModifier != null) {
            keyboardView.getViewController().getModelA().getUSBController()
                    .updateActiveKeycodeModifiers(Set.of(keycodeModifier), false);
        }

        if (keycodeModifier == KeycodeModifier.LEFT_SHIFT || keycodeModifier == KeycodeModifier.RIGHT_SHIFT) {
            keyboardView.toggleVisibleCases();
        }

        try {
            keyboardView.getViewController().getModelA().getUSBController().flush(100, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
