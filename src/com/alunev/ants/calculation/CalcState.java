package com.alunev.ants.calculation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.logic.TurnTimer;
import com.alunev.ants.mechanics.DiagDirection;
import com.alunev.ants.mechanics.Direction;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class CalcState {
    private long CALC_TIME_EPSILON = 50;

    private final GameSetup gameSetup;
    private GameState gameState;

    // sets that live whole games
    private Set<Tile> unseenTiles = new HashSet<Tile>();
    private Set<Tile> seenEnemyHills = new HashSet<Tile>();
    private Set<Tile> seenFood = new HashSet<Tile>();
    private Set<Tile> motherlandDefenders = new HashSet<Tile>();

    // sets that live only turn calculation
    private Set<Tile> reservedTiles = new HashSet<Tile>();
    private Set<Tile> targetTiles = new HashSet<Tile>();

    // timer to avoid timeouts
    private TurnTimer turnTimer;


    public CalcState(GameSetup gameSetup) {
        this.gameSetup = gameSetup;
        this.unseenTiles = new HashSet<Tile>(gameSetup.getRows() * gameSetup.getCols());

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

    public GameState getGameState() {
        return gameState;
    }

    public void update(GameState gameState) {
        this.gameState = gameState;

        reservedTiles.clear();
        targetTiles.clear();

        // remove eaten food
        Set<Tile> filteredFood = new HashSet<Tile>();
        for (Tile foodTile : seenFood) {
            if (getTyleType(foodTile) == TileType.FOOD) {
                filteredFood.add(foodTile);
            }
        }

        // add new foods
        for (Tile foodLoc : gameState.getFoodTiles()) {
            filteredFood.add(foodLoc);
        }

        this.seenFood = filteredFood;

        // add new hills to set
        for (Tile enemyHill : gameState.getEnemyHills()) {
            seenEnemyHills.add(enemyHill);
        }

        // explore unseen areas
        Set<Tile> copy = new HashSet<Tile>();
        copy.addAll(unseenTiles);
        for (Tile tile : copy) {
            if (isVisible(tile)) {
                unseenTiles.remove(tile);
            }
        }

        // prevent stepping on own hill
        reservedTiles.addAll(gameState.getMyHills());

        this.gameState = gameState;
    }

    public boolean haveEnoughAntsForDefense() {
        return gameState.getMyAnts().size() > 10;
    }

    public Set<Tile> getFreeToMoveAnts(List<Order> orders) {
        Set<Tile> freeAnts = new HashSet<Tile>();

        for (Tile tile : gameState.getMyAnts()) {
            if (!motherlandDefenders.contains(tile) && !hasOrderForTile(tile, orders)) {
                freeAnts.add(tile);
            }
        }

        return freeAnts;
    }

    public boolean hasOrderForTile(Tile tile, List<Order> orders) {
        boolean hasOrder = false;
        for (Order order : orders) {
            if (order.getRow() == tile.getRow()
                    && order.getCol() == tile.getCol()) {
                hasOrder = true;
                break;
            }
        }

        return hasOrder;
    }

    public Tile getTile(Tile tile, Direction direction) {
        return getTile(tile, direction.getRowDelta(), direction.getColDelta());
    }

    public Tile getDiagTile(Tile tile, DiagDirection direction) {
        return getTile(tile, direction.getRowDelta(), direction.getColDelta());
    }

    public Tile getTile(Tile tile, int rowDelta, int colDelta) {
        int row = (tile.getRow() + rowDelta) % gameSetup.getRows();
        if (row < 0) {
            row += gameSetup.getRows();
        }
        int col = (tile.getCol() + colDelta) % gameSetup.getCols();
        if (col < 0) {
            col += gameSetup.getCols();
        }
        return new Tile(row, col);
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
        return new MapUtils(gameSetup).isVisible(tile, gameState.getMyAnts(), gameSetup.getViewRadius2());
    }

    public boolean isVisibleForAnt(Tile myAnt, Tile tile) {
        return new MapUtils(gameSetup).isVisibleForAnt(myAnt, tile);
    }

    public Order doMoveInDirection(Tile antLoc, Direction direction) {
        // Track all moves, prevent collisions
        Tile newLoc = getTile(antLoc, direction);
        if (getTyleType(newLoc).isUnoccupied() && !getReservedTiles().contains(newLoc)) {
            getReservedTiles().add(newLoc);
            return new Order(antLoc, direction);
        } else {
            return null;
        }
    }

    public Order doMoveToLocation(Tile antLoc, Tile destLoc) {
        // Track targets to prevent 2 ants to the same location
        List<Direction> directions = getDirections(antLoc, destLoc);
        for (Direction direction : directions) {
            return doMoveInDirection(antLoc, direction);
        }
        return null;
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

    public TileType getTyleType(Tile tile) {
        return gameState.getMap()[tile.getRow()][tile.getCol()];
    }

    public boolean isResered(Tile tile) {
        return reservedTiles.contains(tile);
    }

    public Set<Tile> getMyHills() {
        return gameState.getMyHills();
    }

    public void addTarget(Tile tile) {
        targetTiles.add(tile);
    }

    public Set<Tile> getMyAnts() {
        // TODO Auto-generated method stub
        return gameState.getMyAnts();
    }
}
