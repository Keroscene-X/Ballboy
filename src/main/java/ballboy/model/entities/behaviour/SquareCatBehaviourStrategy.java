package ballboy.model.entities.behaviour;

import ballboy.model.Level;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.utilities.Vector2D;

/**
 * An aggressive strategy that makes the entity follow the ballboy.
 * Acceleration is applied horizontally in the direction of the ballboy
 * until a maximum velocity is reached.
 */
public class SquareCatBehaviourStrategy implements BehaviourStrategy {

    private Level level;

    public SquareCatBehaviourStrategy(Level level) {
        this.level = level;
    }


    // makes the squarecat to fly in square around the hero
    @Override
    public void behave(
            DynamicEntity entity,
            double frameDurationMilli) {
        Vector2D lastPosition_cat = entity.getPositionBeforeLastUpdate();
        double newX = lastPosition_cat.getX() + level.getHeroX();
        double newY = lastPosition_cat.getY() + level.getHeroY() - 50.0;
        Vector2D newPosition = new Vector2D(newX,newY);
        entity.setPosition(newPosition);
        if (entity.getPosition().isLeftOf((level.getHeroX() + level.getHeroWidth() + 40.0)) && entity.getPosition().isAbove((level.getHeroY() - 40.0))) {
            entity.setVelocity(new Vector2D(15.0,0.0));
        } else if (entity.getPosition().isRightOf((level.getHeroX() + level.getHeroWidth() + 40.0)) && entity.getPosition().isAbove((level.getHeroY() + level.getHeroHeight()+ 40.0))){
            entity.setVelocity(new Vector2D(0.0,15.0));
        } else if (entity.getPosition().isRightOf((level.getHeroX() - 50.0)) && entity.getPosition().isBelow((level.getHeroY() + level.getHeroHeight()+ 40.0))){
            entity.setVelocity(new Vector2D(-15.0,0.0));
        } else if (entity.getPosition().isLeftOf((level.getHeroX() - 50.0)) && entity.getPosition().isBelow((level.getHeroY() + level.getHeroHeight()+ 40.0))){
            entity.setVelocity(new Vector2D(0.0,-15.0));
        }
    }

    @Override
    public BehaviourStrategy copy(Level level) {
        return new SquareCatBehaviourStrategy(level);
    }

}
