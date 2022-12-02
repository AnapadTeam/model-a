package tech.anapad.modela.hapticsboard.lra;

import tech.anapad.modela.hapticsboard.ioportexpander.IOPortExpander;
import tech.anapad.modela.hapticsboard.lra.location.Location;
import tech.anapad.modela.hapticsboard.lra.reference.Reference;

/**
 * {@link LRA} represents an LRA on the haptics board.
 */
public class LRA {

    private final IOPortExpander ioPortExpander;
    private final int portIndex;
    private final Reference reference;
    private final Location activeAreaLocation;

    private boolean enabled;

    /**
     * Instantiates a new {@link LRA}.
     *
     * @param ioPortExpander     the {@link IOPortExpander}
     * @param portIndex          the {@link IOPortExpander} port index
     * @param reference          the {@link Reference}
     * @param activeAreaLocation the active area {@link Location}
     */
    public LRA(IOPortExpander ioPortExpander, int portIndex, Reference reference, Location activeAreaLocation) {
        this.ioPortExpander = ioPortExpander;
        this.portIndex = portIndex;
        this.reference = reference;
        this.activeAreaLocation = activeAreaLocation;
        enabled = false;
    }

    /**
     * Enables or disables this {@link LRA} by pulling its SSR gate high or low via the {@link IOPortExpander}.
     *
     * @param enable <code>true</code> to enable, <code>false</code> otherwise
     *
     * @throws Exception thrown for {@link Exception}s
     */
    public void enable(boolean enable) throws Exception {
        if (enable) {
            ioPortExpander.setOutput(portIndex);
        } else {
            ioPortExpander.resetOutput(portIndex);
        }
        enabled = enable;
    }

    public IOPortExpander getIOPortExpander() {
        return ioPortExpander;
    }

    public int getPortIndex() {
        return portIndex;
    }

    public Reference getReference() {
        return reference;
    }

    public Location getActiveAreaLocation() {
        return activeAreaLocation;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
