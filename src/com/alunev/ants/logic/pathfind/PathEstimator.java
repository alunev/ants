package com.alunev.ants.logic.pathfind;

import com.alunev.ants.mechanics.Tile;

public interface PathEstimator {

    boolean gotCloseEnough(Tile tile, Tile goal);

}
