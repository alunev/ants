package com.alunev.ants.bot;

import java.util.List;

import com.alunev.ants.io.GameSetup;
import com.alunev.ants.io.GameState;
import com.alunev.ants.mechanics.Order;

public interface Bot {
    void beforeStart(GameSetup gameSetup);

    void beforeUpdate(GameState gameState);

    void afterUpdate();

    List<Order> doTurn();
}
