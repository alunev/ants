package com.alunev.ants.bot;

import java.util.List;

import com.alunev.ants.calculation.CalcState;
import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.mechanics.Order;
import com.alunev.ants.mechanics.TileType;

public interface Bot {
    String beforeStart(GameSetup gameSetup);

    void beforeUpdate(GameState gameState);

    List<Order> doTurn();

    String afterUpdate();

    CalcState getCalcState();
}
