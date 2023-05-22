package org.example;

import java.util.*;

class Definition {
    int id;
    String var;
    Expression rhs;

    Definition(int id, String var, Expression rhs) {
        this.id = id;
        this.var = var;
        this.rhs = rhs;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other instanceof Definition otherDef) {
            return Objects.equals(this.id, otherDef.id)
                && Objects.equals(this.var, otherDef.var)
                && Objects.equals(this.rhs, otherDef.rhs);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.var, this.rhs);
    }

    @Override
    public String toString() {
        return String.format("def(%d, %s, %s)", this.id, this.var, this.rhs);
    }
}

class ReachingDefinitionsAnalysis extends DataFlowAnalysis<Set<Definition>, PowersetLattice<Definition>> {
    ReachingDefinitionsAnalysis(ControlFlowGraph cfg) {
        super(new PowersetLattice<>(), cfg, DataFlowDirection.FORWARD);
    }

    @Override
    Set<Definition> transfer(BasicBlock block, Set<Definition> input) {
        // assume that CFG blocks are atomic (contains either 0 or 1 statements)
        if (block.statements.size() == 0) {
            // if there is a conditional jump, apply transfer function of the guard
            if (block.jump instanceof ConditionalJump) {
                return transfer(((ConditionalJump)block.jump).guard, input);

            } else {
                return input;
            }

        } else if (block.statements.size() == 1) {
            return transfer(block.id, block.statements.element(), input);

        } else {
            throw new RuntimeException("CFG used in dataflow analysis must be atomic");
        }
    }

    private Set<Definition> transfer(int blockId, AtomicStatement statement, Set<Definition> input) {
        if (statement instanceof Assign assign) {
            var res = new HashSet<Definition>();

            for (var def : input) {
                if (!def.var.equals(assign.var)) {
                    res.add(def);
                }
            }

            res.add(new Definition(blockId, assign.var, assign.rhs));
            return res;

        } else if (statement instanceof Output) {
            return input;

        } else {
            throw new RuntimeException("unreachable");
        }
    }
}