package tech.anapad.modela.usb.report;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * {@link Report} is an immutable model for the USB HID report data.
 */
public class Report {

    private byte buttons;
    private byte x;
    private byte y;
    private byte wheel;
    private byte modifier;
    private byte[] keycodes = new byte[6];

    /**
     * {@link Builder} is an object builder for {@link Report}.
     */
    public static class Builder {

        private final Report report;

        /**
         * Instantiates a new {@link Builder}.
         */
        public Builder() {
            this.report = new Report();
        }

        public Builder buttons(byte buttons) {
            report.buttons = buttons;
            return this;
        }

        public Builder x(byte x) {
            report.x = x;
            return this;
        }

        public Builder y(byte y) {
            report.y = y;
            return this;
        }

        public Builder wheel(byte wheel) {
            report.wheel = wheel;
            return this;
        }

        public Builder modifier(byte modifier) {
            report.modifier = modifier;
            return this;
        }

        public Builder keycodes(byte[] keycodes) {
            checkArgument(keycodes != null && keycodes.length == 6);
            report.keycodes = keycodes;
            return this;
        }

        public Report build() {
            return report;
        }
    }

    /**
     * Returns a byte array of this {@link Report}.
     *
     * @return a byte array
     */
    public byte[] toByteArray() {
        return new byte[]{buttons, x, y, wheel,
                modifier, 0, keycodes[0], keycodes[1], keycodes[2], keycodes[3], keycodes[4], keycodes[5]};
    }

    public byte getButtons() {
        return buttons;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public byte getWheel() {
        return wheel;
    }

    public byte getModifier() {
        return modifier;
    }

    public byte[] getKeycodes() {
        return keycodes;
    }
}
