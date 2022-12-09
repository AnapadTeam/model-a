package tech.anapad.modela.view.util.palette;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static javafx.scene.paint.Color.rgb;
import static tech.anapad.modela.view.util.palette.Mode.LIGHT;

/**
 * {@link Palette} represents the color palette used for various anapad views.
 */
public class Palette {

    public static final ObjectProperty<Mode> MODE_PROPERTY = new SimpleObjectProperty<>(LIGHT);
    public static final Color BACKGROUND_COLOR_LIGHT = rgb(204, 204, 204);
    public static final Color BACKGROUND_COLOR_DARK = rgb(26, 26, 26);
    public static final ObjectProperty<Paint> BACKGROUND_COLOR_PROPERTY = new SimpleObjectProperty<>();
    public static final Color FOREGROUND_COLOR_LIGHT = rgb(255, 255, 255);
    public static final Color FOREGROUND_COLOR_DARK = rgb(77, 77, 77);
    public static final ObjectProperty<Paint> FOREGROUND_COLOR_PROPERTY = new SimpleObjectProperty<>();
    public static final Color TEXT_COLOR_LIGHT = rgb(0, 0, 0);
    public static final Color TEXT_COLOR_DARK = rgb(255, 255, 255);
    public static final ObjectProperty<Paint> TEXT_COLOR_PROPERTY = new SimpleObjectProperty<>();
    public static final Color ACCENT_COLOR_LIGHT = rgb(204, 230, 255);
    public static final Color ACCENT_COLOR_DARK = rgb(67, 89, 112);
    public static final ObjectProperty<Paint> ACCENT_COLOR_PROPERTY = new SimpleObjectProperty<>();
    public static final Color ACCENT_DEEP_COLOR_LIGHT = rgb(118, 178, 218);
    public static final Color ACCENT_DEEP_COLOR_DARK = rgb(51, 77, 117);
    public static final ObjectProperty<Paint> ACCENT_DEEP_COLOR_PROPERTY = new SimpleObjectProperty<>();

    static {
        setLightDarkMode(LIGHT);
    }

    /**
     * Sets static {@link ObjectProperty ObjectProperties} to either light or dark mode colors. Must be called on the
     * JavaFX thread.
     *
     * @param mode the {@link Mode}
     */
    public static void setLightDarkMode(Mode mode) {
        MODE_PROPERTY.set(mode);
        BACKGROUND_COLOR_PROPERTY.set(mode == LIGHT ? BACKGROUND_COLOR_LIGHT : BACKGROUND_COLOR_DARK);
        FOREGROUND_COLOR_PROPERTY.set(mode == LIGHT ? FOREGROUND_COLOR_LIGHT : FOREGROUND_COLOR_DARK);
        TEXT_COLOR_PROPERTY.set(mode == LIGHT ? TEXT_COLOR_LIGHT : TEXT_COLOR_DARK);
        ACCENT_COLOR_PROPERTY.set(mode == LIGHT ? ACCENT_COLOR_LIGHT : ACCENT_COLOR_DARK);
        ACCENT_DEEP_COLOR_PROPERTY.set(mode == LIGHT ? ACCENT_DEEP_COLOR_LIGHT : ACCENT_DEEP_COLOR_DARK);
    }
}
