package ballboy.model;

import ballboy.model.entities.observer.Observer;
import ballboy.model.entities.observer.ScoreObserver;
import ballboy.save.Momento;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the GameEngine interface.
 * This provides a common interface for the entire game.
 */
public class GameEngineImpl implements GameEngine {
    private List<Level> level;
    private List<Level> level_backup = new ArrayList<>();
    private Level currentLevel;
    private Integer levelIndex;
    private Observer totalScore;
    private Momento save;
    private int savedLevelNumber;
    private Observer savedTotal;

    public GameEngineImpl(List<Level> level, Integer levelIndex) {
        this.level = level;
        for (Level le : level){
            level_backup.add(le.copy());
        }
        this.levelIndex = levelIndex;
        currentLevel = level.get(levelIndex);
        this.totalScore = new ScoreObserver();
        currentLevel.addTotalObserver(this.totalScore);
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    //level preparation and transition
    public void startLevel() {
        levelIndex += 1;
        if (levelIndex >= level.size()){
            Platform.exit();
            return;
        }
        currentLevel = level.get(levelIndex);
        currentLevel.addTotalObserver(totalScore);
        return;
    }

    public boolean boostHeight() {
        return currentLevel.boostHeight();
    }

    public boolean dropHeight() {
        return currentLevel.dropHeight();
    }

    public boolean moveLeft() {
        return currentLevel.moveLeft();
    }

    public boolean moveRight() {
        return currentLevel.moveRight();
    }

    public void tick() {
        //transit the level if the current level is finished
        if (currentLevel.isFinished()){
            startLevel();
        }
        currentLevel.update();
    }

    public int getTotalScore() {
        return totalScore.getValue();
    }

    @Override
    public void createSave() {
        this.save = currentLevel.createMomento();
        this.savedLevelNumber = level.indexOf(currentLevel);
        this.savedTotal = save.load().getTotal();
    }

    @Override
    public void load() {
        List<Level> newLevels = new ArrayList<>();
        for (int levelNum = 0; levelNum < level.size(); levelNum++){
            if (levelNum == savedLevelNumber){
                newLevels.add(save.load().copy());
                continue;
            }
            else if(levelNum > savedLevelNumber){
                newLevels.add(level_backup.get(levelNum));
                continue;
            }
            newLevels.add(level.get(levelNum));
        }
        totalScore = savedTotal.copy();
        level = new ArrayList<>(newLevels);
        levelIndex = savedLevelNumber;
        currentLevel = level.get(levelIndex);
    }
}