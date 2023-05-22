package org.example;

import java.util.HashSet;
import java.util.Set;

/** Backwards may-analysis that computes live variables at every program point. */
class LivenessAnalysis extends BasicDataFlowAnalysis<Set<String>, PowersetLattice<String>> {
    LivenessAnalysis(ControlFlowGraph cfg) {
        super(new PowersetLattice<>(), cfg, DataFlowDirection.BACKWARD);
    }

    @Override
    Set<String> transfer(AtomicStatement statement, Set<String> input) {
        if (statement instanceof Assign assign) {
            var res = new HashSet<>(input);
            res.remove(assign.var);
            res.addAll(assign.rhs.accept(new ExpressionVariables()));
            return res;

        } else if (statement instanceof Output output) {
            var res = new HashSet<>(input);
            res.addAll(output.expr.accept(new ExpressionVariables()));
            return res;

        } else {
            throw new RuntimeException("unknown statement type");
        }
    }

    @Override
    Set<String> transfer(Expression guard, Set<String> input) {
        var output = new HashSet<>(input);
        output.addAll(guard.accept(new ExpressionVariables()));
        return output;
    }
}
