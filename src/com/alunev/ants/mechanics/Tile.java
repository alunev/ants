package com.alunev.ants.mechanics;

/**
 * Represents a tile of the game map.
 */
public class Tile {
    private final int row;

    private final int col;

    /**
     * Creates new {@link Tile} object.
     *
     * @param row row index
     * @param col column index
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns row index.
     *
     * @return row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns column index.
     *
     * @return column index
     */
    public int getCol() {
        return col;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + col;
        result = prime * result + row;
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
        Tile other = (Tile) obj;
        if (col != other.col)
            return false;
        if (row != other.row)
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return row + " " + col;
    }
}
