package application.logic;

public class GameEngine {

    private int lapsCompleted = 0;

    public void incrementLap() {
        lapsCompleted++;
    }

    public int getLapsCompleted() {
        return lapsCompleted;
    }
}
