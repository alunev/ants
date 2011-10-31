package com.alunev.ants.logic.pathfind;

import com.alunev.ants.Ants;
import com.alunev.ants.mechanics.Tile;

public class FoodEstimator implements PathEstimator {
    private Ants ants;

    public FoodEstimator(Ants ants) {
        this.ants = ants;
    }

    public boolean gotCloseEnough(Tile tile, Tile goal) {
        return ants.getDistance(tile, goal) <= 1;
    }
}
