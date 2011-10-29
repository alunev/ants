package com.alunev.ants.logic;

public interface PathEstimator {

    boolean gotCloseEnough(WeightedTile tile, WeightedTile goal);

}
