package com.alunev.ants.logic.pathfind;

import com.alunev.ants.calculation.CalcState;
import com.alunev.ants.io.*;
import com.alunev.ants.mechanics.Tile;
import com.alunev.ants.mechanics.TileType;
import com.alunev.ants.simulator.Simulator;
import com.alunev.ants.utils.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PathFinderTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSimpleAStarPath() throws Exception {
        FileInputStream fis = new FileInputStream("testdata/game_input.001.txt");
        InputReader inputReader = new InputReader(fis);
        InputParser inputParser = new AntsInputParser();
        GameSetup gameSetup = inputParser.parseSetup(inputReader.readGameSetup());
        GameState gameState = inputParser.parseUpdate(inputReader.readGameUpdate(), gameSetup);

        CalcState calcState = new CalcState(gameSetup);
        calcState.update(gameState);

        List<Tile> goals = new ArrayList<Tile>();
        goals.add(new Tile(2, 2));
        PathFinder pathFinder = new PathFinder(calcState, new Tile(0, 0), goals,
                new FoodEstimator(calcState));

        List<Tile> path = new ArrayList<Tile>();
        path.add(new Tile(0, 0));
        path.add(new Tile(0, 1));
        path.add(new Tile(1, 1));
        path.add(new Tile(1, 2));

        List<Tile> calculatedPath = pathFinder.getAStarPath().getPath();

        IOUtils.printMapWithPath(gameState.getMap(), calculatedPath);

        assertEquals(path, calculatedPath);
    }


    @Test
    public void testAStarPathWithWall() throws Exception {
        FileInputStream fis = new FileInputStream("testdata/game_input.002.txt");
        InputReader inputReader = new InputReader(fis);
        InputParser inputParser = new AntsInputParser();
        GameSetup gameSetup = inputParser.parseSetup(inputReader.readGameSetup());
        GameState gameState = inputParser.parseUpdate(inputReader.readGameUpdate(), gameSetup);

        CalcState calcState = new CalcState(gameSetup);
        calcState.update(gameState);

        List<Tile> goals = new ArrayList<Tile>();
        goals.add(new Tile(12, 10));
        PathFinder pathFinder = new PathFinder(calcState, new Tile(8, 10), goals,
                new FoodEstimator(calcState));

        List<Tile> path = new ArrayList<Tile>();
        path.add(new Tile(8, 10));
        path.add(new Tile(9, 10));
        path.add(new Tile(10, 10));
        path.add(new Tile(10, 11));
        path.add(new Tile(10, 12));
        path.add(new Tile(10, 13));
        path.add(new Tile(11, 13));
        path.add(new Tile(12, 13));
        path.add(new Tile(12, 12));
        path.add(new Tile(12, 11));

        List<Tile> calculatedPath = pathFinder.getAStarPath().getPath();

        IOUtils.printMapWithPath(gameState.getMap(), calculatedPath);

        assertEquals(path, calculatedPath);
    }

    @Test
    public void testAStarPathWithLoosingVisibility() throws Exception {
        FileInputStream fis = new FileInputStream("testdata/game_input.003.txt");
        InputReader inputReader = new InputReader(fis);
        InputParser inputParser = new AntsInputParser();
        GameSetup gameSetup = inputParser.parseSetup(inputReader.readGameSetup());
        GameState gameState = inputParser.parseUpdate(inputReader.readGameUpdate(), gameSetup);

        CalcState calcState = new CalcState(gameSetup);
        calcState.update(gameState);

        List<Tile> goals = new ArrayList<Tile>();
        for (Tile tile : gameState.getFoodTiles()) {
            goals.add(tile);
        }
        Tile myAnt = null;
        for (Tile tile : gameState.getMyAnts()) {
            myAnt = tile;
        }
        PathFinder pathFinder = new PathFinder(calcState, myAnt, goals, new FoodEstimator(calcState));

        List<Tile> path = new ArrayList<Tile>();
        path.add(new Tile(10, 9));
        path.add(new Tile(10, 10));
        path.add(new Tile(10, 11));
        path.add(new Tile(10, 12));
        path.add(new Tile(10, 13));
        path.add(new Tile(11, 13));
        path.add(new Tile(12, 13));
        path.add(new Tile(12, 12));
        path.add(new Tile(12, 11));
        path.add(new Tile(12, 10));
        path.add(new Tile(12, 9));

        List<Tile> calculatedPath = pathFinder.getAStarPath().getPath();

        IOUtils.printMapWithPath(gameState.getMap(), calculatedPath);

        assertEquals(path, calculatedPath);
    }

    @Test
    public void testAStarPathRealtime() throws Exception {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/001.game_setup.txt")).readGameSetup());
        TileType[][] map = IOUtils.readMap(
                new FileReader("testdata/001.map.txt"), gameSetup.getRows(), gameSetup.getCols());
        Simulator simulator = new Simulator(gameSetup, map);

        System.out.println("Starting Map");
        IOUtils.printMap(map);

        CalcState calcState = new CalcState(gameSetup);
        List<Tile> calculatedPath = new ArrayList<Tile>();
        calculatedPath.add(null);
        while (calculatedPath.size() > 1) {
            GameState gameStateUpdate = new AntsInputParser().parseUpdate(simulator.getGameStateStrings(), gameSetup);
            calcState.update(gameStateUpdate);

            List<Tile> goals = new ArrayList<Tile>();
            for (Tile tile : calcState.getSeenFood()) {
                goals.add(tile);
            }

            Tile myAnt = null;
            for (Tile tile : calcState.getMyAnts()) {
                myAnt = tile;
            }

            PathFinder pathFinder = new PathFinder(calcState, myAnt, goals, new FoodEstimator(calcState));
            calculatedPath = pathFinder.getAStarPath().getPath();

            simulator.processMove(calculatedPath.get(0), calculatedPath.get(1));

            System.out.println("Map after turn");
            IOUtils.printMap(simulator.getMap());
        }
    }

    @Test
    public void testAStarPathMap003() throws Exception {
        GameSetup gameSetup = new AntsInputParser().parseSetup(
                new InputReader(new FileInputStream("testdata/003.game_setup.txt")).readGameSetup());
        TileType[][] map = IOUtils.readMap(
                new FileReader("testdata/003.map.txt"), gameSetup.getRows(), gameSetup.getCols());
        Simulator simulator = new Simulator(gameSetup, map);

        System.out.println("Starting Map");
        IOUtils.printMap(map);

        CalcState calcState = new CalcState(gameSetup);
        List<Tile> calculatedPath = new ArrayList<Tile>();
        calculatedPath.add(null);
        while (calculatedPath.size() > 1) {
            GameState gameStateUpdate = new AntsInputParser().parseUpdate(simulator.getGameStateStrings(), gameSetup);
            calcState.update(gameStateUpdate);

            List<Tile> goals = new ArrayList<Tile>();
            for (Tile tile : calcState.getSeenFood()) {
                goals.add(tile);
            }

            Tile myAnt = null;
            for (Tile tile : calcState.getMyAnts()) {
                myAnt = tile;
            }

            PathFinder pathFinder = new PathFinder(calcState, myAnt, goals, new FoodEstimator(calcState));
            calculatedPath = pathFinder.getAStarPath().getPath();

            simulator.processMove(calculatedPath.get(0), calculatedPath.get(1));

            System.out.println("Map after turn");
            IOUtils.printMap(simulator.getMap());
        }
    }
}
