package com.alunev.ants.logic;

import com.alunev.ants.mechanics.Tile;

public class LinearRoute {
    private final Tile start;
    private final Tile end;

    public LinearRoute(Tile start, Tile end) {
        this.start = start;
        this.end = end;
    }

    public Tile getStart() {
        return start;
    }

    public Tile getEnd() {
        return end;
    }


    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());

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
        LinearRoute other = (LinearRoute) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Route [start=" + start + ", end=" + end + "]";
    }
}
