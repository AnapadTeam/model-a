package tech.anapad.modela.view.component.button;

import tech.anapad.modela.loadsurface.sample.Sample;
import tech.anapad.modela.touchscreen.driver.Touch;
import tech.anapad.modela.view.component.AbstractComponent;

/**
 * {@link Button} is an {@link AbstractComponent} button.
 */
public abstract class Button extends AbstractComponent {

    protected boolean isTouchedDown;
    protected boolean isPressedDown;
    protected boolean isForcePressedDown;

    /**
     * Instantiates a new {@link Button}.
     */
    public Button() {
        isTouchedDown = false;
        isPressedDown = false;
        isForcePressedDown = false;
    }

    /**
     * Gets if the given {@link Touch} is contained in this {@link Button}.
     *
     * @param touch the {@link Touch}
     *
     * @return <code>true</code> if it is contained, <code>false</code> otherwise
     */
    public boolean containsTouch(Touch touch) {
        return getBoundsInParent().contains(touch.getX(), touch.getY());
    }

    /**
     * Called when this {@link Button} is touched down upon.
     */
    public void onTouchDown() {
        isTouchedDown = true;
    }

    /**
     * Called when this {@link Button} is touched up upon.
     */
    public void onTouchUp() {
        isTouchedDown = false;
    }

    /**
     * Called when this {@link Button} is pressed down upon.
     */
    public void onPressDown() {
        isPressedDown = true;
    }

    /**
     * Called when this {@link Button} is pressed up upon.
     */
    public void onPressUp() {
        isPressedDown = false;
    }

    /**
     * Called when this {@link Button} is force pressed down upon.
     */
    public void onForcePressDown() {
        isForcePressedDown = true;
    }

    /**
     * Called when this {@link Button} is force pressed up upon.
     */
    public void onForcePressUp() {
        isForcePressedDown = false;
    }

    /**
     * Gets the percentage threshold in relation to {@link Sample#getPercentOffsetSample()} that should trigger a press
     * of this {@link Button}.
     *
     * @return a double
     */
    public double getPressDownThreshold() {
        return 0.04;
    }

    /**
     * Gets the percentage threshold in relation to {@link Sample#getPercentOffsetSample()} that should trigger a
     * release of this {@link Button}.
     *
     * @return a double
     */
    public double getPressUpThreshold() {
        return 0.03;
    }

    /**
     * Gets the percentage threshold in relation to {@link Sample#getPercentOffsetSample()} that should trigger a force
     * press of this {@link Button}.
     *
     * @return a double
     */
    public double getForcePressDownThreshold() {
        return 0.10;
    }

    /**
     * Gets the percentage threshold in relation to {@link Sample#getPercentOffsetSample()} that should trigger a force
     * release of this {@link Button}.
     *
     * @return a double
     */
    public double getForcePressUpThreshold() {
        return 0.06;
    }

    public boolean isTouchedDown() {
        return isTouchedDown;
    }

    public boolean isPressedDown() {
        return isPressedDown;
    }

    public boolean isForcePressedDown() {
        return isForcePressedDown;
    }
}
