package com.alunev.ants.logic.pathfind;

import com.alunev.ants.calculation.CalcState;
import com.alunev.ants.mechanics.Tile;

public class FoodEstimator implements PathEstimator {

    private final CalcState calcState;

    public FoodEstimator(CalcState calcState) {
        this.calcState = calcState;
    }

    public boolean gotCloseEnough(Tile tile, Tile goal) {
        return calcState.getDistance(tile, goal) <= 1;
    }
}
