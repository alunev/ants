package com.alunev.ants.logic.pathfind;

import com.alunev.ants.mechanics.Tile;

public class HillEstimator implements PathEstimator {
    @Override
    public boolean gotCloseEnough(Tile tile, Tile goal) {
        return tile.equals(goal);
    }
}
