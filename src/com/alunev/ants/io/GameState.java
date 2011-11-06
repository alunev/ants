package com.alunev.ants.io;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class GameState {
    private TileType map[][];

    private Set<Tile> myAnts;

    private Set<Tile> enemyAnts;

    private Set<Tile> myHills;

    private Set<Tile> enemyHills;

    private Set<Tile> foodTiles;

    public GameState(int rows, int cols) {
        this.map = new TileType[rows][cols];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = TileType.UNKNOWN;
            }
        }

        this.myAnts = new HashSet<Tile>();
        this.enemyAnts = new HashSet<Tile>();
        this.myHills = new HashSet<Tile>();
        this.enemyHills = new HashSet<Tile>();
        this.foodTiles = new HashSet<Tile>();
    }

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

    public GameState merge(GameState gameState) {
        // clear all ants from old version of map
        // copy all visible things on new map to old one
        for (int i = 0;i < map.length; i++) {
            for (int j = 0;j < map[0].length;j++) {
                if (map[i][j] == TileType.MY_ANT) {
                    map[i][j] = TileType.LAND;
                }
                if (gameState.getMap()[i][j] != TileType.UNKNOWN) {
                    this.map[i][j] = gameState.getMap()[i][j];
                }
            }
        }

        this.myAnts = gameState.getMyAnts();
        this.enemyAnts = gameState.getEnemyAnts();
        this.myHills = gameState.getMyHills();
        this.enemyHills.addAll(gameState.getEnemyHills());

        Set<Tile> remainingFoodTiles = new HashSet<Tile>();
        remainingFoodTiles.addAll(foodTiles);
        for (Tile tile : foodTiles) {
            if (gameState.getMap()[tile.getRow()][tile.getCol()] != TileType.FOOD
                    && gameState.getMap()[tile.getRow()][tile.getCol()] != TileType.UNKNOWN) {
                remainingFoodTiles.remove(tile);
            }
        }
        this.foodTiles = remainingFoodTiles;
        this.foodTiles.addAll(gameState.getFoodTiles());

        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((enemyAnts == null) ? 0 : enemyAnts.hashCode());
        result = prime * result + ((enemyHills == null) ? 0 : enemyHills.hashCode());
        result = prime * result + ((foodTiles == null) ? 0 : foodTiles.hashCode());
        result = prime * result + Arrays.hashCode(map);
        result = prime * result + ((myAnts == null) ? 0 : myAnts.hashCode());
        result = prime * result + ((myHills == null) ? 0 : myHills.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GameState other = (GameState) obj;
        if (enemyAnts == null) {
            if (other.enemyAnts != null)
                return false;
        } else if (!enemyAnts.equals(other.enemyAnts))
            return false;
        if (enemyHills == null) {
            if (other.enemyHills != null)
                return false;
        } else if (!enemyHills.equals(other.enemyHills))
            return false;
        if (foodTiles == null) {
            if (other.foodTiles != null)
                return false;
        } else if (!foodTiles.equals(other.foodTiles))
            return false;
        for (int i = 0;i < map.length;i++) {
            if (!Arrays.equals(map[i], other.map[i]))
                return false;
        }
        if (myAnts == null) {
            if (other.myAnts != null)
                return false;
        } else if (!myAnts.equals(other.myAnts))
            return false;
        if (myHills == null) {
            if (other.myHills != null)
                return false;
        } else if (!myHills.equals(other.myHills))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GameState [map=" + Arrays.toString(map) + ", myAnts=" + myAnts + ", enemyAnts=" + enemyAnts
                + ", myHills=" + myHills + ", enemyHills=" + enemyHills + ", foodTiles=" + foodTiles + "]";
    }
}
