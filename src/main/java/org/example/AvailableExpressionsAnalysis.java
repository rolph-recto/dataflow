package org.example;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** Forward must-analysis that computes expressions that are *definitely* available at a program point. */
class AvailableExpressionsAnalysis extends DataFlowAnalysis<Set<Expression>, ReversePowersetLattice<Expression>> {
    AvailableExpressionsAnalysis(ControlFlowGraph cfg) {
        super(new ReversePowersetLattice<>(programExpressions(cfg)), cfg, DataFlowDirection.FORWARD);
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
    Set<Expression> entry() {
        // no expressions are available at entry
        return new HashSet<>();
    }

    @Override
    Set<Expression> transfer(AtomicStatement statement, Set<Expression> input) {
        if (statement instanceof Assign assign) {
            // all complex expressions on the RHS of the assignment have been computed for the assignment,
            // and thus are available after the assignment
            var res = new HashSet<>(input);
            res.addAll(assign.rhs.accept(new ComplexExpressions()));

            // must remove all expressions that use the newly assigned variable, since those expressions are now stale
            Iterator<Expression> iter = res.iterator();
            while (iter.hasNext()) {
                Expression expr = iter.next();

                if (expr.accept(new ExpressionVariables()).contains(assign.var)) {
                    iter.remove();
                }
            }

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
