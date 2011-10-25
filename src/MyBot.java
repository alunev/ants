import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.alunev.ants.Ants;
import com.alunev.ants.bot.Bot;
import com.alunev.ants.logic.Route;
import com.alunev.ants.logic.RouteWithWeight;
import com.alunev.ants.mechanics.Aim;
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
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
        Ants ants = getAnts();
        reservedTiles.clear();

        // prevent stepping on own hill
        for(Tile tile : ants.getMyHills()) {
            ants.issueOrder(tile, Aim.NONE);
        }



        // unblock own hill
        for(Tile hill : ants.getMyHills()) {
            if (ants.getMyAnts().contains(hill) && !ants.getOrders().contains(hill)) {
                for (Aim direction : Aim.values()) {
                    if (doMoveInDirection(hill, direction)) {
                        break;
                    }
                }
            }
        }

        // move to food
        Map<Tile, Tile> foodTargets = new HashMap<Tile, Tile>();

        // find close food
        SortedMap<Integer, Route> ant_dist = new TreeMap<Integer, Route>();
        for (Tile foodLoc : ants.getFoodTiles()) {
            for (Tile antLoc : ants.getMyAnts()) {
                Integer dist = ants.getDistance(antLoc, foodLoc);
                ant_dist.put(dist, new Route(antLoc, foodLoc));
            }
        }

        for (Route route : ant_dist.values()) {
            if (!foodTargets.containsKey(route.start) &&
                    !foodTargets.containsValue(route.end)) {
                doMoveToLocation(foodTargets, route.start, route.end);
            }
        }

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
                SortedSet<RouteWithWeight> routesForAnt = new TreeSet<RouteWithWeight>();
                for (Tile unseenTile : unseen) {
                    routesForAnt.add(new RouteWithWeight(getAnts(), new Route(myAnt, unseenTile)));
                }

                for (RouteWithWeight routeWithWeight : routesForAnt) {
                    if (doMoveToLocation(foodTargets, routeWithWeight.getRoute().getStart(),
                            routeWithWeight.getRoute().getEnd())) {
                        break;
                    }
                }
            }
        }
    }


    private boolean doMoveInDirection(Tile antLoc, Aim direction) {
        Ants ants = getAnts();

        // Track all moves, prevent collisions
        Tile newLoc = ants.getTile(antLoc, direction);
        if (ants.getIlk(newLoc).isUnoccupied() && !reservedTiles.contains(newLoc)) {
            ants.issueOrder(antLoc, direction);
            reservedTiles.add(newLoc);
            return true;
        } else {
            return false;
        }
    }

    private boolean doMoveToLocation(Map<Tile, Tile> movingTargets, Tile antLoc, Tile destLoc) {
        Ants ants = getAnts();

        // Track targets to prevent 2 ants to the same location
        List<Aim> directions = ants.getDirections(antLoc, destLoc);
        for (Aim direction : directions) {
            if (this.doMoveInDirection(antLoc, direction)) {
                movingTargets.put(antLoc, destLoc);
                return true;
            }
        }
        return false;
    }
}
