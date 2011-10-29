package com.alunev.ants.logic;

public class HillEstimator implements PathEstimator {
    @Override
    public boolean gotCloseEnough(WeightedTile tile, WeightedTile goal) {
        return tile.equals(goal);
    }
}
