package com.alunev.ants.mechanics;

public enum DiagDirection {
    /** North direction, or up. */
    NORTH_EAST(1, -1),

    /** East direction or right. */
    SOUTH_EAST(1, 1),

    /** South direction or down. */
    SOUTH_WEST(-1, 1),

    /** West direction or left. */
    NORTH_WEST(-1, -1);

    private final int rowDelta;
    private final int colDelta;

    DiagDirection(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    public int getRowDelta() {
        return rowDelta;
    }

    public int getColDelta() {
        return colDelta;
    }
}
