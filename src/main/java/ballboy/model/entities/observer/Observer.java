package ballboy.model.entities.observer;

/**
 * Observer updates when its publisher updates.
 */
public interface Observer {

    /**
     * updates the values in the Observer.
     */
    void update();

    int getValue();

    /**
     * @return Observer A deep copy of this instance.
     */
    Observer copy();
}
