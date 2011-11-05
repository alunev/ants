package com.alunev.ants.io;

import java.util.Set;

import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class GameState {
    private final TileType map[][];

    private final Set<Tile> myAnts;

    private final Set<Tile> enemyAnts;

    private final Set<Tile> myHills;

    private final Set<Tile> enemyHills;

    private final Set<Tile> foodTiles;

    public GameState(TileType[][] map, Set<Tile> myAnts, Set<Tile> enemyAnts, Set<Tile> myHills,
            Set<Tile> enemyHills, Set<Tile> foodTiles) {
        this.map = map;
        this.myAnts = myAnts;
        this.enemyAnts = enemyAnts;
        this.myHills = myHills;
        this.enemyHills = enemyHills;
        this.foodTiles = foodTiles;
    }

    public Set<Tile> getMyAnts() {
        return myAnts;
    }

    public Set<Tile> getEnemyAnts() {
        return enemyAnts;
    }

    public Set<Tile> getMyHills() {
        return myHills;
    }

    public Set<Tile> getEnemyHills() {
        return enemyHills;
    }

    public Set<Tile> getFoodTiles() {
        return foodTiles;
    }

    public TileType[][] getMap() {
        return map;
    }
}
