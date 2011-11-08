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
    private Tile myAnt;
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

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == TileType.MY_ANT) {
                    this.myAnt = new Tile(i,j);
                    break;
                }
            }
        }
    }

    public void processOrders(List<Order> orders) {
        for (Order order : orders) {
            MapUtils mapUtils = new MapUtils(gameSetup);
            Tile targetTile = mapUtils.getTile(order.getTile(), order.getDirection());

            if (map[targetTile.getRow()][targetTile.getCol()].isPassable()) {
                map[order.getTile().getRow()][order.getTile().getCol()] = TileType.LAND;
                map[targetTile.getRow()][targetTile.getCol()] = TileType.MY_ANT;
            }
        }
    }

    public void processMove(Tile from, Tile to) {
            if (map[to.getRow()][to.getCol()].isPassable()) {
                map[from.getRow()][from.getCol()] = TileType.LAND;
                map[to.getRow()][to.getCol()] = TileType.MY_ANT;

                myAnt = new Tile(to.getRow(), to.getCol());
            }
    }

    public TileType[][] getMap() {
        return this.map;
    }

    public List<String> getGameStateStrings() {
        List<String> lines = new ArrayList<String>();
        StringBuilder line = new StringBuilder();
        Set<Tile> myAnts = new HashSet<Tile>();
        myAnts.add(myAnt);
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
