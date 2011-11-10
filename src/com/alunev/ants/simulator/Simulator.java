package com.alunev.ants.simulator;

import com.alunev.ants.calculation.MapUtils;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

import java.util.*;

public class Simulator {
    private GameSetup gameSetup;
    private TileType[][] map;
    private Set<Tile> myAnts;
    private Map<TileType, String> typeToStatusCode = new HashMap<TileType, String>();

    {
        typeToStatusCode.put(TileType.MY_ANT, "a");
        typeToStatusCode.put(TileType.DEAD, "d");
        typeToStatusCode.put(TileType.ENEMY_ANT, "a");
        typeToStatusCode.put(TileType.FOOD, "f");
        typeToStatusCode.put(TileType.WATER, "w");
    }

    public Simulator(GameSetup gameSetup, TileType[][] map) {
        this.gameSetup = gameSetup;
        this.map = map;

        this.myAnts = new HashSet<Tile>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == TileType.MY_ANT) {
                    this.myAnts.add(new Tile(i,j));
                    break;
                }
            }
        }
    }

    public void processOrders(List<Order> orders) {
        for (Order order : orders) {
            MapUtils mapUtils = new MapUtils(gameSetup);
            Tile targetTile = mapUtils.getTile(order.getTile(), order.getDirection());

            processMove(order.getTile(), targetTile);
        }
    }

    public void processMove(Tile from, Tile to) {
            if (map[to.getRow()][to.getCol()].isPassable()) {
                map[from.getRow()][from.getCol()] = TileType.LAND;
                map[to.getRow()][to.getCol()] = TileType.MY_ANT;

                myAnts.remove(from);
                myAnts.add(to);
            } else {
                throw new RuntimeException("Invalid move from " + from + " to " + to);
            }
    }

    public TileType[][] getMap() {
        return this.map;
    }

    public TileType[][] getMapCopy() {
        TileType[][] copy = new TileType[map.length][map[0].length];

        for (int i = 0;i < map.length;i++) {
            System.arraycopy(map[i], 0, copy[i], 0, map[0].length);
        }

        return copy;
    }

    public List<String> getGameStateStrings() {
        List<String> lines = new ArrayList<String>();
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != TileType.LAND
                        && new MapUtils(gameSetup).isVisible(new Tile(i,j), myAnts, gameSetup.getViewRadius2())) {
                    line.setLength(0);
                    line.append(typeToStatusCode.get(map[i][j]));
                    line.append(" ");
                    line.append(i);
                    line.append(" ");
                    line.append(j);

                    if (map[i][j] == TileType.MY_ANT) {
                        line.append(" ");
                        line.append(0);
                    }

                    lines.add(line.toString());
                }
            }
        }

        return lines;
    }
}
