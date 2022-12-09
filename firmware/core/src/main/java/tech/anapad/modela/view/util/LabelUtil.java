package tech.anapad.modela.view.util;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import static tech.anapad.modela.view.util.palette.Palette.TEXT_COLOR_PROPERTY;

/**
 * {@link LabelUtil} contains utility functions for {@link Label}s.
 */
public class LabelUtil {

    /**
     * Gets a {@link Label} in <code>System</code> font.
     *
     * @param text       the text
     * @param fontWeight the {@link FontWeight}
     * @param size       the size
     *
     * @return the {@link Label}
     */
    public static Label label(String text, FontWeight fontWeight, double size) {
        final Label label = new Label(text);
        label.textFillProperty().bind(TEXT_COLOR_PROPERTY);
        label.setFont(Font.font("System", fontWeight, size));
        return label;
    }
}
