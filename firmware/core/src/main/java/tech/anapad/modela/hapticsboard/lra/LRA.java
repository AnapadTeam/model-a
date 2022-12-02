package tech.anapad.modela.hapticsboard.lra;

import tech.anapad.modela.hapticsboard.ioportexpander.IOPortExpander;

/**
 * {@link LRA} represents an LRA on the haptics board.
 */
public class LRA {

    private final IOPortExpander ioPortExpander;
    private final int portIndex;
    private final LRAReferenceColumn referenceColumn;
    private final LRAReferenceRow referenceRow;
    private final double activeAreaX;
    private final double activeAreaY;

    /**
     * Instantiates a new {@link LRA}.
     *
     * @param ioPortExpander  the {@link IOPortExpander}
     * @param portIndex       the {@link IOPortExpander} port index
     * @param referenceColumn the {@link LRAReferenceColumn}
     * @param referenceRow    the {@link LRAReferenceRow}
     * @param activeAreaX     the active area X
     * @param activeAreaY     the active area Y
     */
    public LRA(IOPortExpander ioPortExpander, int portIndex, LRAReferenceColumn referenceColumn,
            LRAReferenceRow referenceRow, double activeAreaX, double activeAreaY) {
        this.ioPortExpander = ioPortExpander;
        this.portIndex = portIndex;
        this.referenceColumn = referenceColumn;
        this.referenceRow = referenceRow;
        this.activeAreaX = activeAreaX;
        this.activeAreaY = activeAreaY;
    }

    /**
     * Enables this {@link LRA} by pulling its SSR gate high via the {@link IOPortExpander}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void enable(int i2cFD) throws Exception {
        ioPortExpander.setOutput(i2cFD, portIndex);
    }

    /**
     * Disables this {@link LRA} by pulling its SSR gate low via the {@link IOPortExpander}.
     *
     * @param i2cFD the low-level I2C device file descriptor
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void disable(int i2cFD) throws Exception {
        ioPortExpander.resetOutput(i2cFD, portIndex);
    }

    public IOPortExpander getIOPortExpander() {
        return ioPortExpander;
    }

    public int getPortIndex() {
        return portIndex;
    }

    public LRAReferenceColumn getReferenceColumn() {
        return referenceColumn;
    }

    public LRAReferenceRow getReferenceRow() {
        return referenceRow;
    }

    public double getActiveAreaX() {
        return activeAreaX;
    }

    public double getActiveAreaY() {
        return activeAreaY;
    }
}
