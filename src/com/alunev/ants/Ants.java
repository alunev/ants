package com.alunev.ants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alunev.ants.mechanics.DiagDirection;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

/**
 * Holds all game data and current game state.
 */
public class Ants {
    /** Maximum map size. */
    public static final int MAX_MAP_SIZE = 256;

    private static final double TURN_TIME_EPSILON = 0.02;
    private static final int TURN_TIME_EPSILON_ABS = 50;

    private final boolean DEBUG;

    private final int loadTime;

    private final int turnTime;

    private final int rows;

    private final int cols;

    private final int turns;

    private final int viewRadius2;

    private final int attackRadius2;

    private final int spawnRadius2;

    private long turnStartTime;

    private final TileType map[][];

    private final Set<Tile> myAnts = new HashSet<Tile>();

    private final Set<Tile> enemyAnts = new HashSet<Tile>();

    private final Set<Tile> myHills = new HashSet<Tile>();

    private final Set<Tile> enemyHills = new HashSet<Tile>();

    private final Set<Tile> foodTiles = new HashSet<Tile>();

    private final Set<Order> orders = new HashSet<Order>();

    /**
     * Creates new {@link Ants} object.
     *
     * @param loadTime timeout for initializing and setting up the bot on turn 0
     * @param turnTime timeout for a single game turn, starting with turn 1
     * @param rows game map height
     * @param cols game map width
     * @param turns maximum number of turns the game will be played
     * @param viewRadius2 squared view radius of each ant
     * @param attackRadius2 squared attack radius of each ant
     * @param spawnRadius2 squared spawn radius of each ant
     */
    public Ants(int loadTime, int turnTime, int rows, int cols, int turns, int viewRadius2,
            int attackRadius2, int spawnRadius2) {
        this.loadTime = loadTime;
        this.turnTime = turnTime;
        this.rows = rows;
        this.cols = cols;
        this.turns = turns;
        this.viewRadius2 = viewRadius2;
        this.attackRadius2 = attackRadius2;
        this.spawnRadius2 = spawnRadius2;
        map = new TileType[rows][cols];

        for (TileType[] row : map) {
            Arrays.fill(row, TileType.LAND);
        }

        String debugProp = System.getProperty("DEBUG");
        if (debugProp != null) {
            this.DEBUG = true;
        } else {
            this.DEBUG = false;
        }
    }

    /**
     * Returns timeout for initializing and setting up the bot on turn 0.
     *
     * @return timeout for initializing and setting up the bot on turn 0
     */
    public int getLoadTime() {
        return loadTime;
    }

    /**
     * Returns timeout for a single game turn, starting with turn 1.
     *
     * @return timeout for a single game turn, starting with turn 1
     */
    public int getTurnTime() {
        return turnTime;
    }

    public long getEpsilon() {
        // return Math.round(turnTime * TURN_TIME_EPSILON + 1);
        return TURN_TIME_EPSILON_ABS;
    }

    /**
     * Returns game map height.
     *
     * @return game map height
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns game map width.
     *
     * @return game map width
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns maximum number of turns the game will be played.
     *
     * @return maximum number of turns the game will be played
     */
    public int getTurns() {
        return turns;
    }

    /**
     * Returns squared view radius of each ant.
     *
     * @return squared view radius of each ant
     */
    public int getViewRadius2() {
        return viewRadius2;
    }

    /**
     * Returns squared attack radius of each ant.
     *
     * @return squared attack radius of each ant
     */
    public int getAttackRadius2() {
        return attackRadius2;
    }

    /**
     * Returns squared spawn radius of each ant.
     *
     * @return squared spawn radius of each ant
     */
    public int getSpawnRadius2() {
        return spawnRadius2;
    }

    /**
     * Sets turn start time.
     *
     * @param turnStartTime turn start time
     */
    public void setTurnStartTime(long turnStartTime) {
        this.turnStartTime = turnStartTime;
    }

    /**
     * Returns how much time the bot has still has to take its turn before timing out.
     *
     * @return how much time the bot has still has to take its turn before timing out
     */
    public int getTimeRemaining() {
        return turnTime - (int)(System.currentTimeMillis() - turnStartTime);
    }

    /**
     * Returns ilk at the specified location.
     *
     * @param tile location on the game map
     *
     * @return ilk at the <cod>tile</code>
     */
    public TileType getTyleType(Tile tile) {
        return map[tile.getRow()][tile.getCol()];
    }

    /**
     * Sets ilk at the specified location.
     *
     * @param tile location on the game map
     * @param ilk ilk to be set at <code>tile</code>
     */
    public void setTileType(Tile tile, TileType type) {
        map[tile.getRow()][tile.getCol()] = type;
    }

    /**
     * Returns ilk at the location in the specified direction from the specified location.
     *
     * @param tile location on the game map
     * @param direction direction to look up
     *
     * @return ilk at the location in <code>direction</code> from <cod>tile</code>
     */
    public TileType getIlk(Tile tile, Direction direction) {
        Tile newTile = getTile(tile, direction);
        return map[newTile.getRow()][newTile.getCol()];
    }

    /**
     * Returns location in the specified direction from the specified location.
     *
     * @param tile location on the game map
     * @param direction direction to look up
     *
     * @return location in <code>direction</code> from <cod>tile</code>
     */
    public Tile getTile(Tile tile, Direction direction) {
        return getTile(tile, direction.getRowDelta(), direction.getColDelta());
    }

    public Tile getDiagTile(Tile tile, DiagDirection direction) {
        return getTile(tile, direction.getRowDelta(), direction.getColDelta());
    }

    public Tile getTile(Tile tile, int rowDelta, int colDelta) {
        int row = (tile.getRow() + rowDelta) % rows;
        if (row < 0) {
            row += rows;
        }
        int col = (tile.getCol() + colDelta) % cols;
        if (col < 0) {
            col += cols;
        }
        return new Tile(row, col);
    }

    /**
     * Returns a set containing all my ants locations.
     *
     * @return a set containing all my ants locations
     */
    public Set<Tile> getMyAnts() {
        return myAnts;
    }

    /**
     * Returns a set containing all enemy ants locations.
     *
     * @return a set containing all enemy ants locations
     */
    public Set<Tile> getEnemyAnts() {
        return enemyAnts;
    }

    /**
     * Returns a set containing all my hills locations.
     *
     * @return a set containing all my hills locations
     */
    public Set<Tile> getMyHills() {
        return myHills;
    }

    /**
     * Returns a set containing all enemy hills locations.
     *
     * @return a set containing all enemy hills locations
     */
    public Set<Tile> getEnemyHills() {
        return enemyHills;
    }

    /**
     * Returns a set containing all food locations.
     *
     * @return a set containing all food locations
     */
    public Set<Tile> getFoodTiles() {
        return foodTiles;
    }

    /**
     * Returns all orders sent so far.
     *
     * @return all orders sent so far
     */
    public Set<Order> getOrders() {
        return orders;
    }

    /**
     * Calculates distance between two locations on the game map.
     *
     * @param t1 one location on the game map
     * @param t2 another location on the game map
     *
     * @return distance between <code>t1</code> and <code>t2</code>
     */
    public int getDistance(Tile t1, Tile t2) {
        int rowDelta = Math.abs(t1.getRow() - t2.getRow());
        int colDelta = Math.abs(t1.getCol() - t2.getCol());

        rowDelta = Math.min(rowDelta, rows - rowDelta);
        colDelta = Math.min(colDelta, cols - colDelta);

        return rowDelta * rowDelta + colDelta * colDelta;
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
            if (t2.getRow() - t1.getRow() >= rows / 2) {
                directions.add(Direction.NORTH);
            } else {
                directions.add(Direction.SOUTH);
            }
        } else if (t1.getRow() > t2.getRow()) {
            if (t1.getRow() - t2.getRow() >= rows / 2) {
                directions.add(Direction.SOUTH);
            } else {
                directions.add(Direction.NORTH);
            }
        }
        if (t1.getCol() < t2.getCol()) {
            if (t2.getCol() - t1.getCol() >= cols / 2) {
                directions.add(Direction.WEST);
            } else {
                directions.add(Direction.EAST);
            }
        } else if (t1.getCol() > t2.getCol()) {
            if (t1.getCol() - t2.getCol() >= cols / 2) {
                directions.add(Direction.EAST);
            } else {
                directions.add(Direction.WEST);
            }
        }
        return directions;
    }

    /**
     * Clears game state information about my ants locations.
     */
    public void clearMyAnts() {
        for (Tile myAnt : myAnts) {
            map[myAnt.getRow()][myAnt.getCol()] = TileType.LAND;
        }
        myAnts.clear();
    }

    /**
     * Clears game state information about enemy ants locations.
     */
    public void clearEnemyAnts() {
        for (Tile enemyAnt : enemyAnts) {
            map[enemyAnt.getRow()][enemyAnt.getCol()] = TileType.LAND;
        }
        enemyAnts.clear();
    }

    /**
     * Clears game state information about my hills locations.
     */
    public void clearMyHills() {
        myHills.clear();
    }

    /**
     * Clears game state information about enemy hills locations.
     */
    public void clearEnemyHills() {
        enemyHills.clear();
    }

    /**
     * Updates game state information about new ants and food locations.
     *
     * @param ilk ilk to be updated
     * @param tile location on the game map to be updated
     */
    public void update(TileType ilk, Tile tile) {
        map[tile.getRow()][tile.getCol()] = ilk;
        switch (ilk) {
            case FOOD:
                foodTiles.add(tile);
            break;
            case MY_ANT:
                myAnts.add(tile);
            break;
            case ENEMY_ANT:
                enemyAnts.add(tile);
            break;
        }
    }

    /**
     * Updates game state information about hills locations.
     *
     * @param owner owner of hill
     * @param tile location on the game map to be updated
     */
    public void updateHills(int owner, Tile tile) {
        if (owner > 0)
            enemyHills.add(tile);
        else
            myHills.add(tile);
    }

    /**
     * Issues an order by sending it to the system output.
     *
     * @param myAnt map tile with my ant
     * @param direction direction in which to move my ant
     */
    public void issueOrder(Tile myAnt, Direction direction) {
        Order order = new Order(myAnt, direction);
        orders.add(order);
        System.out.println(order);
        System.out.flush();
    }

    public boolean isVisible(Tile tile) {
        boolean visible = false;
        for (Tile myAnt : getMyAnts()) {
            if (getDistance(myAnt, tile) <  getViewRadius2()) {
                visible = true;
                break;
            }
        }

        return visible;
    }

    public boolean isVisibleForAnt(Tile myAnt, Tile tile) {
        return getDistance(myAnt, tile) <  getViewRadius2();
    }

    public boolean hasOrderForTile(Tile tile) {
        boolean hasOrder = false;
        for (Order order : getOrders()) {
            if (order.getRow() == tile.getRow()
                    && order.getCol() == tile.getCol()) {
                hasOrder = true;
                break;
            }
        }

        return hasOrder;
    }
}
