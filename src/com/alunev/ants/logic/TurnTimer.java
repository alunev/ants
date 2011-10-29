package com.alunev.ants.logic;

public class TurnTimer {
    private final long turnTime;
    private final long epsilon;

    private long startTurnTime = 0;
    private long lapActionTime = 0;

    public TurnTimer(long turnTime, long epsilon) {
        this.turnTime = turnTime;
        this.epsilon = epsilon;
        this.startTurnTime = System.currentTimeMillis();
    }

    public long timePassed() {
        return System.currentTimeMillis() - startTurnTime;
    }

    public long lapTime() {
        long current = System.currentTimeMillis();
        long lapTime = System.currentTimeMillis() - lapActionTime;

        this.lapActionTime = current;

        return lapTime;
    }

    public boolean giveUp() {
        return (turnTime - timePassed()) < epsilon;
    }
}
