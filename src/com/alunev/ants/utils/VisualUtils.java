package com.alunev.ants.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class VisualUtils {
    private static Map<TileType, String> typeToSymbol = new HashMap<TileType, String>();

    static {
        typeToSymbol.put(TileType.DEAD, "d");
        typeToSymbol.put(TileType.ENEMY_ANT, "e");
        typeToSymbol.put(TileType.FOOD, "f");
        typeToSymbol.put(TileType.LAND, ".");
        typeToSymbol.put(TileType.MY_ANT, "a");
        typeToSymbol.put(TileType.UNKNOWN, "_");
        typeToSymbol.put(TileType.WATER, "%");
    }


    public static void printMap(TileType[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print(typeToSymbol.get(map[i][j]));
            }
            System.out.println();
        }
    }

    public static void printMapWithPath(TileType[][] map, List<Tile> path) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (path.contains(new Tile(i, j))) {
                    System.out.print(typeToSymbol.get(TileType.MY_ANT));
                } else {
                    System.out.print(typeToSymbol.get(map[i][j]));
                }
            }
            System.out.println();
        }
    }
}
