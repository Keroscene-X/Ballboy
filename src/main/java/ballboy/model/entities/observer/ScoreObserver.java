package ballboy.model.entities.observer;

public class ScoreObserver implements Observer {
    private int score = 0;

    public ScoreObserver(){}

    public ScoreObserver(int score){
        this.score = score;
    }

    @Override
    public void update() {
        this.score += 1;
    }

    public int getValue() {
        return this.score;
    }

    @Override
    public Observer copy() {
        return new ScoreObserver(score);
    }
}
