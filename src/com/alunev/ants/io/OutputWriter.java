package com.alunev.ants.io;

import java.io.PrintStream;
import java.util.List;

import com.alunev.ants.mechanics.Order;

public class OutputWriter {
    private final PrintStream outputStream;

    public OutputWriter(PrintStream outputStream) {
        this.outputStream = outputStream;

    }

    public void writeOrders(List<Order> orders) {
        for (Order order : orders) {
            outputStream.println(order);
        }
        outputStream.flush();
    }
}
