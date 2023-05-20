package org.example;

import org.junit.jupiter.api.Test;

import java.util.*;

class DataFlowAnalysisTest {
    @Test
    public void testLivenessAnalysis() {
        var program = new Block(new ArrayList<>(List.of(new Statement[] {
            new Assign("x", new Literal(1)),
            new Conditional(
                new Literal(1),
                new Block(new ArrayList<>(List.of(new Assign("y", new Literal(0))))),
                new Block(new ArrayList<>(List.of(new Assign("y", new Var("x")))))
            ),
            new Assign("x", new Var("y"))
        })));

        var cfg = new AtomicCFGBuilder().buildCFG(program);
        var livenessAnalysis = new LivenessAnalysis(cfg);
        Map<Integer, Set<String>> solution = livenessAnalysis.analyze();

        for (Map.Entry<Integer,BasicBlock> kv: cfg.blockMap.entrySet()) {
            System.out.printf("%d => %s\n%s\n\n", kv.getKey(), solution.get(kv.getKey()), kv.getValue());
        }
    }
}