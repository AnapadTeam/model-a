package tech.anapad.modela.view;

import javafx.scene.Group;

/**
 * {@link AbstractView} represents a view.
 */
public abstract class AbstractView {

    protected final Group nodeGroup;

    /**
     * Instantiates a new {@link AbstractView}.
     */
    public AbstractView() {
        this.nodeGroup = new Group();
    }

    /**
     * Starts this {@link AbstractView}.
     */
    public abstract void start();

    /**
     * Stops this {@link AbstractView}.
     */
    public abstract void stop();

    public Group getNodeGroup() {
        return nodeGroup;
    }
}
