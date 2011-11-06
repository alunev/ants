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

    private final int playerSeed;

    public GameSetup(int loadTime, int turnTime, int rows, int cols,
            int turns, int viewRadius2, int attackRadius2,
            int spawnRadius2, int playerSeed) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.rows = rows;
        this.cols = cols;
        this.turns = turns;
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        this.spawnRadius2 = spawnRadius2;
        this.playerSeed = playerSeed;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + attackRadius2;
        result = prime * result + cols;
        result = prime * result + loadTime;
        result = prime * result + playerSeed;
        result = prime * result + rows;
        result = prime * result + spawnRadius2;
        result = prime * result + turnTime;
        result = prime * result + turns;
        result = prime * result + viewRadius2;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        GameSetup other = (GameSetup) obj;
        if (attackRadius2 != other.attackRadius2)
            return false;
        if (cols != other.cols)
            return false;
        if (loadTime != other.loadTime)
            return false;
        if (playerSeed != other.playerSeed)
            return false;
        if (rows != other.rows)
            return false;
        if (spawnRadius2 != other.spawnRadius2)
            return false;
        if (turnTime != other.turnTime)
            return false;
        if (turns != other.turns)
            return false;
        if (viewRadius2 != other.viewRadius2)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GameSetup [loadTime=" + loadTime + ", turnTime=" + turnTime + ", rows=" + rows + ", cols=" + cols
                + ", turns=" + turns + ", viewRadius2=" + viewRadius2 + ", attackRadius2=" + attackRadius2
                + ", spawnRadius2=" + spawnRadius2 + ", playerSeed=" + playerSeed + "]";
    }
}
