package com.alunev.ants.io;

public class GameSetup {
    private final int loadTime;

    private final int turnTime;

    private final int rows;

    private final int cols;

    private final int turns;

    private final int viewRadius2;

    private final int attackRadius2;

    private final int spawnRadius2;

    public GameSetup(int loadTime, int turnTime, int rows, int cols,
            int turns, int viewRadius2, int attackRadius2,
            int spawnRadius2) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.rows = rows;
        this.cols = cols;
        this.turns = turns;
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        this.spawnRadius2 = spawnRadius2;
    }

    public int getLoadTime() {
        return loadTime;
    }

    public int getTurnTime() {
        return turnTime;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getTurns() {
        return turns;
    }

    public int getViewRadius2() {
        return viewRadius2;
    }

    public int getAttackRadius2() {
        return attackRadius2;
    }

    public int getSpawnRadius2() {
        return spawnRadius2;
    }
}
