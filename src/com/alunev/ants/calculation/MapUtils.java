package com.alunev.ants.calculation;

import java.util.Set;

import com.alunev.ants.io.GameSetup;
import com.alunev.ants.mechanics.Tile;

public class MapUtils {

    private final GameSetup gameSetup;

    public MapUtils(GameSetup gameSetup) {
        this.gameSetup = gameSetup;

    }

    public boolean isVisible(Tile tile, Set<Tile> myAnts, int viewRadius2) {
        boolean visible = false;
        for (Tile myAnt : myAnts) {
            if (isVisibleForAnt(myAnt, tile)) {
                visible = true;
                break;
            }
        }

        return visible;
    }

    public boolean isVisibleForAnt(Tile myAnt, Tile tile) {
        return getDistance(myAnt, tile) <=  gameSetup.getViewRadius2();
    }

    public int getDistance(Tile t1, Tile t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());

        rowDelta = Math.min(rowDelta, gameSetup.getRows() - rowDelta);
        colDelta = Math.min(colDelta, gameSetup.getCols() - colDelta);

        return rowDelta * rowDelta + colDelta * colDelta;
    }
}
