package tech.anapad.modela.usb.mapping;

/**
 * {@link KeycodeModifier} represents a key code from the USB HID Keyboard/Keypad usage page.
 *
 * @see "Page 88 of USB HID Usage Tables"
 */
public enum KeycodeModifier {

    LEFT_CONTROL(1),
    LEFT_SHIFT(1 << 1),
    LEFT_ALT(1 << 2),
    LEFT_GUI(1 << 3),
    RIGHT_CONTROL(1 << 4),
    RIGHT_SHIFT(1 << 5),
    RIGHT_ALT(1 << 6),
    RIGHT_GUI(1 << 7);

    private final byte bitmask;

    /**
     * Instantiates a new {@link KeycodeModifier}.
     *
     * @param bitmask the USB HID keycode modifier usage ID code bitmask
     */
    KeycodeModifier(int bitmask) {
        this.bitmask = (byte) (bitmask & 0xFF);
    }

    public byte getBitmask() {
        return bitmask;
    }
}
