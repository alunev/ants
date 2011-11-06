package com.alunev.ants.io;

import java.util.HashSet;
import java.util.Set;

import com.alunev.ants.calculation.MapUtils;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class GameStateBuilder {
    private final TileType map[][];

    private final Set<Tile> myAnts = new HashSet<Tile>();

    private final Set<Tile> enemyAnts = new HashSet<Tile>();

    private final Set<Tile> myHills = new HashSet<Tile>();

    private final Set<Tile> enemyHills = new HashSet<Tile>();

    private final Set<Tile> foodTiles = new HashSet<Tile>();

    private final GameSetup gameSetup;

    public GameStateBuilder(GameSetup gameSetup) {
        this.gameSetup = gameSetup;

        this.map = new TileType[gameSetup.getRows()][gameSetup.getCols()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = TileType.UNKNOWN;
            }
        }
    }

    public GameState build() {
        // mark all visible tiles around ant as land if it was not marked otherwise
        MapUtils mapUtils = new MapUtils(gameSetup);
        for (int i = 0;i < map.length;i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == TileType.UNKNOWN) {
                    if (mapUtils.isVisible(new Tile(i,j), myAnts, gameSetup.getViewRadius2())) {
                        map[i][j] = TileType.LAND;
                    }
                }
            }
        }


        GameState gameState = new GameState(map, myAnts, enemyAnts, myHills, enemyHills, foodTiles);
        return gameState;
    }

    public void addWater(int row, int col) {
        update(TileType.WATER, new Tile(row, col));
    }

    public void addAnt(int row, int col, int owner) {
        update(owner > 0 ? TileType.ENEMY_ANT : TileType.MY_ANT, new Tile(row, col));
    }

    public void addFood(int row, int col) {
        update(TileType.FOOD, new Tile(row, col));
    }

    public void removeAnt(int row, int col, int owner) {
        update(TileType.DEAD, new Tile(row, col));
    }

    public void addHill(int row, int col, int owner) {
        updateHills(owner, new Tile(row, col));
    }

    public void update(TileType ilk, Tile tile) {
        map[tile.getRow()][tile.getCol()] = ilk;
        switch (ilk) {
            case FOOD:
                foodTiles.add(tile);
            break;
            case MY_ANT:
                myAnts.add(tile);
            break;
            case ENEMY_ANT:
                enemyAnts.add(tile);
            break;
        }
    }

    public void updateHills(int owner, Tile tile) {
        if (owner > 0) {
            enemyHills.add(tile);
        } else {
            myHills.add(tile);
        }
    }
}
