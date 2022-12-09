package tech.anapad.modela.view.views;

import javafx.scene.Group;

/**
 * {@link AbstractView} represents a view.
 */
public abstract class AbstractView {

    protected final Group nodeGroup;
    protected boolean started;

    /**
     * Instantiates a new {@link AbstractView}.
     */
    public AbstractView() {
        this.nodeGroup = new Group();
    }

    /**
     * Starts this {@link AbstractView}.
     */
    public void start() {
        started = true;
    }

    /**
     * Stops this {@link AbstractView}.
     */
    public void stop() {
        started = false;
    }

    public Group getNodeGroup() {
        return nodeGroup;
    }

    public boolean isStarted() {
        return started;
    }
}
