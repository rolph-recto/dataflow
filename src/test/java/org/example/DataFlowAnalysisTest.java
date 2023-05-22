package org.example;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

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

    static Block program2 =
        new Block(new ArrayList<>(List.of(new Statement[] {
            new Assign("z", new Add(new Var("a"), new Var("b"))),
            new Assign("y", new Multiply(new Var("a"), new Var("b"))),
            new While(
                new Add(new Var("y"), new Add(new Var("a"), new Var("b"))),
                new Block(new ArrayList<>(List.of(new Statement[]{
                    new Assign("a", new Add(new Var("a"), new Literal(1))),
                    new Assign("x", new Add(new Var("a"), new Var("b"))),
                })))
            )
        })));

    static Block program3 =
        new Block(new ArrayList<>(List.of(new Statement[] {
            new Assign("a", new Add(new Var("x"), new Literal(1))),
            new Assign("b", new Add(new Var("x"), new Literal(2))),
            new While(
                new Add(new Var("x"), new Literal(0)),
                new Block(new ArrayList<>(List.of(new Statement[]{
                    new Output(new Add(new Multiply(new Var("a"), new Var("b")), new Var("x"))),
                    new Assign("x", new Add(new Var("x"), new Literal(1))),
                })))
            ),
            new Output(new Multiply(new Var("a"), new Var("b")))
        })));

    static Block program4 =
        new Block(new ArrayList<>(List.of(new Statement[] {
            new Assign("x", new Literal(5)),
            new While(
                new Add(new Var("x"), new Literal(1)),
                new Block(new ArrayList<>(List.of(new Statement[]{
                    new Assign("y", new Add(new Var("x"), new Literal(2))),
                    new Conditional(
                        new Add(new Var("y"), new Literal(3)),
                        new Block(new ArrayList<>(List.of(new Statement[] {
                            new Assign("x", new Add(new Var("x"), new Var("y")))
                        }))),
                        new Block(new ArrayList<>())
                    ),
                    new Assign("z", new Add(new Var("x"), new Literal(4))),
                    new Conditional(
                        new Add(new Var("z"), new Literal(0)),
                        new Block(new ArrayList<>(List.of(new Statement[] {
                            new Assign("x", new Add(new Var("x"), new Literal(2)))
                        }))),
                        new Block(new ArrayList<>())
                    ),
                    new Assign("z", new Add(new Var("x"), new Literal(1)))
                })))
            ),
            new Output(new Var("x"))
        })));

    private
    <T, L extends CompleteUpperSemiLattice<T>>
    void testAnalysis(Block program, Function<ControlFlowGraph,DataFlowAnalysis<T,L>> analysisBuilder) {
        var cfg = new AtomicCFGBuilder().buildCFG(program);
        var analysis = analysisBuilder.apply(cfg);
        var solution = analysis.analyze();

        for (BasicBlock block : cfg.blockList()) {
            System.out.printf("%d => %s\n%s\n\n", block.id, solution.get(block.id), block);
        }
    }

    @Test
    public void testLivenessAnalysis() {
        testAnalysis(program1, LivenessAnalysis::new);
    }

    @Test
    public void testSignAnalysis() {
        testAnalysis(program1, SignAnalysis::new);
    }

    @Test
    public void testAvailableExpressionsAnalysis() {
        testAnalysis(program2, AvailableExpressionsAnalysis::new);
    }

    @Test
    public void testVeryBusyExpressionsAnalysis() {
        testAnalysis(program3, VeryBusyExpressionsAnalysis::new);
    }

    @Test
    public void testReachingDefinitionsAnalysis() {
        testAnalysis(program4, ReachingDefinitionsAnalysis::new);
    }
}