package com.alunev.ants.logic;

import com.alunev.ants.Ants;

public class RouteWithWeight implements Comparable<RouteWithWeight>{
    private Route route;
    private int weight;

    public RouteWithWeight(Ants ants, Route route) {
        this.route = route;
        this.weight = ants.getDistance(route.getStart(), route.getEnd());
    }

    public Route getRoute() {
        return route;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((route == null) ? 0 : route.hashCode());
        result = prime * result + weight;
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
        RouteWithWeight other = (RouteWithWeight) obj;
        if (route == null) {
            if (other.route != null)
                return false;
        } else if (!route.equals(other.route))
            return false;
        if (weight != other.weight)
            return false;
        return true;
    }

    @Override
    public int compareTo(RouteWithWeight o) {
        return this.weight - o.weight;
    }
}
