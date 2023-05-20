package org.example;

import org.junit.jupiter.api.Test;

import java.util.*;

class DataFlowAnalysisTest {
    static Block program1 =
        new Block(new ArrayList<>(List.of(new Statement[] {
            new Assign("x", new Literal(1)),
            new Conditional(
                new Literal(1),
                new Block(new ArrayList<>(List.of(new Assign("y", new Literal(0))))),
                new Block(new ArrayList<>(List.of(new Assign("y", new Var("x")))))
            ),
            new Assign("x", new Var("y"))
        })));

    @Test
    public void testLivenessAnalysis() {
        var cfg = new AtomicCFGBuilder().buildCFG(program1);
        var livenessAnalysis = new LivenessAnalysis(cfg);
        Map<Integer, Set<String>> solution = livenessAnalysis.analyze();

        for (BasicBlock block : cfg.blockList()) {
            System.out.printf("%d => %s\n%s\n\n", block.id, solution.get(block.id), block);
        }
    }
}