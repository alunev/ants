package com.alunev.ants.model;

import java.util.List;

import com.alunev.ants.mechanics.Tile;

public class Ant {
    private Tile tile;
    private List<Tile> currentPath;

    public Ant(Tile tile, List<Tile> currentPath) {
        this.tile = tile;
        this.currentPath = currentPath;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public List<Tile> getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(List<Tile> currentPath) {
        this.currentPath = currentPath;
    }
}
