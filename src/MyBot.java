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
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Tile;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {
    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private Set<Tile> unseen = new HashSet<Tile>();

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
        Ants ants = getAnts();
        reservedTiles.clear();

        preventSteppingOnOwnHill(ants);

        unblockOwnHill(ants);

        lookAndMoveForFood(ants);

        exploreMapMoves(ants);
    }

    private void exploreMapMoves(Ants ants) {
        // explore unseen areas
        Set<Tile> copy = new HashSet<Tile>();
        copy.addAll(unseen);
        for (Tile tile : copy) {
            if (ants.isVisible(tile)) {
                unseen.remove(tile);
            }
        }

        for (Tile myAnt : ants.getMyAnts()) {
            if (!ants.hasOrderForTile(myAnt)) {
                SortedSet<RouteWithSimpleDistanceWeight> routesForAnt = new TreeSet<RouteWithSimpleDistanceWeight>();
                for (Tile unseenTile : unseen) {
                    routesForAnt.add(new RouteWithSimpleDistanceWeight(getAnts(), new LinearRoute(myAnt, unseenTile)));
                }

                for (RouteWithSimpleDistanceWeight routeWithWeight : routesForAnt) {
                    if (doMoveToLocation(routeWithWeight.getRoute().getStart(),
                            routeWithWeight.getRoute().getEnd())) {
                        break;
                    }
                }
            }
        }
    }

    private void lookAndMoveForFood(Ants ants) {
        // find close food
        SortedMap<Integer, LinearRoute> distancesToFood = new TreeMap<Integer, LinearRoute>();
        for (Tile foodLoc : ants.getFoodTiles()) {
            for (Tile antLoc : ants.getMyAnts()) {
                Integer dist = ants.getDistance(antLoc, foodLoc);
                distancesToFood.put(dist, new LinearRoute(antLoc, foodLoc));
            }
        }

        // move to food[63 28, 64 28, 64 29, 65 29, 66 29, 66 28, 67 28, 67 29, 68 29, 68 28, 68 27, 69 27, 70 27, 71 27, 72 27, 72 26, 72 25, 71 25, 70 25, 69 25, 69 24, 68 24, 67 24, 66 24, 65 24, 64 24, 64 23, 63 23, 62 23]
        for (LinearRoute route : distancesToFood.values()) {
            if (!ants.hasOrderForTile(route.start) &&
                    !reservedTiles.contains(route.end)) {
                List<Tile> list = new PathFinder(getAnts(), route.start, route.end).getAStarPath();
                if (list.size() > 1) {
                    doMoveToLocation(route.start, list.get(1));
                }
            }
        }
    }

    private void unblockOwnHill(Ants ants) {
        // unblock own hill
        for(Tile hill : ants.getMyHills()) {
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
}
