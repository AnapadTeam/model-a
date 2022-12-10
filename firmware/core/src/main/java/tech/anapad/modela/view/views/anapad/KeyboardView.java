package tech.anapad.modela.view.views.anapad;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import tech.anapad.modela.usb.mapping.Keycode;
import tech.anapad.modela.usb.mapping.KeycodeModifier;
import tech.anapad.modela.view.ViewController;
import tech.anapad.modela.view.views.AbstractView;

import java.util.ArrayList;
import java.util.List;

import static tech.anapad.modela.view.ViewController.VIEW_HEIGHT;
import static tech.anapad.modela.view.ViewController.VIEW_WIDTH;
import static tech.anapad.modela.view.util.palette.Palette.BACKGROUND_COLOR_PROPERTY;
import static tech.anapad.modela.view.util.palette.Palette.MODE_PROPERTY;

/**
 * {@link KeyboardView} is an {@link AbstractView} of an anapad.
 */
public class KeyboardView extends AbstractView {

    private final ViewController viewController;
    private final ImageView keyboardLowercaseImage;
    private final ImageView keyboardUppercaseImage;

    /**
     * Instantiates a new {@link KeyboardView}.
     *
     * @param viewController the {@link ViewController}
     */
    public KeyboardView(ViewController viewController) {
        this.viewController = viewController;

        final Rectangle background = new Rectangle(VIEW_WIDTH, VIEW_HEIGHT);
        background.fillProperty().bind(BACKGROUND_COLOR_PROPERTY);
        nodeGroup.getChildren().add(background);

        final List<QWERTYButton> qwertyButtons = new ArrayList<>();
        qwertyButtons.add(new QWERTYButton(this, Keycode.A, null, 28.481, 29.377, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.B, null, 110.688, 47.116, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.C, null, 74.136, 47.116, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.D, null, 65.051, 29.377, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.E, null, 60.465, 11.635, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.F, null, 83.325, 29.377, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.G, null, 101.599, 29.377, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.H, null, 191.286, 29.378, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.I, null, 223.27, 11.636, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.J, null, 209.581, 29.378, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.K, null, 227.857, 29.378, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.L, null, 246.131, 29.378, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.M, null, 218.665, 47.117, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.N, null, 200.37, 47.117, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.O, null, 241.544, 11.636, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.P, null, 259.82, 11.636, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.Q, null, 23.894, 11.635, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.R, null, 78.738, 11.635, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.S, null, 46.776, 29.377, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.T, null, 97.014, 11.635, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.U, null, 204.994, 11.636, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.V, null, 92.412, 47.116, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.W, null, 42.189, 11.635, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.X, null, 55.86, 47.116, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.Y, null, 186.699, 11.635, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.Z, null, 37.565, 47.116, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.APOSTROPHE_OR_DOUBLE_APOSTROPHE, null,
                264.406, 29.378, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.PERIOD_OR_RIGHT_ANGLE_BRACKET, null,
                255.216, 47.117, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.COMMA_OR_LEFT_ANGLE_BRACKET, null,
                236.941, 47.117, 15.846, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.ESCAPE, null,
                1.636, 29.376, 24.603, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.ENTER, null,
                282.29, 29.376, 24.603, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.ENTER, null,
                282.29, 29.376, 24.603, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.SPACE, null,
                78.006, 64.857, 40.592, 15.479, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.SPACE, null,
                190.033, 64.857, 40.592, 15.479, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.TAB, null,
                46.716, 2.327, 49.133, 6.841, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.BACKSPACE, null,
                203.155, 2.327, 49.133, 6.841, 2.001));
        qwertyButtons.add(new QWERTYButton(this, Keycode.BACKSPACE, null,
                203.155, 2.327, 49.133, 6.841, 2.001));

        qwertyButtons.add(new QWERTYButton(this, null, KeycodeModifier.LEFT_SHIFT,
                1.411, 47.116, 34.042, 15.478, 2.001));
        qwertyButtons.add(new QWERTYButton(this, null, KeycodeModifier.RIGHT_SHIFT,
                273.076, 47.116, 34.042, 15.478, 2.001));

        qwertyButtons.add(new QWERTYButton(this, null, KeycodeModifier.LEFT_ALT,
                33.697, 64.857, 19.809, 15.479, 2.001));
        qwertyButtons.add(new QWERTYButton(this, null, KeycodeModifier.LEFT_GUI,
                55.766, 64.857, 19.809, 15.479, 2.001));

        qwertyButtons.add(new QWERTYButton(this, null, KeycodeModifier.RIGHT_GUI,
                233.054, 64.857, 19.809, 15.479, 2.001));
        qwertyButtons.add(new QWERTYButton(this, null, KeycodeModifier.RIGHT_ALT,
                255.123, 64.857, 19.809, 15.479, 2.001));

        nodeGroup.getChildren().addAll(qwertyButtons);

        keyboardLowercaseImage = new ImageView();
        final Image keyboardLowercaseDark = new Image("image/anapad/keyboard_lowercase_dark.png");
        final Image keyboardLowercaseLight = new Image("image/anapad/keyboard_lowercase_light.png");
        keyboardLowercaseImage.imageProperty().bind(MODE_PROPERTY.map(mode -> {
            switch (mode) {
                case LIGHT -> {
                    return keyboardLowercaseLight;
                }
                case DARK -> {
                    return keyboardLowercaseDark;
                }
                default -> throw new UnsupportedOperationException();
            }
        }));
        keyboardLowercaseImage.setVisible(true);

        keyboardUppercaseImage = new ImageView();
        final Image keyboardUppercaseDark = new Image("image/anapad/keyboard_uppercase_dark.png");
        final Image keyboardUppercaseLight = new Image("image/anapad/keyboard_uppercase_light.png");
        keyboardUppercaseImage.imageProperty().bind(MODE_PROPERTY.map(mode -> {
            switch (mode) {
                case LIGHT -> {
                    return keyboardUppercaseLight;
                }
                case DARK -> {
                    return keyboardUppercaseDark;
                }
                default -> throw new UnsupportedOperationException();
            }
        }));
        keyboardUppercaseImage.setVisible(false);
        nodeGroup.getChildren().addAll(keyboardLowercaseImage, keyboardUppercaseImage);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void toggleVisibleCases() {
        keyboardLowercaseImage.setVisible(!keyboardLowercaseImage.isVisible());
        keyboardUppercaseImage.setVisible(!keyboardUppercaseImage.isVisible());
    }

    public ViewController getViewController() {
        return viewController;
    }
}
