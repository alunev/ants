package com.alunev.ants.logic.pathfind;

import java.util.List;

import com.alunev.ants.mechanics.Tile;

public class PathSpec {
    private Tile goal;
    List<Tile> path;

    public PathSpec(Tile goal, List<Tile> path) {
        this.goal = goal;
        this.path = path;
    }

    public Tile getGoal() {
        return goal;
    }

    public List<Tile> getPath() {
        return path;
    }
}
