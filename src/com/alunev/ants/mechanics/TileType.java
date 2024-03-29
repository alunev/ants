package com.alunev.ants.mechanics;
/**
 * Represents type of tile on the game map.
 */
public enum TileType {
    /** Food tile. */
    FOOD,

    /** Water tile. */
    WATER,

    /** Water tile. */
    UNKNOWN,

    /** Land tile. */
    LAND,

    /** Dead ant tile. */
    DEAD,

    /** My ant tile. */
    MY_ANT,

    /** Enemy ant tile. */
    ENEMY_ANT;

    /**
     * Checks if this type of tile is passable, which means it is not a water tile.
     *
     * @return <code>true</code> if this is not a water tile, <code>false</code> otherwise
     */
    public boolean isPassable() {
        return ordinal() > WATER.ordinal();
    }

    /**
     * Checks if this type of tile is unoccupied, which means it is a land tile or a dead ant tile.
     *
     * @return <code>true</code> if this is a land tile or a dead ant tile, <code>false</code>
     *         otherwise
     */
    public boolean isUnoccupied() {
        return this == LAND || this == DEAD;
    }

    public String toString() {
        return this.name();
    }
}
