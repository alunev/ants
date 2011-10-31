package com.alunev.ants.logic.pathfind;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alunev.ants.Ants;
import com.alunev.ants.logic.TurnTimer;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Tile;

public class PathFinder {
    private Ants ants;
    private PathEstimator pathEstimator;

    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private Tile start;
    private List<Tile> goals;

    private Set<Tile> evaluatedNodes = new HashSet<Tile>();
    private Map<Tile, Integer> nodesToEvaluate = new HashMap<Tile, Integer>();
    private Map<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();
    private Map<Tile, Tile> childToParent = new HashMap<Tile, Tile>();

    private Map<Tile, Integer> gScores = new HashMap<Tile, Integer>();

    public PathFinder(Ants ants, Tile start, List<Tile> goals, PathEstimator pathEstimator) {
        this.ants = ants;
        this.start = start;

        this.goals = goals;

        this.pathEstimator = pathEstimator;
    }

    public PathSpec getAStarPath(TurnTimer turnTimer) {
        List<Tile> currentPath;
        PathSpec shortestPathSpec = null;

        for (Tile goal : goals) {
            if (turnTimer.giveUp()) {
                break;
            }

            currentPath = getAStarPathForGoal(turnTimer, goal);
            if (shortestPathSpec == null || currentPath.size() < shortestPathSpec.getPath().size()) {
                shortestPathSpec = new PathSpec(goal, currentPath);
            }
        }

        return shortestPathSpec;
    }

    public List<Tile> getAStarPathForGoal(TurnTimer turnTimer, Tile goal) {
        gScores.put(start, 0);

        nodesToEvaluate.put(start, fScore(start, goal));
        while (!nodesToEvaluate.isEmpty()) {
            // sort according to weights and get lowest
            SortedMap<Tile, Integer> sortedMap = new TreeMap<Tile, Integer>(new ValueComparator(nodesToEvaluate));
            sortedMap.putAll(nodesToEvaluate);
            Tile x = sortedMap.firstKey();

            // we can't step on food, so check if we are near it already
            if (pathEstimator.gotCloseEnough(x, goal) || turnTimer.giveUp()) {
                return buildPathFormStartToHere(x);
            }

            nodesToEvaluate.remove(x);
            evaluatedNodes.add(x);

            for (Tile neighbor : getNeighbors(x)) {
                if (evaluatedNodes.contains(neighbor)) {
                    continue;
                }

                int tentativeGScore = gScore(neighbor);

                boolean tentativeIsBetter = false;
                if (!nodesToEvaluate.containsKey(neighbor)) {
                    nodesToEvaluate.put(neighbor, fScore(neighbor, goal));
                    tentativeIsBetter = true;
                } else if (tentativeGScore < gScores.get(neighbor)) {
                    tentativeIsBetter = true;
                }

                if (tentativeIsBetter) {
                    gScores.put(neighbor, tentativeGScore);
                    nodesToEvaluate.put(neighbor, fScore(neighbor, goal));
                    cameFrom.put(neighbor, x);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Tile> buildPathFormStartToHere(Tile current) {
        List<Tile> path = new ArrayList<Tile>();

        if (cameFrom.get(current) != null) {
            path.addAll(buildPathFormStartToHere(cameFrom.get(current)));
        }

        path.add(current);

        return path;
    }

    private int gScore(Tile tile) {
        if (tile.equals(start)) {
            return 0;
        }

        return gScores.get(childToParent.get(tile)) + 1;
    }

    private int hScore(Tile tile, Tile goal) {
        return hFunction(tile, goal);
    }

    private int fScore(Tile tile, Tile goal) {
        return gScore(tile) + hScore(tile, goal);
    }

    private int hFunction(Tile tile, Tile goal) {
        return ants.getDistance(tile, goal);
    }

    private Set<Tile> getNeighbors(Tile tile) {
        Set<Tile> neighbors = new HashSet<Tile>();

        Tile potentialNeighbor;
        for (Direction direction : Direction.values()) {
            if (direction != Direction.NONE) {
                potentialNeighbor = ants.getTile(tile, direction);

                if (ants.getTyleType(potentialNeighbor).isUnoccupied()
                        && !reservedTiles.contains(potentialNeighbor)) {
                    neighbors.add(potentialNeighbor);
                    childToParent.put(potentialNeighbor, tile);
                }
            }
        }

        return neighbors;
    }
}
