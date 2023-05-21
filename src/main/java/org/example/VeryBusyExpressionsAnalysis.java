package org.example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Backwards must-analysis that computes for every program point expressions that will definitely be computed
 * again in the future. Similar to available expressions analysis, but backwards! */
class VeryBusyExpressionsAnalysis extends DataFlowAnalysis<Set<Expression>,ReversePowersetLattice<Expression>> {
    VeryBusyExpressionsAnalysis(ControlFlowGraph cfg) {
        super(new ReversePowersetLattice<>(programExpressions(cfg)), cfg, DataFlowDirection.BACKWARD);
    }

    private static Set<Expression> programExpressions(ControlFlowGraph cfg) {
        var exprs = new HashSet<Expression>();
        for (BasicBlock block : cfg.blockMap.values()) {
            for (var stmt : block.statements) {
                exprs.addAll(stmt.accept(new ComplexExpressions()));
            }
        }

        return exprs;
    }

    @Override
    Set<Expression> initial() {
        return new HashSet<>();
    }

    @Override
    Set<Expression> transfer(AtomicStatement statement, Set<Expression> input) {
        if (statement instanceof Assign assign) {
            // all complex expressions on the RHS of the assignment have been computed for the assignment,
            // and thus are available after the assignment
            var res = new HashSet<>(input);

            // must remove all expressions that use the newly assigned variable, since those expressions are now stale
            Iterator<Expression> iter = res.iterator();
            while (iter.hasNext()) {
                Expression expr = iter.next();

                if (expr.accept(new ExpressionVariables()).contains(assign.var)) {
                    iter.remove();
                }
            }

            // finally, add all expressions computed from the RHS
            res.addAll(assign.rhs.accept(new ComplexExpressions()));

            return res;

        } else {
            throw new RuntimeException("unreachable");
        }
    }

    @Override
    Set<Expression> transfer(Expression guard, Set<Expression> input) {
        var res = new HashSet<>(input);
        res.addAll(guard.accept(new ComplexExpressions()));
        return res;
    }
}
