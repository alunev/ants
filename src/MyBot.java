import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alunev.ants.Ants;
import com.alunev.ants.bot.Bot;
import com.alunev.ants.logic.LinearRoute;
import com.alunev.ants.logic.TurnTimer;
import com.alunev.ants.logic.pathfind.FoodEstimator;
import com.alunev.ants.logic.pathfind.HillEstimator;
import com.alunev.ants.logic.pathfind.PathFinder;
import com.alunev.ants.logic.pathfind.PathSpec;
import com.alunev.ants.mechanics.DiagDirection;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    private static final int UNSEEN_TILES_TO_ANALYZE = 1;

    private int hillAttackRadius;

    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private Set<Tile> unseen = new HashSet<Tile>();
    private Set<Tile> seenEnemyHills = new HashSet<Tile>();

    private Set<Tile> motherlandDefenders = new HashSet<Tile>();
    private Set<Tile> targetTiles = new HashSet<Tile>();

    /**
     * Main method executed by the game engine for starting the bot.
     *
     * @param args command line arguments
     *
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        new MyBot().readSystemInput();
    }

    @Override
    public void setup(int loadTime, int turnTime, int rows, int cols,
            int turns, int viewRadius2, int attackRadius2, int spawnRadius2) {
        super.setup(loadTime, turnTime, rows, cols, turns, viewRadius2, attackRadius2,
                spawnRadius2);

        this.unseen = new HashSet<Tile>(rows * cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                unseen.add(new Tile(i, j));
            }
        }

        this.hillAttackRadius = viewRadius2 * 2;
    }

    /**
     * Main method - all orders are issued here.
     */
    @Override
    public void doTurn() {
        TurnTimer turnTimer = new TurnTimer(getAnts().getTurnTime(), getAnts().getEpsilon());

        Ants ants = getAnts();

        reservedTiles.clear();
        targetTiles.clear();

        preventSteppingOnOwnHill(ants);

        unblockOwnHill(ants, turnTimer);

        setupDefense(ants, turnTimer);

        lookAndMoveForFood(ants, turnTimer);

        attackHills(ants, turnTimer);

        exploreMapMoves(ants, turnTimer);

        doRandomMoveAfterAll(ants, turnTimer);
    }

    private void doRandomMoveAfterAll(Ants ants, TurnTimer turnTimer) {
        if (turnTimer.giveUp()) {
            return;
        }

        Random random = new Random(System.currentTimeMillis());
        for (Tile tile : getFreeToMoveAnts()) {
            if (turnTimer.giveUp()) {
                return;
            }

            boolean moved = false;
            int tries = 0;
            while (!moved && tries++ < 10) {
                Direction direction = Direction.values()[random.nextInt(4)];

                if (doMoveInDirection(tile, direction)) {
                    moved = true;
                }

                tries++;
            }
        }
    }

    private void attackHills(Ants ants, TurnTimer turnTimer) {
        if (turnTimer.giveUp()) {
            return;
        }

        // add new hills to set
        for (Tile enemyHill : ants.getEnemyHills()) {
            if (!seenEnemyHills.contains(enemyHill)) {
                seenEnemyHills.add(enemyHill);
            }
        }

        // form list of hills for each ant
        Map<Tile, List<Tile>> antToGoals = new HashMap<Tile, List<Tile>>();
        for (Tile enemyHill : seenEnemyHills) {
            if (turnTimer.giveUp()) {
                return;
            }

            for (Tile myAnt : getFreeToMoveAnts()) {
                if (ants.getDistance(myAnt, enemyHill) < hillAttackRadius) {
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
            goals = filterGoals(goals);
            if (goals.isEmpty()) {
                continue;
            }

            if (turnTimer.giveUp()) {
                return;
            }

            PathSpec pathSpec = new PathFinder(getAnts(),
                    myAnt, goals,
                    new HillEstimator()).getAStarPath(turnTimer);
            if (pathSpec.getPath().size() > 1) {
                doMoveToLocation(myAnt, pathSpec.getPath().get(1));
                targetTiles.add(pathSpec.getGoal());
            }
        }
    }

    private void exploreMapMoves(Ants ants, TurnTimer turnTimer) {
        // explore unseen areas
        Set<Tile> copy = new HashSet<Tile>();
        copy.addAll(unseen);
        for (Tile tile : copy) {
            if (ants.isVisible(tile)) {
                unseen.remove(tile);
            }
        }

        if (unseen.size() == 0 || turnTimer.giveUp()) {
            return;
        }

        for (Tile myAnt : getFreeToMoveAnts()) {
            if (ants.hasOrderForTile(myAnt)) {
                continue;
            }

            if (turnTimer.giveUp()) {
                return;
            }

            // find closest unseen tiles
            SortedMap<Integer, LinearRoute> routesForAnt = new TreeMap<Integer, LinearRoute>();
            for (Tile unseenTile : unseen) {
                if (ants.getTyleType(unseenTile).isUnoccupied()) {
                    routesForAnt.put(ants.getDistance(myAnt, unseenTile),
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

            goals = filterGoals(goals);
            if (goals.isEmpty()) {
                continue;
            }

            PathSpec pathSpec = new PathFinder(getAnts(), myAnt, goals,
                    new HillEstimator()).getAStarPath(turnTimer);
            if (pathSpec.getPath().size() > 1 && !reservedTiles.contains(pathSpec.getPath().get(1))) {
                doMoveToLocation(myAnt, pathSpec.getPath().get(1));
                targetTiles.add(pathSpec.getGoal());
            }
        }
    }

    private void lookAndMoveForFood(Ants ants, TurnTimer turnTimer) {
        if (turnTimer.giveUp()) {
            return;
        }

        // find close food
        Map<Tile, List<Tile>> antToGoals = new HashMap<Tile, List<Tile>>();
        for (Tile foodLoc : ants.getFoodTiles()) {
            if (turnTimer.giveUp()) {
                return;
            }

            for (Tile myAnt : getFreeToMoveAnts()) {
                // if (ants.isVisibleForAnt(myAnt, foodLoc)) {
                    if (!antToGoals.containsKey(myAnt)) {
                        antToGoals.put(myAnt, new ArrayList<Tile>());
                    }

                    antToGoals.get(myAnt).add(foodLoc);
                // }
            }
        }

        // examine each ant move to closest food
        for (Map.Entry<Tile, List<Tile>> entry: antToGoals.entrySet()) {
            Tile myAnt = entry.getKey();
            List<Tile> goals = entry.getValue();

            goals = filterGoals(goals);
            if (goals.isEmpty()) {
                continue;
            }

            if (turnTimer.giveUp()) {
                return;
            }

            PathSpec pathSpec = new PathFinder(getAnts(),
                    myAnt, goals,
                    new FoodEstimator(ants)).getAStarPath(turnTimer);
            if (pathSpec.getPath().size() > 1) {
                doMoveToLocation(myAnt, pathSpec.getPath().get(1));
                targetTiles.add(pathSpec.getGoal());
            }
        }
    }


    private List<Tile> filterGoals(List<Tile> goals) {
        // filter routes
        List<Tile> filteredGoals = new ArrayList<Tile>(goals.size());
        for (Tile goal : goals) {
            if (!targetTiles.contains(goal)
                    && !reservedTiles.contains(goal)) {
                filteredGoals.add(goal);
            }
        }
        return filteredGoals;
    }

    private void unblockOwnHill(Ants ants, TurnTimer turnTimer) {
        for(Tile hill : ants.getMyHills()) {
            if (turnTimer.giveUp()) {
                return;
            }

            if (ants.getMyAnts().contains(hill) && !ants.getOrders().contains(hill)) {
                for (Direction direction : Direction.values()) {
                    if (doMoveInDirection(hill, direction)) {
                        break;
                    }
                }
            }
        }
    }

    private void preventSteppingOnOwnHill(Ants ants) {
        // prevent stepping on own hill
        reservedTiles.addAll(ants.getMyHills());
    }

    private void setupDefense(Ants ants, TurnTimer turnTimer) {
        // setup defense of our Motherland
        if (ants.getMyAnts().size() > 10) {
            for (Tile myHill : ants.getMyHills()) {
                if (turnTimer.giveUp()) {
                    return;
                }

                for (DiagDirection direction : DiagDirection.values()) {
                    Tile placeToHold = ants.getDiagTile(myHill, direction);

                    if (ants.getTyleType(placeToHold) == TileType.MY_ANT) {
                        motherlandDefenders.add(placeToHold);
                    }
                }
            }
        }

        // if thing so bad that we need to leae defense position to gather food - do it
        if (ants.getMyAnts().size() == motherlandDefenders.size()) {
            motherlandDefenders.clear();
        }
    }

    private boolean doMoveInDirection(Tile antLoc, Direction direction) {
        Ants ants = getAnts();

        // Track all moves, prevent collisions
        Tile newLoc = ants.getTile(antLoc, direction);
        if (ants.getTyleType(newLoc).isUnoccupied() && !reservedTiles.contains(newLoc)) {
            ants.issueOrder(antLoc, direction);
            reservedTiles.add(newLoc);
            return true;
        } else {
            return false;
        }
    }

    private boolean doMoveToLocation(Tile antLoc, Tile destLoc) {
        Ants ants = getAnts();

        // Track targets to prevent 2 ants to the same location
        List<Direction> directions = ants.getDirections(antLoc, destLoc);
        for (Direction direction : directions) {
            if (this.doMoveInDirection(antLoc, direction)) {
                return true;
            }
        }
        return false;
    }

    public Set<Tile> getFreeToMoveAnts() {
        Set<Tile> freeAnts = new HashSet<Tile>();

        for (Tile tile : getAnts().getMyAnts()) {
            if (!motherlandDefenders.contains(tile) && !getAnts().hasOrderForTile(tile)) {
                freeAnts.add(tile);
            }
        }

        return freeAnts;
    }
}
