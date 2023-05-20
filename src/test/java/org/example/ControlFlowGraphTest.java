package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControlFlowGraphTest {
    @Test
    public void testBasicBlockCFGBuilder() {
        ArrayList<Statement> program = new ArrayList<>();
        program.add(new Assign("x", new Literal(1)));

        program.add(
            new Conditional(
                new Literal(1),
                new Block(new ArrayList<>(List.of(new Assign("y", new Literal(0))))),
                new Block(new ArrayList<>(List.of(new Assign("y", new Literal(1)))))
            )
        );

        var cfg = new BasicBlockCFGBuilder().buildCFG(new Block(program));

        System.out.println(cfg.toString());

        assertEquals(4, cfg.blockMap.size());
    }

    @Test
    public void testAtomicBlockCFGBuilder() {
        ArrayList<Statement> program = new ArrayList<>();
        program.add(new Assign("x", new Literal(1)));
        program.add(new Assign("z", new Literal(1)));

        program.add(
            new Conditional(
                new Literal(1),
                new Block(new ArrayList<>(List.of(new Assign("y", new Literal(0))))),
                new Block(new ArrayList<>(List.of(new Assign("y", new Literal(1)))))
            )
        );

        var cfg = new AtomicCFGBuilder().buildCFG(new Block(program));

        System.out.println(cfg.toString());

        assertEquals(6, cfg.blockMap.size());
    }
}