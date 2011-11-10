package com.alunev.ants.simulator;

import com.alunev.ants.io.AntsInputParser;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.InputReader;
import com.alunev.ants.mechanics.TileType;
import com.alunev.ants.utils.IOUtils;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: red
 * Date: 11/8/11
 * Time: 10:35 PM
 */
public class SimulatorTest {
    @Test
    public void testGameStateString() throws Exception {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/001.game_setup.txt")).readGameSetup());
        TileType[][] map = IOUtils.readMap(
                new FileReader("testdata/001.map.txt"), gameSetup.getRows(), gameSetup.getCols());
        Simulator simulator = new Simulator(gameSetup, map);

        List<String> stateStrings = simulator.getGameStateStrings();

        for (String stateString : stateStrings) {
            System.out.println(stateString);
        }

        List<String> expected = new ArrayList<String>();
        expected.add("w 8 8");
        expected.add("w 8 9");
        expected.add("w 8 10");
        expected.add("w 8 11");
        expected.add("w 9 8");
        expected.add("a 9 9 0");
        expected.add("w 10 8");
        expected.add("w 10 9");
        expected.add("w 10 10");
        expected.add("w 10 11");
        expected.add("f 11 8");

        assertArrayEquals(stateStrings.toArray(), expected.toArray());
    }

     @Test
    public void testGameStateStringsMap004() throws Exception {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/004.game_setup.txt")).readGameSetup());
        TileType[][] map = IOUtils.readMap(
                new FileReader("testdata/004.map.txt"), gameSetup.getRows(), gameSetup.getCols());
        Simulator simulator = new Simulator(gameSetup, map);

        List<String> stateStrings = simulator.getGameStateStrings();

        for (String stateString : stateStrings) {
            System.out.println(stateString);
        }

        List<String> expected = new ArrayList<String>();
        expected.add("w 6 7");
        expected.add("w 6 8");
        expected.add("w 6 9");
        expected.add("w 6 10");
        expected.add("w 6 11");
        expected.add("w 8 9");
        expected.add("w 9 9");
        expected.add("w 7 7");
        expected.add("a 7 9 0");


        assertTrue(stateStrings.containsAll(expected));
    }
}
