package com.alunev.ants.io;

import java.util.List;

public interface InputParser {
    GameSetup parseSetup(List<String> input);

    GameState parseUpdate(List<String> input, int rows, int cols);
}
