package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** computes the sign of variables at specific program points. */
public class SignAnalysis extends BasicDataFlowAnalysis<Map<String, Sign>, StoreLattice<Sign, SignLattice>> {
    SignAnalysis(ControlFlowGraph cfg) {
        super(new StoreLattice<>(new SignLattice(), cfgVariables(cfg)), cfg, DataFlowDirection.FORWARD);
    }

    private static Set<String> cfgVariables(ControlFlowGraph cfg) {
        var variables = new HashSet<String>();
        var stmtVisitor = new StatementVariables();

        for (var block : cfg.blockMap.values()) {
            for (var stmt : block.statements) {
                variables.addAll(stmt.accept(stmtVisitor));
            }
        }

        return variables;
    }

    private Sign eval(Map<String, Sign> store, Expression expr) {
        return expr.accept(new EvalVisitor(store));
    }

    @Override
    Map<String, Sign> transfer(AtomicStatement statement, Map<String, Sign> input) {
        if (statement instanceof Assign assign) {
            var output = new HashMap<>(input);
            output.put(assign.var, eval(input, assign.rhs));
            return output;

        } else {
            throw new RuntimeException("unknown statement variant");
        }
    }

    @Override
    Map<String, Sign> transfer(Expression expr, Map<String, Sign> input) {
        // conditional / loop guards don't change the abstract state, just propagate input
        return input;
    }

    private static class EvalVisitor implements ValueExpressionVisitor<Sign> {
        Map<String, Sign> store;

        EvalVisitor(Map<String, Sign> store) {
            this.store = store;
        }

        @Override
        public Sign visitInput() {
            return Sign.UNKNOWN;
        }

        @Override
        public Sign visitLiteral(int value) {
            if (value == 0) {
                return Sign.ZERO;

            } else if (value > 0) {
                return Sign.POS;

            } else {
                return Sign.NEG;
            }
        }

        @Override
        public Sign visitVar(String name) {
            assert(this.store.containsKey(name));
            return this.store.get(name);
        }

        @Override
        public Sign visitAdd(Sign lhs, Sign rhs) {
            if (lhs == Sign.NO_SIGN) {
                return rhs;

            } else if (rhs == Sign.NO_SIGN) {
                return lhs;

            } else if (lhs == Sign.ZERO && rhs == Sign.ZERO) {
                return Sign.ZERO;

            } else if ((lhs == Sign.POS || lhs == Sign.ZERO) && (rhs == Sign.POS || rhs == Sign.ZERO)) {
                return Sign.POS;

            } else if ((lhs == Sign.NEG || lhs == Sign.ZERO) && (rhs == Sign.NEG || rhs == Sign.ZERO)) {
                return Sign.NEG;

            } else {
                return Sign.UNKNOWN;
            }
        }

        @Override
        public Sign visitMultiply(Sign lhs, Sign rhs) {
            if (lhs == Sign.NO_SIGN) {
                return rhs;

            } else if (rhs == Sign.NO_SIGN) {
                return lhs;

            } else if (lhs == Sign.ZERO || rhs == Sign.ZERO) {
                return Sign.ZERO;

            } else if ((lhs == Sign.POS && rhs == Sign.POS) || (lhs == Sign.NEG && rhs == Sign.NEG)) {
                return Sign.POS;

            } else if ((lhs == Sign.POS && rhs == Sign.NEG) || (lhs == Sign.NEG && rhs == Sign.POS)) {
                return Sign.NEG;

            } else {
                return Sign.UNKNOWN;
            }
        }
    }
}
