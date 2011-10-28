package com.alunev.ants.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.alunev.ants.Ants;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Tile;

public class PathFinder {
    private Ants ants;
    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private WeightedTile start;
    private WeightedTile goal;

    private Set<WeightedTile> evaluatedNodes = new HashSet<WeightedTile>();
    private SortedSet<WeightedTile> nodesToEvaluate = new TreeSet<WeightedTile>();
    private Map<WeightedTile, WeightedTile> cameFrom = new HashMap<WeightedTile, WeightedTile>();
    private Map<WeightedTile, WeightedTile> childToParent = new HashMap<WeightedTile, WeightedTile>();

    private Map<WeightedTile, Integer> gScores = new HashMap<WeightedTile, Integer>();

    public PathFinder(Ants ants, Tile start, Tile goal) {
        this.ants = ants;
        this.start = new WeightedTile(start, 0);
        this.goal = new WeightedTile(goal, 0);
    }

    public List<Tile> getAStarPath() {
        gScores.put(start, 0);

        start.setWeight(fScore(start));
        nodesToEvaluate.add(start);
        while (!nodesToEvaluate.isEmpty()) {
            WeightedTile x = nodesToEvaluate.first();

            // we can't step on food, so check if we are near it already
            if (gotCloseEnoughToFood(x)) {
                return buildPathFormStartToGoal(x);
            }

            nodesToEvaluate.remove(x);
            evaluatedNodes.add(x);

            for (WeightedTile neighbor : getNeighbors(x)) {
                if (evaluatedNodes.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = gScore(neighbor);

                boolean tentativeIsBetter = false;
                if (!nodesToEvaluate.contains(neighbor)) {
                    neighbor.setWeight(fScore(neighbor));
                    nodesToEvaluate.add(neighbor);
                    tentativeIsBetter = true;
                } else if (tentativeGScore < gScores.get(neighbor)) {
                    tentativeIsBetter = true;
                }

                if (tentativeIsBetter) {
                    gScores.put(neighbor, tentativeGScore);

                    neighbor.setWeight(fScore(neighbor));
                    // cameFrom.removeByKey(neighbor);
                    cameFrom.put(neighbor, x);
                }
            }
        }

        return Collections.emptyList();
    }

    private boolean gotCloseEnoughToFood(WeightedTile tile) {
        return ants.getDistance(tile.getTile(), goal.getTile()) <= 1;
    }

    private List<Tile> buildPathFormStartToGoal(WeightedTile current) {
        List<Tile> path = new ArrayList<Tile>();

        if (cameFrom.get(current) != null) {
            path.addAll(buildPathFormStartToGoal(cameFrom.get(current)));
        }

        path.add(current.getTile());

        return path;
    }

    private int gScore(WeightedTile tile) {
        if (tile.equals(start)) {
            return 0;
        }

        return gScores.get(childToParent.get(tile)) + 1;
    }

    private int hScore(WeightedTile tile) {
        return hFunction(tile, goal);
    }

    private int fScore(WeightedTile tile) {
        return gScore(tile) + hScore(tile);
    }

    private int hFunction(WeightedTile tile, WeightedTile goal) {
        return ants.getDistance(tile.getTile(), goal.getTile());
    }

    private Set<WeightedTile> getNeighbors(WeightedTile tile) {
        Set<WeightedTile> neighbors = new HashSet<WeightedTile>();

        WeightedTile potentialNeighbor;
        for (Direction direction : Direction.values()) {
            if (direction != Direction.NONE) {
                potentialNeighbor = new WeightedTile(ants.getTile(tile.getTile(), direction), 0);

                if (ants.getTyleType(potentialNeighbor.getTile()).isUnoccupied()
                        && !reservedTiles.contains(potentialNeighbor.getTile())) {
                    neighbors.add(potentialNeighbor);
                    childToParent.put(potentialNeighbor, tile);
                }
            }
        }

        return neighbors;
    }
}
