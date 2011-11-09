package com.alunev.ants.calculation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.logic.TurnTimer;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class CalcState {
    private static final long CALC_TIME_EPSILON = 50;

    private final GameSetup gameSetup;

    // sets that live whole games
    private TileType map[][];
    private Set<Tile> myAnts = new HashSet<Tile>();
    private Set<Tile> enemyAnts = new HashSet<Tile>();
    private Set<Tile> myHills = new HashSet<Tile>();
    private Set<Tile> seenEnemyHills = new HashSet<Tile>();
    private Set<Tile> seenFood = new HashSet<Tile>();
    private Set<Tile> unseenTiles = new HashSet<Tile>();
    private Set<Tile> motherlandDefenders = new HashSet<Tile>();

    // sets that live only turn calculation
    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private Set<Tile> targetTiles = new HashSet<Tile>();

    // timer to avoid timeouts
    private TurnTimer turnTimer;


    public CalcState(GameSetup gameSetup) {
        this.gameSetup = gameSetup;

        this.map = new TileType[gameSetup.getRows()][gameSetup.getCols()];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = TileType.UNKNOWN;
            }
        }

        for (int i = 0; i < gameSetup.getRows(); i++) {
            for (int j = 0; j < gameSetup.getCols(); j++) {
                unseenTiles.add(new Tile(i, j));
            }
        }

        this.turnTimer = new TurnTimer(gameSetup.getTurnTime(), CALC_TIME_EPSILON);
    }

    public Set<Tile> getReservedTiles() {
        return reservedTiles;
    }

    public Set<Tile> getUnseenTiles() {
        return unseenTiles;
    }

    public Set<Tile> getSeenEnemyHills() {
        return seenEnemyHills;
    }

    public Set<Tile> getSeenFood() {
        return seenFood;
    }

    public Set<Tile> getMotherlandDefenders() {
        return motherlandDefenders;
    }

    public Set<Tile> getTargetTiles() {
        return targetTiles;
    }

    public boolean giveUp() {
        return turnTimer.giveUp();
    }

    public GameSetup getGameSetup() {
        return gameSetup;
    }

    public void update(GameState gameState) {
        // clear all ants from old version of map
        // copy all visible things on new map to old one
        for (int i = 0;i < map.length; i++) {
            for (int j = 0;j < map[0].length;j++) {
                if (map[i][j] == TileType.MY_ANT) {
                    map[i][j] = TileType.LAND;
                }
                if (gameState.getMap()[i][j] != TileType.UNKNOWN) {
                    this.map[i][j] = gameState.getMap()[i][j];
                }
            }
        }

        this.myAnts = gameState.getMyAnts();
        this.enemyAnts = gameState.getEnemyAnts();
        this.myHills = gameState.getMyHills();
        this.seenEnemyHills.addAll(gameState.getEnemyHills());

        // remove eaten food
        MapUtils mapUtils = new MapUtils(gameSetup);
        Set<Tile> filteredFood = new HashSet<Tile>();
        filteredFood.addAll(seenFood);
        for (Tile foodTile : seenFood) {
            if (mapUtils.isVisible(foodTile, gameState.getMyAnts(), gameSetup.getViewRadius2())
                    && getTileType(foodTile) != TileType.FOOD) {
                filteredFood.remove(foodTile);
            }
        }

        // add new foods
        filteredFood.addAll(gameState.getFoodTiles());
        this.seenFood = filteredFood;

        // explore unseen areas
        Set<Tile> copy = new HashSet<Tile>();
        copy.addAll(unseenTiles);
        for (Tile tile : copy) {
            if (isVisible(tile)) {
                unseenTiles.remove(tile);
            }
        }

        // remove fallen defenders
        Set<Tile> defenders = new HashSet<Tile>();
        for (Tile defender : motherlandDefenders) {
            if (myAnts.contains(defender)) {
                defenders.add(defender);
            }
        }
        this.motherlandDefenders = defenders;

        // prevent stepping on own hill
        reservedTiles.clear();
        reservedTiles.addAll(gameState.getMyHills());

        targetTiles.clear();
    }

    public boolean haveEnoughAntsForDefense() {
        return myAnts.size() > 10;
    }

    public Set<Tile> getFreeToMoveAnts(List<Order> orders) {
        Set<Tile> freeAnts = new HashSet<Tile>();

        for (Tile tile : myAnts) {
            if (!motherlandDefenders.contains(tile) && !hasOrderForTile(tile, orders)) {
                freeAnts.add(tile);
            }
        }

        return freeAnts;
    }

    public boolean hasOrderForTile(Tile tile, List<Order> orders) {
        boolean hasOrder = false;
        for (Order order : orders) {
            if (order.getTile().getRow() == tile.getRow()
                    && order.getTile().getCol() == tile.getCol()) {
                hasOrder = true;
                break;
            }
        }

        return hasOrder;
    }

    /**
     * Returns one or two orthogonal directions from one location to the another.
     *
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     *
     * @return orthogonal directions from <code>t1</code> to <code>t2</code>
     */
    public List<Direction> getDirections(Tile t1, Tile t2) {
        List<Direction> directions = new ArrayList<Direction>();
        if (t1.getRow() < t2.getRow()) {
            if (t2.getRow() - t1.getRow() >= gameSetup.getRows() / 2) {
                directions.add(Direction.NORTH);
            } else {
                directions.add(Direction.SOUTH);
            }
        } else if (t1.getRow() > t2.getRow()) {
            if (t1.getRow() - t2.getRow() >= gameSetup.getRows() / 2) {
                directions.add(Direction.SOUTH);
            } else {
                directions.add(Direction.NORTH);
            }
        }

        if (t1.getCol() < t2.getCol()) {
            if (t2.getCol() - t1.getCol() >= gameSetup.getCols() / 2) {
                directions.add(Direction.WEST);
            } else {
                directions.add(Direction.EAST);
            }
        } else if (t1.getCol() > t2.getCol()) {
            if (t1.getCol() - t2.getCol() >= gameSetup.getCols() / 2) {
                directions.add(Direction.EAST);
            } else {
                directions.add(Direction.WEST);
            }
        }

        return directions;
    }

    public boolean isVisible(Tile tile) {
        return new MapUtils(gameSetup).isVisible(tile, myAnts, gameSetup.getViewRadius2());
    }

    public boolean isVisibleForAnt(Tile myAnt, Tile tile) {
        return new MapUtils(gameSetup).isVisibleForAnt(myAnt, tile);
    }

    public Order doMoveInDirection(Tile antLoc, Direction direction) {
        // Track all moves, prevent collisions
        Tile newLoc = new MapUtils(gameSetup).getTile(antLoc, direction);
        if (getTileType(newLoc).isUnoccupied() && !getReservedTiles().contains(newLoc)) {
            getReservedTiles().add(newLoc);
            return new Order(antLoc, direction);
        } else {
            return null;
        }
    }

    public Order doMoveToLocation(Tile antLoc, Tile destLoc) {
        // Track targets to prevent 2 ants to the same location
        List<Direction> directions = getDirections(antLoc, destLoc);
        Order order = null;
        for (Direction direction : directions) {
            if ((order = doMoveInDirection(antLoc, direction)) != null) {
                break;
            }
        }

        return order;
    }

    public List<Tile> filterGoals(List<Tile> goals) {
        // filter routes
        List<Tile> filteredGoals = new ArrayList<Tile>(goals.size());
        for (Tile goal : goals) {
            if (!getTargetTiles().contains(goal)
                    && !getReservedTiles().contains(goal)) {
                filteredGoals.add(goal);
            }
        }

        return filteredGoals;
    }

    public TileType getTileType(Tile tile) {
        return this.map[tile.getRow()][tile.getCol()];
    }

    public boolean isReserved(Tile tile) {
        return this.reservedTiles.contains(tile);
    }

    public Set<Tile> getMyHills() {
        return this.myHills;
    }

    public void addTarget(Tile tile) {
        targetTiles.add(tile);
    }

    public Set<Tile> getMyAnts() {
        return this.myAnts;
    }

    public TileType[][] getMap() {
        return map;
    }
}
