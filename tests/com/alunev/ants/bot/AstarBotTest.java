package com.alunev.ants.bot;

import com.alunev.ants.io.AntsInputParser;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.InputParser;
import com.alunev.ants.io.InputReader;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.TileType;
import com.alunev.ants.simulator.Simulator;
import com.alunev.ants.utils.IOUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: red
 * Date: 11/9/11
 * Time: 10:35 PM
 */
public class AstarBotTest {
    @Test
    public void testDoTurn() throws IOException {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/003.game_setup.txt")).readGameSetup());
        Simulator simulator = new Simulator(gameSetup, IOUtils.readMap(
                new FileReader("testdata/003.map.txt"), gameSetup.getRows(), gameSetup.getCols()));
        InputParser inputParser = new AntsInputParser();

        AstarBot astarBot = new AstarBot();
        astarBot.beforeStart(gameSetup);

        TileType[][] previousMap = simulator.getMapCopy();
        IOUtils.printMap(previousMap);

        int i = 0;
        TileType[][] map;
        while (i++ < 100 && simulator.getMap()[4][6] != TileType.MY_ANT) {
            astarBot.beforeUpdate(inputParser.parseUpdate(simulator.getGameStateStrings(), gameSetup));
            simulator.processOrders(astarBot.doTurn());

            map = simulator.getMap();
            IOUtils.printMap(map);

            boolean changed = false;
            for (int j = 0; j < map.length; j++) {
                changed = changed || !Arrays.equals(previousMap[j], map[j]);
            }
            assertTrue(changed);

            previousMap = simulator.getMapCopy();
        }

        assertTrue(simulator.getMap()[4][6] == TileType.MY_ANT);
    }

    @Test
    public void testMoveForFood() throws IOException {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/003.game_setup.txt")).readGameSetup());
        Simulator simulator = new Simulator(gameSetup, IOUtils.readMap(
                new FileReader("testdata/003.map.txt"), gameSetup.getRows(), gameSetup.getCols()));
        InputParser inputParser = new AntsInputParser();

        AstarBot astarBot = new AstarBot();
        astarBot.beforeStart(gameSetup);

        TileType[][] previousMap = simulator.getMapCopy();
        IOUtils.printMap(previousMap);

        int i = 0;
        TileType[][] map;
        List<Order> orders = new ArrayList<Order>();
        orders.add(null);
        while (i++ < 100 && simulator.getMap()[4][6] != TileType.MY_ANT) {
            astarBot.beforeUpdate(inputParser.parseUpdate(simulator.getGameStateStrings(), gameSetup));

            orders.clear();
            orders.addAll(astarBot.lookAndMoveForFood());
            simulator.processOrders(orders);

            map = simulator.getMap();

            System.out.println("Turn " + i);
            IOUtils.printMap(map);

            boolean changed = false;
            for (int j = 0; j < map.length; j++) {
                changed = changed || !Arrays.equals(previousMap[j], map[j]);
            }
            assertTrue(changed);

            previousMap = simulator.getMapCopy();
        }

        assertTrue(simulator.getMap()[4][6] == TileType.MY_ANT);
    }

}
