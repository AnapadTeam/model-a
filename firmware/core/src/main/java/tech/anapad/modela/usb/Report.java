package tech.anapad.modela.usb;

/**
 * {@link Report} is an immutable model for the USB HID report data.
 */
public class Report {

    private byte buttons;
    private byte x;
    private byte y;
    private byte wheel;
    private byte modifier;
    private byte reserved;
    private byte keycode1;
    private byte keycode2;
    private byte keycode3;
    private byte keycode4;
    private byte keycode5;
    private byte keycode6;

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

        public Builder reserved(byte reserved) {
            report.reserved = reserved;
            return this;
        }

        public Builder keycode1(byte keycode1) {
            report.keycode1 = keycode1;
            return this;
        }

        public Builder keycode2(byte keycode2) {
            report.keycode2 = keycode2;
            return this;
        }

        public Builder keycode3(byte keycode3) {
            report.keycode3 = keycode3;
            return this;
        }

        public Builder keycode4(byte keycode4) {
            report.keycode4 = keycode4;
            return this;
        }

        public Builder keycode5(byte keycode5) {
            report.keycode5 = keycode5;
            return this;
        }

        public Builder keycode6(byte keycode6) {
            report.keycode6 = keycode6;
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
                modifier, reserved, keycode1, keycode2, keycode3, keycode4, keycode5, keycode6};
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

    public byte getReserved() {
        return reserved;
    }

    public byte getKeycode1() {
        return keycode1;
    }

    public byte getKeycode2() {
        return keycode2;
    }

    public byte getKeycode3() {
        return keycode3;
    }

    public byte getKeycode4() {
        return keycode4;
    }

    public byte getKeycode5() {
        return keycode5;
    }

    public byte getKeycode6() {
        return keycode6;
    }
}
