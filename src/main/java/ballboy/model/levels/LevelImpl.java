package ballboy.model.levels;

import ballboy.ConfigurationParseException;
import ballboy.model.Entity;
import ballboy.model.Level;
import ballboy.model.entities.ControllableDynamicEntity;
import ballboy.model.entities.DynamicEntity;
import ballboy.model.entities.StaticEntity;
import ballboy.model.entities.behaviour.AggressiveEnemyBehaviourStrategy;
import ballboy.model.entities.behaviour.PassiveEntityBehaviourStrategy;
import ballboy.model.entities.behaviour.ScaredEnemyBehaviourStrategy;
import ballboy.model.entities.observer.Observer;
import ballboy.model.entities.observer.ScoreObserver;
import ballboy.model.entities.utilities.Vector2D;
import ballboy.model.factories.EntityFactory;
import ballboy.save.LevelMomento;
import ballboy.save.Momento;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * Level logic, with abstract factor methods.
 */
public class LevelImpl implements Level {

    private final List<Entity> entities = new ArrayList<>();
    private final PhysicsEngine engine;
    private final EntityFactory entityFactory;
    private ControllableDynamicEntity<DynamicEntity> hero;
    private Entity finish;
    private double levelHeight;
    private double levelWidth;
    private double levelGravity;
    private double floorHeight;
    private Color floorColor;
    private boolean finished = false;
    private List<Entity> enemies = new ArrayList<>();
    private Entity squarecat;
    private Observer red;
    private Observer green;
    private Observer blue;
    private Observer total;

    private final double frameDurationMilli;

    /**
     * A callback queue for post-update jobs. This is specifically useful for scheduling jobs mid-update
     * that require the level to be in a valid state.
     */
    private final Queue<Runnable> afterUpdateJobQueue = new ArrayDeque<>();

    public LevelImpl(
            JSONObject levelConfiguration,
            PhysicsEngine engine,
            EntityFactory entityFactory,
            double frameDurationMilli) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.frameDurationMilli = frameDurationMilli;
        initLevel(levelConfiguration);
    }

    public LevelImpl(
            List<Entity> entities,
            PhysicsEngine engine,
            EntityFactory entityFactory,
            double frameDurationMilli,
            ControllableDynamicEntity<DynamicEntity> hero,
            Entity finish,
            double levelHeight,
            double levelWidth,
            double levelGravity,
            double floorHeight,
            Color floorColor,
            boolean finished,
            List<Entity> enemies,
            Entity squarecat,
            Observer red,
            Observer green,
            Observer blue,
            Observer total) {
        this.engine = engine;
        this.entityFactory = entityFactory;
        this.frameDurationMilli = frameDurationMilli;
        this.levelHeight = levelHeight;
        this.levelWidth = levelWidth;
        this.levelGravity = levelGravity;
        this.floorHeight = floorHeight;
        this.floorColor = floorColor;
        this.finished = finished;
        this.red = red.copy();
        this.green = green.copy();
        this.blue = blue.copy();
        if (total != null){
            this.total = total.copy();
        }
        for (Entity entity : entities) {
            Entity newEntity = entity.copy(this);
            this.entities.add(newEntity);
            if (entity == hero) {
                this.hero = (ControllableDynamicEntity<DynamicEntity>) newEntity;
            } else if (entity == finish) {
                this.finish = newEntity;
            } else if (entity == squarecat) {
                this.squarecat = newEntity;
            }
            for (Entity enemy : enemies) {
                JSONObject config = enemy.getConfig();
                if (enemy == entity) {
                    newEntity.addObserver(this.total);
                    switch ((String) config.get("color")) {
                        case "red":
                            newEntity.addObserver(this.red);
                            break;
                        case "blue":
                            newEntity.addObserver(this.blue);
                            break;
                        case "green":
                            newEntity.addObserver(this.green);
                            break;
                        default:
                            throw new ConfigurationParseException(
                                    String.format("%s is not a valid color\n", config.get("color")));
                    }
                    this.enemies.add(newEntity);
                }
            }
        }
    }



    /**
     * Instantiates a level from the level configuration.
     *
     * @param levelConfiguration The configuration for the level.
     */
    private void initLevel(JSONObject levelConfiguration) {
        this.levelWidth = ((Number) levelConfiguration.get("levelWidth")).doubleValue();
        this.levelHeight = ((Number) levelConfiguration.get("levelHeight")).doubleValue();
        this.levelGravity = ((Number) levelConfiguration.get("levelGravity")).doubleValue();

        this.red = new ScoreObserver();
        this.green = new ScoreObserver();
        this.blue = new ScoreObserver();

        JSONObject floorJson = (JSONObject) levelConfiguration.get("floor");
        this.floorHeight = ((Number) floorJson.get("height")).doubleValue();
        String floorColorWeb = (String) floorJson.get("color");
        this.floorColor = Color.web(floorColorWeb);

        JSONArray generalEntities = (JSONArray) levelConfiguration.get("genericEntities");
        for (Object o : generalEntities) {
            JSONObject obj = (JSONObject) o;
            Entity unique_entity = entityFactory.createEntity(this, obj);
            this.entities.add(unique_entity);
            if (obj.get("type").equals("enemy")){
                enemies.add(unique_entity);
                switch ((String) obj.get("color")) {
                    case "red":
                        unique_entity.addObserver(red);
                        break;
                    case "blue":
                        unique_entity.addObserver(blue);
                        break;
                    case "green":
                        unique_entity.addObserver(green);
                        break;
                    default:
                        throw new ConfigurationParseException(
                                String.format("%s is not a valid color\n", obj.get("color")));
                }
            }
        }

        JSONObject heroConfig = (JSONObject) levelConfiguration.get("hero");
        double maxVelX = ((Number) levelConfiguration.get("maxHeroVelocityX")).doubleValue();

        Object hero = entityFactory.createEntity(this, heroConfig);
        if (!(hero instanceof DynamicEntity)) {
            throw new ConfigurationParseException("hero must be a dynamic entity");
        }
        DynamicEntity dynamicHero = (DynamicEntity) hero;
        Vector2D heroStartingPosition = dynamicHero.getPosition();
        this.hero = new ControllableDynamicEntity<>(dynamicHero, heroStartingPosition, maxVelX, floorHeight,
                levelGravity);
        this.entities.add(this.hero);

        JSONObject finishConfig = (JSONObject) levelConfiguration.get("finish");
        this.finish = entityFactory.createEntity(this, finishConfig);
        this.entities.add(finish);

        JSONObject squareCatConfig = (JSONObject) levelConfiguration.get("squarecat");
        squarecat = entityFactory.createEntity(this,squareCatConfig);
        this.entities.add(squarecat);
    }

    @Override
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    private List<DynamicEntity> getDynamicEntities() {
        return entities.stream().filter(e -> e instanceof DynamicEntity).map(e -> (DynamicEntity) e).collect(
                Collectors.toList());
    }

    private List<StaticEntity> getStaticEntities() {
        return entities.stream().filter(e -> e instanceof StaticEntity).map(e -> (StaticEntity) e).collect(
                Collectors.toList());
    }

    @Override
    public double getLevelHeight() {
        return this.levelHeight;
    }

    @Override
    public double getLevelWidth() {
        return this.levelWidth;
    }

    @Override
    public double getHeroHeight() {
        return hero.getHeight();
    }

    @Override
    public double getHeroWidth() {
        return hero.getWidth();
    }

    @Override
    public double getFloorHeight() {
        return floorHeight;
    }

    @Override
    public Color getFloorColor() {
        return floorColor;
    }

    @Override
    public double getGravity() {
        return levelGravity;
    }

    @Override
    public void update() {
        List<DynamicEntity> dynamicEntities = getDynamicEntities();

        dynamicEntities.stream().forEach(e -> {
            //sqaurecat does not affect by the gravity
            if (e == squarecat){
                e.update(frameDurationMilli, 0);
            }
            else{
                e.update(frameDurationMilli, levelGravity);
            }
        });

        for (int i = 0; i < dynamicEntities.size(); ++i) {
            DynamicEntity dynamicEntityA = dynamicEntities.get(i);

            for (int j = i + 1; j < dynamicEntities.size(); ++j) {
                DynamicEntity dynamicEntityB = dynamicEntities.get(j);

                if (dynamicEntityA.collidesWith(dynamicEntityB)) {
                    dynamicEntityA.collideWith(dynamicEntityB);
                    dynamicEntityB.collideWith(dynamicEntityA);
                    if (!isHero(dynamicEntityA) && !isHero(dynamicEntityB)) {
                        //sqaurecat does not affect by the collision of dynamic entity
                        if (dynamicEntityA != squarecat && dynamicEntityB != squarecat){
                            engine.resolveCollision(dynamicEntityA, dynamicEntityB);
                        }
                    }
                }
            }

            for (StaticEntity staticEntity : getStaticEntities()) {
                //sqaurecat does not affect by the collision of static entity
                if ( dynamicEntityA != squarecat && dynamicEntityA.collidesWith(staticEntity)) {
                    dynamicEntityA.collideWith(staticEntity);
                    engine.resolveCollision(dynamicEntityA, staticEntity, this);
                }
            }
        }

        dynamicEntities.stream().forEach(e -> engine.enforceWorldLimits(e, this));

        afterUpdateJobQueue.forEach(j -> j.run());
        afterUpdateJobQueue.clear();

    }

    @Override
    public double getHeroX() {
        return hero.getPosition().getX();
    }

    @Override
    public double getHeroY() {
        return hero.getPosition().getY();
    }

    @Override
    public boolean boostHeight() {
        return hero.boostHeight();
    }

    @Override
    public boolean dropHeight() {
        return hero.dropHeight();
    }

    @Override
    public boolean moveLeft() {
        return hero.moveLeft();
    }

    @Override
    public boolean moveRight() {
        return hero.moveRight();
    }

    @Override
    public boolean isHero(Entity entity) {
        return entity == hero;
    }

    @Override
    public boolean isFinish(Entity entity) {
        return this.finish == entity;
    }

    @Override
    public void resetHero() {
        afterUpdateJobQueue.add(() -> this.hero.reset());
    }

    @Override
    public void finish() {
        finished = true;
    }

    @Override
    public boolean isFinished(){
        return finished;
    }

    @Override
    public Entity getFinish(){
        return this.finish;
    }

    @Override
    public boolean isEnemy(Entity entity) {
        return enemies.contains(entity);
    }

    @Override
    public void removeEnemy(Entity entity) {
        entity.updateObservers();
        entities.remove(entity);
    }

    @Override
    public ControllableDynamicEntity<DynamicEntity> getHero(){
        return hero;
    }

    @Override
    public Entity getSquareCat(){
        return squarecat;
    }

    @Override
    public void addTotalObserver(Observer observer) {
        this.total = observer;
        for (Entity enemy : enemies){
            enemy.addObserver(this.total);
        }
    }

    @Override
    public Observer getRed() {
        return red;
    }

    @Override
    public Observer getGreen() {
        return green;
    }

    @Override
    public Observer getBlue() {
        return blue;
    }

    @Override
    public Observer getTotal() {
        return total;
    }

    @Override
    public Momento createMomento() {
        return new LevelMomento(this.copy());
    }

    @Override
    public Level copy() {
        return new LevelImpl(entities,
                engine,
                entityFactory,
                frameDurationMilli,
                hero,
                finish,
                levelHeight,
                levelWidth,
                levelGravity,
                floorHeight,
                floorColor,
                finished,
                enemies,
                squarecat,
                red,
                green,
                blue,
                total);
    }
}
