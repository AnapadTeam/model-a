package tech.anapad.modela.usb;

/**
 * {@link MouseButton} represents a mouse button from the USB HID Button usage page.
 *
 * @see "Page 108 of USB HID Usage Tables"
 */
public enum MouseButton {

    LEFT(1 << 1),
    RIGHT(1 << 2),
    MIDDLE(1 << 3);

    private final byte bitmask;

    /**
     * Instantiates a new {@link MouseButton}.
     *
     * @param bitmask the USB HID mouse button usage ID bit mask
     */
    MouseButton(int bitmask) {
        this.bitmask = (byte) (bitmask & 0xFF);
    }

    public byte getBitmask() {
        return bitmask;
    }
}
