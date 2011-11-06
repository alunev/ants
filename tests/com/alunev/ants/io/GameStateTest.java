package com.alunev.ants.io;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;

import org.junit.Test;

import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;

public class GameStateTest {

    @Test
    public void testMerge() throws Exception {
        GameSetup gameSetup = new GameSetup(0, 0, 5, 5, 0, 2, 0, 0, 0);

        GameState gameState = new GameState(5, 5);
        gameState.getMap()[0][0] = TileType.UNKNOWN;
        gameState.getMap()[0][1] = TileType.WATER;
        gameState.getMap()[0][2] = TileType.WATER;
        gameState.getMap()[0][3] = TileType.FOOD;
        gameState.getMap()[0][4] = TileType.UNKNOWN;
        gameState.getMap()[1][0] = TileType.UNKNOWN;
        gameState.getMap()[1][1] = TileType.LAND;
        gameState.getMap()[1][2] = TileType.LAND;
        gameState.getMap()[1][3] = TileType.LAND;
        gameState.getMap()[1][4] = TileType.UNKNOWN;
        gameState.getMap()[2][0] = TileType.WATER;
        gameState.getMap()[2][1] = TileType.WATER;
        gameState.getMap()[2][2] = TileType.WATER;
        gameState.getMap()[2][3] = TileType.LAND;
        gameState.getMap()[2][4] = TileType.UNKNOWN;
        gameState.getMap()[3][0] = TileType.LAND;
        gameState.getMap()[3][1] = TileType.MY_ANT;
        gameState.getMap()[3][2] = TileType.FOOD;
        gameState.getMap()[3][3] = TileType.UNKNOWN;
        gameState.getMap()[3][4] = TileType.UNKNOWN;
        gameState.getMap()[4][0] = TileType.LAND;
        gameState.getMap()[4][1] = TileType.FOOD;
        gameState.getMap()[4][2] = TileType.LAND;
        gameState.getMap()[4][3] = TileType.UNKNOWN;
        gameState.getMap()[4][4] = TileType.UNKNOWN;

        gameState.getMyAnts().add(new Tile(3, 1));
        gameState.getMyHills().add(new Tile(1, 2));
        gameState.getFoodTiles().add(new Tile(0, 3));
        gameState.getFoodTiles().add(new Tile(3, 2));
        gameState.getFoodTiles().add(new Tile(4, 1));

        FileInputStream fis = new FileInputStream("testdata/game_state.002.txt");
        InputReader inputReader = new InputReader(fis);
        GameState gameStateMerged = new AntsInputParser().parseUpdate(inputReader.readGameUpdate(),
                gameSetup);

        FileInputStream fis2 = new FileInputStream("testdata/game_state.003.txt");
        InputReader inputReader2 = new InputReader(fis2);
        GameState gameState2 = new AntsInputParser().parseUpdate(inputReader2.readGameUpdate(),
                gameSetup);

        gameStateMerged.merge(gameState2);

        assertEquals(gameState, gameStateMerged);
    }

}
