package com.alunev.ants.calculation;

import com.alunev.ants.io.AntsInputParser;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.io.InputReader;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;
import com.alunev.ants.simulator.Simulator;
import com.alunev.ants.utils.VisualUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by IntelliJ IDEA.
 * User: red
 * Date: 11/8/11
 * Time: 11:50 PM
 */
public class CalcStateTest {
    @Test
    public void testMapUpdate() throws IOException {
        GameSetup gameSetup = new GameSetup(0, 0, 5, 5, 0, 2, 0, 0, 0);

        CalcState calcStateExpected = new CalcState(gameSetup);
        calcStateExpected.getMap()[0][0] = TileType.UNKNOWN;
        calcStateExpected.getMap()[0][1] = TileType.WATER;
        calcStateExpected.getMap()[0][2] = TileType.WATER;
        calcStateExpected.getMap()[0][3] = TileType.FOOD;
        calcStateExpected.getMap()[0][4] = TileType.UNKNOWN;
        calcStateExpected.getMap()[1][0] = TileType.UNKNOWN;
        calcStateExpected.getMap()[1][1] = TileType.LAND;
        calcStateExpected.getMap()[1][2] = TileType.LAND;
        calcStateExpected.getMap()[1][3] = TileType.LAND;
        calcStateExpected.getMap()[1][4] = TileType.UNKNOWN;
        calcStateExpected.getMap()[2][0] = TileType.WATER;
        calcStateExpected.getMap()[2][1] = TileType.WATER;
        calcStateExpected.getMap()[2][2] = TileType.WATER;
        calcStateExpected.getMap()[2][3] = TileType.LAND;
        calcStateExpected.getMap()[2][4] = TileType.UNKNOWN;
        calcStateExpected.getMap()[3][0] = TileType.LAND;
        calcStateExpected.getMap()[3][1] = TileType.MY_ANT;
        calcStateExpected.getMap()[3][2] = TileType.FOOD;
        calcStateExpected.getMap()[3][3] = TileType.UNKNOWN;
        calcStateExpected.getMap()[3][4] = TileType.UNKNOWN;
        calcStateExpected.getMap()[4][0] = TileType.LAND;
        calcStateExpected.getMap()[4][1] = TileType.FOOD;
        calcStateExpected.getMap()[4][2] = TileType.LAND;
        calcStateExpected.getMap()[4][3] = TileType.UNKNOWN;
        calcStateExpected.getMap()[4][4] = TileType.UNKNOWN;

        calcStateExpected.getMyAnts().add(new Tile(3, 1));
        calcStateExpected.getMyHills().add(new Tile(1, 2));
        calcStateExpected.getSeenFood().add(new Tile(0, 3));
        calcStateExpected.getSeenFood().add(new Tile(3, 2));
        calcStateExpected.getSeenFood().add(new Tile(4, 1));

        CalcState calcState = new CalcState(gameSetup);

        InputReader inputReader = new InputReader(new FileInputStream("testdata/game_state.002.txt"));
        GameState gameStateUpdate = new AntsInputParser().parseUpdate(inputReader.readGameUpdate(), gameSetup);
        calcState.update(gameStateUpdate);

        inputReader = new InputReader(new FileInputStream("testdata/game_state.003.txt"));
        gameStateUpdate = new AntsInputParser().parseUpdate(inputReader.readGameUpdate(), gameSetup);
        calcState.update(gameStateUpdate);

        for (int i = 0; i < calcState.getMap().length; i++) {
            assertArrayEquals(calcState.getMap()[i], calcState.getMap()[i]);
        }
    }

    @Test
    public void testSingleUpdate() throws IOException {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/001.game_setup.txt")).readGameSetup());
        TileType[][] map = VisualUtils.readMap(
                new FileReader("testdata/001.map.txt"), gameSetup.getRows(), gameSetup.getCols());
        Simulator simulator = new Simulator(gameSetup, map);

        List<String> stateStrings = simulator.getGameStateStrings();
        GameState gameStateUpdate = new AntsInputParser().parseUpdate(stateStrings, gameSetup);

        CalcState calcState = new CalcState(gameSetup);
        calcState.update(gameStateUpdate);

        assertEquals(calcState.getGameSetup(), gameSetup);
        assertThat(calcState.getMyAnts(), hasItem(new Tile(9, 9)));
        assertThat(calcState.getSeenFood(), hasItem(new Tile(11, 8)));
        assertThat(calcState.getFreeToMoveAnts(new ArrayList<Order>()), hasItem(new Tile(9, 9)));
    }

    @Test
    public void testCoupleSimpleUpdates() throws IOException {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/002.game_setup.txt")).readGameSetup());
        GameState gameStateUpdate = loadGameStateFromMap(gameSetup, "testdata/002.01.map.txt");

        CalcState calcState = new CalcState(gameSetup);
        calcState.update(gameStateUpdate);

        assertEquals(calcState.getGameSetup(), gameSetup);
        assertThat(calcState.getMyAnts(), hasItem(new Tile(9, 9)));
        assertThat(calcState.getSeenFood(), hasItem(new Tile(11, 8)));
        assertThat(calcState.getFreeToMoveAnts(new ArrayList<Order>()), hasItem(new Tile(9, 9)));

        gameStateUpdate = loadGameStateFromMap(gameSetup, "testdata/002.02.map.txt");
        calcState.update(gameStateUpdate);

        assertEquals(calcState.getGameSetup(), gameSetup);
        assertThat(calcState.getMyAnts(), hasItem(new Tile(9, 10)));
        assertThat(calcState.getSeenFood(), hasItem(new Tile(11, 8)));
        assertThat(calcState.getFreeToMoveAnts(new ArrayList<Order>()), hasItem(new Tile(9, 10)));

        gameStateUpdate = loadGameStateFromMap(gameSetup, "testdata/002.03.map.txt");
        calcState.update(gameStateUpdate);

        assertEquals(calcState.getGameSetup(), gameSetup);
        assertThat(calcState.getMyAnts(), hasItem(new Tile(9, 11)));
        assertThat(calcState.getSeenFood(), hasItems(new Tile(11, 8), new Tile(7, 10)));
        assertThat(calcState.getFreeToMoveAnts(new ArrayList<Order>()), hasItem(new Tile(9, 11)));
    }

    private GameState loadGameStateFromMap(GameSetup gameSetup, String mapFile) throws IOException {
        TileType[][] map = VisualUtils.readMap(
                new FileReader(mapFile), gameSetup.getRows(), gameSetup.getCols());
        Simulator simulator = new Simulator(gameSetup, map);

        return new AntsInputParser().parseUpdate(simulator.getGameStateStrings(), gameSetup);
    }
}
