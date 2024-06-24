package ballboy.model;

import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.observer.Observer;
import ballboy.model.factories.EntityFactory;
import ballboy.model.levels.PhysicsEngine;
import ballboy.save.Momento;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * The base interface for a Ballboy level.
 */
public interface Level {

    /**
     * Return a List of the currently existing Entities.
     *
     * @return The list of current entities for this level
     */
    List<Entity> getEntities();

    /**
     * The height of the level
     *
     * @return The height (should be in the same format as Entity sizes)
     */
    double getLevelHeight();

    /**
     * The width of the level
     *
     * @return The width (should be in the same format as Entity sizes)
     */
    double getLevelWidth();

    /**
     * @return double The height of the hero.
     */
    double getHeroHeight();

    /**
     * @return double The width of the hero.
     */
    double getHeroWidth();

    /**
     * @return double The vertical position of the floor.
     */
    double getFloorHeight();

    /**
     * @return Color The current configured color of the floor.
     */
    Color getFloorColor();

    /**
     * @return double The current level gravity.
     */
    double getGravity();

    /**
     * Instruct the level to progress forward in time by one increment.
     */
    void update();

    /**
     * The current x position of the hero. This is useful for views so they can follow the hero.
     *
     * @return The hero x position (should be in the same format as Entity sizes)
     */
    double getHeroX();

    /**
     * The current y position of the hero. This is useful for views so they can follow the hero.
     *
     * @return The hero y position (should be in the same format as Entity sizes)
     */
    double getHeroY();

    /**
     * Increase the height the bouncing hero can reach. This could be the vertical acceleration of the hero, unless
     * the current level has special behaviour.
     *
     * @return true if successful
     */
    boolean boostHeight();

    /**
     * Reduce the height the bouncing hero can reach. This could be the vertical acceleration of the hero, unless the
     * current level has special behaviour.
     *
     * @return true if successful
     */
    boolean dropHeight();

    /**
     * Move the hero left or accelerate the hero left, depending on the current level's desired behaviour
     *
     * @return true if successful
     */
    boolean moveLeft();

    /**
     * Move the hero right or accelerate the hero right, depending on the current level's desired behaviour
     *
     * @return true if successful
     */
    boolean moveRight();

    /**
     * @param entity The entity to be checked.
     * @return boolean True if the provided entity is the current hero.
     */
    boolean isHero(Entity entity);

    /**
     * @param entity The entity to be checked.
     * @return boolean True if the provided entity is the finish of this level.
     */
    boolean isFinish(Entity entity);

    /*
     * Currently, this will just reset the hero to its starting position.
     */
    void resetHero();

    /**
     * Finishes the level.
     */
    void finish();

    /**
     * @return boolean True if the level finished
     */
    boolean isFinished();

    /**
     * @param entity The entity to be checked.
     * @return boolean True if the provided entity is an Enemy of this level.
     */
    boolean isEnemy(Entity entity);

    /**
     * @param entity The entity to be checked.
     * removes the enemy from this level.
     */
    void removeEnemy(Entity entity);

    /**
     * @return ControllableDynamicEntity<DynamicEntity> Hero of this level.
     */
    ControllableDynamicEntity<DynamicEntity> getHero();

    /**
     * @return Entity the squarecat of this level.
     */
    Entity getSquareCat();

    /**
     * @param observer to be added.
     * Add the total score observer to this level.
     */
    void addTotalObserver(Observer observer);

    /**
     * @return Observer the red score observer of this level.
     */
    Observer getRed();

    /**
     * @return Observer the green score observer of this level.
     */
    Observer getGreen();

    /**
     * @return Observer the blue score observer of this level.
     */
    Observer getBlue();

    /**
     * @return Observer the total score observer of this level.
     */
    Observer getTotal();

    /**
     * @return Entity the finish entity of this level.
     */
    Entity getFinish();

    /**
     * @return Momento create a momento which saves a copy of this level.
     */
    Momento createMomento();

    /**
     * @return Level A deep copy of this instance.
     */
    Level copy();
}
