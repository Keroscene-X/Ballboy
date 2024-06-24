package ballboy.save;

import ballboy.model.Level;

/**
 * The base interface for a save state of level.
 */
public interface Momento {
    /**
     * @return Level stored in the Momento
     */
    Level load();
}
