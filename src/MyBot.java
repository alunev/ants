import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.alunev.ants.Ants;
import com.alunev.ants.bot.Bot;
import com.alunev.ants.logic.LinearRoute;
import com.alunev.ants.logic.PathFinder;
import com.alunev.ants.logic.RouteWithSimpleDistanceWeight;
import com.alunev.ants.logic.TurnTimer;
import com.alunev.ants.mechanics.DiagDirection;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private Set<Tile> unseen = new HashSet<Tile>();
    private Set<Tile> seenEnemyHills = new HashSet<Tile>();

    private Set<Tile> motherlandDefenders = new HashSet<Tile>();

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
    }

    /**
     * Main method - all orders are issued here.
     */
    @Override
    public void doTurn() {
        TurnTimer turnTimer = new TurnTimer(getAnts().getTurnTime(), getAnts().getEpsilon());

        Ants ants = getAnts();
        reservedTiles.clear();

        preventSteppingOnOwnHill(ants);

        setupDefense(ants, turnTimer);

        unblockOwnHill(ants, turnTimer);

        lookAndMoveForFood(ants, turnTimer);

        attackHills(ants, turnTimer);

        exploreMapMoves(ants, turnTimer);
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

        // find closest free ant for each hill
        SortedMap<Integer, LinearRoute> distancesToHills = new TreeMap<Integer, LinearRoute>();
        for (Tile hillLoc : seenEnemyHills) {
            if (turnTimer.giveUp()) {
                return;
            }

            for (Tile antLoc : getFreeToMoveAnts()) {
                if (!ants.hasOrderForTile(antLoc) && !reservedTiles.contains(hillLoc)) {
                    Integer dist = ants.getDistance(antLoc, hillLoc);
                    distancesToHills.put(dist, new LinearRoute(antLoc, hillLoc));
                }
            }
        }

        // attack hills
        for (LinearRoute route : distancesToHills.values()) {
            if (turnTimer.giveUp()) {
                return;
            }

            List<Tile> list = new PathFinder(getAnts(), route.start, route.end).getAStarPath(turnTimer);
            if (list.size() > 1) {
                doMoveToLocation(route.start, list.get(1));
            }
        }
    }

    private void exploreMapMoves(Ants ants, TurnTimer turnTimer) {
        // explore unseen areas
        if (turnTimer.giveUp()) {
            return;
        }

        Set<Tile> copy = new HashSet<Tile>();
        copy.addAll(unseen);
        for (Tile tile : copy) {
            if (ants.isVisible(tile)) {
                unseen.remove(tile);
            }
        }

        for (Tile myAnt : getFreeToMoveAnts()) {
            if (turnTimer.giveUp()) {
                return;
            }

            if (!ants.hasOrderForTile(myAnt)) {
                SortedSet<RouteWithSimpleDistanceWeight> routesForAnt = new TreeSet<RouteWithSimpleDistanceWeight>();
                for (Tile unseenTile : unseen) {
                    routesForAnt.add(new RouteWithSimpleDistanceWeight(
                            getAnts(), new LinearRoute(myAnt, unseenTile)));
                }

                if (turnTimer.giveUp()) {
                    return;
                }

                for (RouteWithSimpleDistanceWeight routeWithWeight : routesForAnt) {
                    if (turnTimer.giveUp()) {
                        return;
                    }

                    if (doMoveToLocation(routeWithWeight.getRoute().getStart(),
                            routeWithWeight.getRoute().getEnd())) {
                        break;
                    }
                }
            }
        }
    }

    private void lookAndMoveForFood(Ants ants, TurnTimer turnTimer) {
        if (turnTimer.giveUp()) {
            return;
        }

        // find close food
        SortedMap<Integer, LinearRoute> distancesToFood = new TreeMap<Integer, LinearRoute>();
        for (Tile foodLoc : ants.getFoodTiles()) {
            if (turnTimer.giveUp()) {
                return;
            }

            for (Tile antLoc : getFreeToMoveAnts()) {
                Integer dist = ants.getDistance(antLoc, foodLoc);
                distancesToFood.put(dist, new LinearRoute(antLoc, foodLoc));
            }
        }

        // move to food
        for (LinearRoute route : distancesToFood.values()) {
            if (turnTimer.giveUp()) {
                return;
            }

            if (!ants.hasOrderForTile(route.start) && !reservedTiles.contains(route.end)) {
                List<Tile> list = new PathFinder(getAnts(), route.start, route.end).getAStarPath(turnTimer);
                if (list.size() > 1) {
                    doMoveToLocation(route.start, list.get(1));
                }
            }
        }
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
            if (!motherlandDefenders.contains(tile)) {
                freeAnts.add(tile);
            }
        }

        return freeAnts;
    }
}
