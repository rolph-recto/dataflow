package org.example;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class InformationFlowEvalVisitor implements ValueExpressionVisitor<SecurityLevel> {
    Map<String, SecurityLevel> store;

    InformationFlowEvalVisitor(Map<String, SecurityLevel> store) {
        this.store = store;
    }

    @Override
    public SecurityLevel visitInput() {
        return SecurityLevel.SECRET;
    }

    @Override
    public SecurityLevel visitLiteral(int value) {
        return SecurityLevel.PUBLIC;
    }

    @Override
    public SecurityLevel visitVar(String name) {
        assert(this.store.containsKey(name));
        return this.store.get(name);
    }

    @Override
    public SecurityLevel visitAdd(SecurityLevel lhs, SecurityLevel rhs) {
        return new SecurityLattice().join(lhs, rhs);
    }

    @Override
    public SecurityLevel visitMultiply(SecurityLevel lhs, SecurityLevel rhs) {
        return new SecurityLattice().join(lhs, rhs);
    }
}

/** Forward may-analysis that checks the security levels of computations.
 *  This is similar to a sign analysis.
 *  This only captures direct flows; it does not have the standard PC label machinery for
 *  implicit flows through control. */
class InformationFlowAnalysis extends BasicDataFlowAnalysis<Map<String,SecurityLevel>, StoreLattice<SecurityLevel, SecurityLattice>>
{
    InformationFlowAnalysis(ControlFlowGraph cfg) {
        super(new StoreLattice<>(new SecurityLattice(), programVariables(cfg)), cfg, DataFlowDirection.FORWARD);
    }

    private static Set<String> programVariables(ControlFlowGraph cfg) {
        var res = new HashSet<String>();
        for (var block : cfg.blockMap.values()) {
            for (var statement : block.statements) {
                res.addAll(statement.accept(new StatementVariables()));
            }
        }

        return res;
    }

    @Override
    Map<String, SecurityLevel> transfer(AtomicStatement statement, Map<String, SecurityLevel> input) {
        if (statement instanceof Assign assign) {
            var res = new HashMap<>(input);
            res.put(assign.var, assign.rhs.accept(new InformationFlowEvalVisitor(input)));
            return res;

        } else if (statement instanceof Output output) {
            SecurityLevel level = output.expr.accept(new InformationFlowEvalVisitor(input));
            return input;

        } else {
            throw new RuntimeException("unreachable");
        }
    }

    @Override
    Map<String, SecurityLevel> transfer(Expression guard, Map<String, SecurityLevel> input) {
        return input;
    }
}

/** Check if information about inputs (deemed secret) have been leaked to an output (deemed public). */
class InformationFlowChecker {
    static boolean check(ControlFlowGraph cfg) {
        var ifcAnalysis = new InformationFlowAnalysis(cfg);
        var solution = ifcAnalysis.analyze();

        for (var block : cfg.blockMap.values()) {
            if (block.statements.size() == 1) {
                if (block.statements.getFirst() instanceof Output output) {
                    // this technically uses the store *after* output is executed, but since
                    // output does not change the store anyway it does not matter
                    var store = solution.get(block.id);
                    var level = output.expr.accept(new InformationFlowEvalVisitor(store));
                    if (level == SecurityLevel.SECRET) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
