package com.alunev.ants.logic;

import com.alunev.ants.mechanics.Tile;

public class WeightedTile implements Comparable<WeightedTile>{
    private Tile tile;
    private int weight;

    public Tile getTile() {
        return tile;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public WeightedTile(Tile tile, int weight) {
        this.tile = tile;
        this.weight = weight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tile == null) ? 0 : tile.hashCode());
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
        WeightedTile other = (WeightedTile) obj;
        if (tile == null) {
            if (other.tile != null)
                return false;
        } else if (!tile.equals(other.tile))
            return false;
        return true;
    }

    @Override
    public int compareTo(WeightedTile other) {
        return this.weight - other.weight;
    }

    @Override
    public String toString() {
        return "WeightedTile [tile=" + tile + ", weight=" + weight + "]";
    }
}
