package com.alunev.ants.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class IOUtils {
    private static Map<TileType, String> typeToSymbol = new HashMap<TileType, String>();
    private static Map<String, TileType> symbolToType = new HashMap<String, TileType>();

    static {
        typeToSymbol.put(TileType.DEAD, "d");
        typeToSymbol.put(TileType.ENEMY_ANT, "e");
        typeToSymbol.put(TileType.FOOD, "*");
        typeToSymbol.put(TileType.LAND, ".");
        typeToSymbol.put(TileType.MY_ANT, "a");
        typeToSymbol.put(TileType.UNKNOWN, "?");
        typeToSymbol.put(TileType.WATER, "%");

        for (Map.Entry<TileType, String> entry : typeToSymbol.entrySet()) {
            symbolToType.put(entry.getValue(), entry.getKey());
        }
    }


    public static void printMap(TileType[][] map) {
        //noinspection ForLoopReplaceableByForEach
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

    public static TileType[][] readMap(Reader reader, int rows, int cols) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
        int row = 0;
        TileType[][] map = new TileType[rows][cols];
        while ((line = bufferedReader.readLine()) != null) {
            for (int col = 0;col < cols;col++) {
                map[row][col] = symbolToType.get(line.substring(col, col + 1));
            }
            row++;
        }

        return map;
    }
}
