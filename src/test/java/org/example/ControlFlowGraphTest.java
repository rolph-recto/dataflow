package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

class ControlFlowGraphTest {
    @Test
    public void testCFGCreation() {
        Vector<Statement> program = new Vector<>();
        program.add(new Assign("x", new Literal(1)));

        program.add(
            new Conditional(
                new Literal(1),
                new Vector<>(List.of(new Assign("y", new Literal(0)))),
                new Vector<>(List.of(new Assign("y", new Literal(1))))
            )
        );

        var cfg = new ControlFlowGraph(program);
        assertEquals(cfg.blockMap.size(), 4);
    }
}