package com.alunev.ants.logic;

import java.util.LinkedList;
import java.util.List;

import com.alunev.ants.mechanics.Tile;

public class PathRoute {
    List<Tile> path = new LinkedList<Tile>();

    public PathRoute() {

    }

    public PathRoute(List<Tile> path) {
        this.path = path;
    }

    public Tile getStart() {
        return path.get(0);
    }

    public Tile getNext(Tile previous) {
        return path.get(path.indexOf(previous) + 1);
    }
}
