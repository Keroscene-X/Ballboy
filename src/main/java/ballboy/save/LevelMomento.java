package ballboy.save;

import ballboy.model.Level;

public class LevelMomento implements Momento{
    private Level level;

    public LevelMomento(Level level){
        this.level = level;
    }

    @Override
    public Level load() {
        return level;
    }
}
