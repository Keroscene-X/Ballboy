package ballboy.model;

import ballboy.model.entities.observer.Observer;
import ballboy.model.entities.utilities.AxisAlignedBoundingBox;
import ballboy.model.entities.utilities.Vector2D;
import javafx.scene.image.Image;
import org.json.simple.JSONObject;

public interface Entity {
    /**
     * Returns the current Image used by this Entity. This may change over time, such as for simple animations.
     *
     * @return An Image representing the current state of this Entity
     */
    Image getImage();

    /**
     * @return Vector2 The current position of the entity, being the top left anchor.
     */
    Vector2D getPosition();

    /**
     * Returns the current height of this Entity
     *
     * @return The height in coordinate space (e.g. number of pixels)
     */
    double getHeight();

    /**
     * Returns the current width of this Entity
     *
     * @return The width in coordinate space (e.g. number of pixels)
     */
    double getWidth();

    /**
     * Returns the current 'z' position to draw this entity. Order within each layer is undefined.
     *
     * @return The layer to draw the entity on.
     */
    Layer getLayer();

    /**
     * @return AxisAlignedBoundingBox The enclosing volume of this entity.
     */
    AxisAlignedBoundingBox getVolume();

    /**
     * @param observer to be added.
     * Add a score observer to this Entity.
     */
    void addObserver(Observer observer);

    /**
     * Instruct all score observers in this entity to update.
     */
    void updateObservers();

    /**
     * @return JSONObject the configuration saved in this entity.
     */
    JSONObject getConfig();

    /**
     * @return Entity A deep copy of this instance.
     */
    Entity copy(Level level);

    /**
     * The set of available layers
     */
    enum Layer {
        BACKGROUND, FOREGROUND, EFFECT
    }
}
