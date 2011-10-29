package com.alunev.ants.logic;

import com.alunev.ants.Ants;

public class FoodEstimator implements PathEstimator {
    private Ants ants;

    public FoodEstimator(Ants ants) {
        this.ants = ants;
    }

    public boolean gotCloseEnough(WeightedTile tile, WeightedTile goal) {
        return ants.getDistance(tile.getTile(), goal.getTile()) <= 1;
    }
}
