package com.alunev.ants.mechanics;
/**
 * Represents an order to be issued.
 */
public class Order {
    private final Tile tile;
    private final Direction direction;

    /**
     * Creates new {@link Order} object.
     *
     * @param tile map tile with my ant
     * @param direction direction in which to move my ant
     */
    public Order(Tile tile, Direction direction) {
        this.tile = tile;
        this.direction = direction;
    }

    public Tile getTile() {
        return this.tile;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return direction == order.direction && !(tile != null ? !tile.equals(order.tile) : order.tile != null);
    }

    @Override
    public int hashCode() {
        int result = tile != null ? tile.hashCode() : 0;
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Order{" +
                "tile=" + tile +
                ", direction=" + direction +
                '}';
    }
}
