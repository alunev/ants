package com.alunev.ants.mechanics;
/**
 * Represents an order to be issued.
 */
public class Order {
    private final int row;

    private final int col;

    private final char direction;

    /**
     * Creates new {@link Order} object.
     *
     * @param tile map tile with my ant
     * @param direction direction in which to move my ant
     */
    public Order(Tile tile, Aim direction) {
        row = tile.getRow();
        col = tile.getCol();
        this.direction = direction.getSymbol();
    }


    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + col;
        result = prime * result + direction;
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
        Order other = (Order) obj;
        if (col != other.col)
            return false;
        if (direction != other.direction)
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
        return "o " + row + " " + col + " " + direction;
    }
}
