package tech.anapad.modela.view.util.font;

import javafx.scene.text.FontWeight;

import static javafx.scene.text.Font.loadFont;

/**
 * {@link FontUtil} is a utility class for fonts.
 */
public class FontUtil {

    public static final String INTER_FONT_FAMILY;

    static {
        // Load Inter fonts up front
        String interFontFamily = null;
        for (FontWeight fontWeight : FontWeight.values()) {
            final String interFontPath = "font/inter/inter-" + fontWeight.getWeight() + ".ttf";
            interFontFamily = loadFont(interFontPath, 12).getFamily(); // TODO fix NPE
        }
        INTER_FONT_FAMILY = interFontFamily;
    }
}
