import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alunev.ants.Ants;
import com.alunev.ants.bot.Bot;
import com.alunev.ants.logic.LinearRoute;
import com.alunev.ants.logic.PathFinder;
import com.alunev.ants.logic.TurnTimer;
import com.alunev.ants.mechanics.DiagDirection;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    private static final int ATTACERS_PER_HILL = 2;

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

        for (Tile hillLoc : seenEnemyHills) {
            if (turnTimer.giveUp()) {
                return;
            }

            // find closest ants for this hill
            SortedMap<Integer, LinearRoute> routesToHill = new TreeMap<Integer, LinearRoute>();
            for (Tile antLoc : getFreeToMoveAnts()) {
                if (!ants.hasOrderForTile(antLoc) && !reservedTiles.contains(hillLoc)) {
                    Integer dist = ants.getDistance(antLoc, hillLoc);
                    routesToHill.put(dist, new LinearRoute(antLoc, hillLoc));
                }
            }

            // attack hill
            int attackers = 0;
            for (LinearRoute route : routesToHill.values()) {
                if (ants.hasOrderForTile(route.getStart())
                        || targetTiles.contains(route.getEnd())
                        || reservedTiles.contains(route.getEnd())) {
                    continue;
                }

                if (turnTimer.giveUp()) {
                    return;
                }

                List<Tile> list = new PathFinder(getAnts(), route.getStart(), route.getEnd()).getAStarPath(turnTimer);
                if (list.size() > 1 && reservedTiles.contains(route.getEnd())) {
                    doMoveToLocation(route.getStart(), list.get(1));
                    targetTiles.add(route.getEnd());

                    if (attackers++ >= ATTACERS_PER_HILL) {
                        break;
                    }
                }
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

            SortedMap<Integer, LinearRoute> routesForAnt = new TreeMap<Integer, LinearRoute>();
            for (Tile unseenTile : unseen) {
                routesForAnt.put(ants.getDistance(myAnt, unseenTile),
                        new LinearRoute(myAnt, unseenTile));
            }

            LinearRoute closestRoute = routesForAnt.get(routesForAnt.firstKey());
            List<Tile> list = new PathFinder(getAnts(), closestRoute.getStart(), closestRoute.getEnd())
                    .getAStarPath(turnTimer);
            if (list.size() > 1 && !reservedTiles.contains(list.get(1))) {
                doMoveToLocation(closestRoute.getStart(), list.get(1));
                targetTiles.add(closestRoute.getEnd());
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
            if (ants.hasOrderForTile(route.getStart())
                    || targetTiles.contains(route.getEnd())
                    || reservedTiles.contains(route.getEnd())) {
                continue;
            }

            if (turnTimer.giveUp()) {
                return;
            }

            List<Tile> list = new PathFinder(getAnts(), route.getStart(), route.getEnd()).getAStarPath(turnTimer);
            if (list.size() > 1) {
                doMoveToLocation(route.getStart(), list.get(1));
                targetTiles.add(route.getEnd());
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
