package com.alunev.ants;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alunev.ants.calculation.CalcState;
import com.alunev.ants.io.AntsInputParser;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.io.InputParser;
import com.alunev.ants.io.InputReader;
import com.alunev.ants.logic.pathfind.FoodEstimator;
import com.alunev.ants.logic.pathfind.PathFinder;
import com.alunev.ants.mechanics.Tile;

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

        assertEquals(path, pathFinder.getAStarPath().getPath());
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
        PathFinder pathFinder = new PathFinder(calcState, new Tile(10, 10), goals,
                new FoodEstimator(calcState));

        List<Tile> path = new ArrayList<Tile>();
        path.add(new Tile(10, 10));
        path.add(new Tile(10, 11));
        path.add(new Tile(10, 12));
        path.add(new Tile(10, 13));
        path.add(new Tile(11, 13));
        path.add(new Tile(12, 13));
        path.add(new Tile(12, 12));
        path.add(new Tile(12, 11));

        assertEquals(path, pathFinder.getAStarPath().getPath());
    }
}
