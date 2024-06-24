package ballboy.model.entities.collision;

import ballboy.model.Entity;
import ballboy.model.Level;

/**
 * Collision logic for enemies.
 */
public class SquareCatCollisionStrategy implements CollisionStrategy {
    private final Level level;

    public SquareCatCollisionStrategy(Level level) {
        this.level = level;
    }

    //removes the enemy when squarecat collides with it.
    @Override
    public void collideWith(
            Entity squareCat,
            Entity enemy) {
        if (level.isEnemy(enemy)) {
            level.removeEnemy(enemy);
        }
    }

    @Override
    public CollisionStrategy copy(Level level) {
        return new SquareCatCollisionStrategy(level);
    }
}
