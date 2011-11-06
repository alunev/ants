package com.alunev.ants.bot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alunev.ants.calculation.CalcState;
import com.alunev.ants.calculation.MapUtils;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.logic.LinearRoute;
import com.alunev.ants.logic.pathfind.FoodEstimator;
import com.alunev.ants.logic.pathfind.HillEstimator;
import com.alunev.ants.logic.pathfind.PathFinder;
import com.alunev.ants.logic.pathfind.PathSpec;
import com.alunev.ants.logic.pathfind.ValueComparator;
import com.alunev.ants.mechanics.DiagDirection;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class AstarBot implements Bot {
    private CalcState calcState;

    private static final int UNSEEN_TILES_TO_ANALYZE = 1;

    private int hillAttackRadius;

    private List<Order> orders = new ArrayList<Order>();

    public AstarBot() {
    }

    @Override
    public void beforeStart(GameSetup gameSetup) {
        this.calcState = new CalcState(gameSetup, null);
        this.hillAttackRadius = gameSetup.getViewRadius2() * 2;
    }

    @Override
    public void beforeUpdate(GameState gameState) {
        calcState.update(gameState);
    }

    @Override
    public List<Order> doTurn() {
        orders.clear();

        unblockOwnHill();

        setupDefense();

        lookAndMoveForFood();

        attackHills();

        exploreMapMoves();

        doRandomMoveAfterAll();

        return orders;
    }

    @Override
    public void afterUpdate() {

    }

    private void doRandomMoveAfterAll() {
        if (calcState.giveUp()) {
            return;
        }

        Random random = new Random(System.currentTimeMillis());
        for (Tile tile : calcState.getFreeToMoveAnts(orders)) {
            if (calcState.giveUp()) {
                return;
            }

            boolean moved = false;
            int tries = 0;
            Order order = null;
            while (!moved && tries++ < 10) {
                Direction direction = Direction.values()[random.nextInt(4)];

                if ((order = calcState.doMoveInDirection(tile, direction)) != null) {
                    moved = true;
                }

                tries++;
            }
        }
    }

    private void attackHills() {
        if (calcState.giveUp()) {
            return;
        }

        MapUtils mapUtils = new MapUtils(calcState.getGameSetup());

        // form list of hills for each ant
        Map<Tile, List<Tile>> antToGoals = new HashMap<Tile, List<Tile>>();
        for (Tile enemyHill : calcState.getSeenEnemyHills()) {
            if (calcState.giveUp()) {
                return;
            }

            for (Tile myAnt : calcState.getFreeToMoveAnts(orders)) {
                if (mapUtils.getDistance(myAnt, enemyHill) < hillAttackRadius) {
                    if (!antToGoals.containsKey(myAnt)) {
                        antToGoals.put(myAnt, new ArrayList<Tile>());
                    }

                    antToGoals.get(myAnt).add(enemyHill);
                }
            }
        }

        for (Map.Entry<Tile, List<Tile>> entry: antToGoals.entrySet()) {
            Tile myAnt = entry.getKey();
            List<Tile> goals = entry.getValue();

            // filter routes so that they don't overlap with other ants/destinations
            goals = calcState.filterGoals(goals);
            if (goals.isEmpty()) {
                continue;
            }

            if (calcState.giveUp()) {
                return;
            }

            PathSpec pathSpec = new PathFinder(calcState,
                    myAnt, goals,
                    new HillEstimator()).getAStarPath();
            if (pathSpec.getPath().size() > 1) {
                orders.add(calcState.doMoveToLocation(myAnt, pathSpec.getPath().get(1)));
                calcState.getTargetTiles().add(pathSpec.getGoal());
            }
        }
    }

    private void exploreMapMoves() {
        if (calcState.getUnseenTiles().size() == 0 || calcState.giveUp()) {
            return;
        }

        MapUtils mapUtils = new MapUtils(calcState.getGameSetup());

        for (Tile myAnt : calcState.getFreeToMoveAnts(orders)) {
            if (calcState.hasOrderForTile(myAnt, orders)) {
                continue;
            }

            if (calcState.giveUp()) {
                return;
            }

            // find closest unseen tiles
            SortedMap<Integer, LinearRoute> routesForAnt = new TreeMap<Integer, LinearRoute>();
            for (Tile unseenTile : calcState.getUnseenTiles()) {
                if (calcState.getTyleType(unseenTile).isUnoccupied()) {
                    routesForAnt.put(mapUtils.getDistance(myAnt, unseenTile),
                            new LinearRoute(myAnt, unseenTile));
                }
            }

            // form list of goals(we can't afford computing astar for each unseen tile)
            List<Tile> goals = new ArrayList<Tile>();
            for (LinearRoute route : routesForAnt.values()) {
                if (goals.size() >= UNSEEN_TILES_TO_ANALYZE) {
                    break;
                }

                goals.add(route.getEnd());
            }

            goals = calcState.filterGoals(goals);
            if (goals.isEmpty()) {
                continue;
            }

            PathSpec pathSpec = new PathFinder(calcState, myAnt, goals,
                    new HillEstimator()).getAStarPath();
            if (pathSpec.getPath().size() > 1 && !calcState.isResered(pathSpec.getPath().get(1))) {
                orders.add(calcState.doMoveToLocation(myAnt, pathSpec.getPath().get(1)));
                calcState.addTarget(pathSpec.getGoal());
            }
        }
    }

    private void lookAndMoveForFood() {
        if (calcState.giveUp()) {
            return;
        }

        MapUtils mapUtils = new MapUtils(calcState.getGameSetup());

        // find close food
        Map<Tile, List<Tile>> antToGoals = new HashMap<Tile, List<Tile>>();
        for (Tile foodLoc : calcState.getSeenFood()) {
            if (calcState.giveUp()) {
                return;
            }

            Map<Tile, Integer> antToDistance = new HashMap<Tile, Integer>();
            for (Tile myAnt : calcState.getFreeToMoveAnts(orders)) {
                antToDistance.put(myAnt, mapUtils.getDistance(myAnt, foodLoc));
            }

            if (!antToDistance.isEmpty()) {
                SortedMap<Tile, Integer> sortedAntToDistance =
                        new TreeMap<Tile, Integer>(new ValueComparator(antToDistance));
                sortedAntToDistance.putAll(antToDistance);

                Tile closestAnt = sortedAntToDistance.firstKey();
                if (!antToGoals.containsKey(closestAnt)) {
                    antToGoals.put(closestAnt, new ArrayList<Tile>());
                }

                antToGoals.get(closestAnt).add(foodLoc);
            }
        }

        // examine each ant move to closest food
        for (Map.Entry<Tile, List<Tile>> entry: antToGoals.entrySet()) {
            Tile myAnt = entry.getKey();
            List<Tile> goals = entry.getValue();

            goals = calcState.filterGoals(goals);
            if (goals.isEmpty()) {
                continue;
            }

            if (calcState.giveUp()) {
                return;
            }

            PathSpec pathSpec = new PathFinder(calcState,
                    myAnt, goals,
                    new FoodEstimator(calcState)).getAStarPath();
            if (pathSpec.getPath().size() > 1) {
                orders.add(calcState.doMoveToLocation(myAnt, pathSpec.getPath().get(1)));
                calcState.addTarget(pathSpec.getGoal());
            }
        }
    }

    private void unblockOwnHill() {
        for(Tile hill : calcState.getMyHills()) {
            if (calcState.giveUp()) {
                return;
            }

            if (calcState.getMyAnts().contains(hill) && !calcState.hasOrderForTile(hill, orders)) {
                for (Direction direction : Direction.values()) {
                    Order order = calcState.doMoveInDirection(hill, direction);
                    if (order != null) {
                        orders.add(order);
                        break;
                    }
                }
            }
        }
    }

    private void setupDefense() {
        // setup defense of our Motherland
        if (calcState.haveEnoughAntsForDefense()) {
            for (Tile myHill : calcState.getMyHills()) {
                if (calcState.giveUp()) {
                    return;
                }

                for (DiagDirection direction : DiagDirection.values()) {
                    Tile placeToHold = calcState.getDiagTile(myHill, direction);

                    if (calcState.getTyleType(placeToHold) == TileType.MY_ANT) {
                        calcState.getMotherlandDefenders().add(placeToHold);
                    }
                }
            }
        } else {
            calcState.getMotherlandDefenders().clear();
        }
    }
}
