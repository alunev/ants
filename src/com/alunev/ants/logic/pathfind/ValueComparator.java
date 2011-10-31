package com.alunev.ants.logic.pathfind;

import java.util.Comparator;
import java.util.Map;

import com.alunev.ants.mechanics.Tile;

public class ValueComparator implements Comparator<Tile> {
    private Map<Tile, Integer> base;

    public ValueComparator(Map base) {
        this.base = base;
    }

    @Override
    public int compare(Tile o1, Tile o2) {
        if(base.get(o1) > base.get(o2)) {
            return 1;
        } else if(base.get(o1) == base.get(o2)) {
            return 0;
        } else {
            return -1;
        }
    }
}