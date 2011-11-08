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
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: red
 * Date: 11/8/11
 * Time: 11:50 PM
 */
public class CalcStateTest {
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
