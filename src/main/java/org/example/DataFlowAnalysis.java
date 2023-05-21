package org.example;

import com.google.common.collect.HashBiMap;

import java.util.HashMap;
import java.util.Map;

enum DataFlowDirection { FORWARD, BACKWARD }

/** abstract dataflow analysis for a control flow graph. */
abstract class DataFlowAnalysis<T, L extends CompleteUpperSemiLattice<T>> implements TransferFunction<T> {
    L lattice;
    DataFlowDirection direction;
    ControlFlowGraph cfg;
    HashBiMap<Integer, DataFlowVariable> blockVars;
    FixpointSolver solver;

    DataFlowAnalysis(L lattice, ControlFlowGraph cfg, DataFlowDirection direction) {
        this.lattice = lattice;
        this.direction = direction;
        this.cfg = cfg;
        this.blockVars = HashBiMap.create();
        this.solver = new FixpointSolver();

        initializeSolver();
    }

    DataFlowAnalysis(L lattice, ControlFlowGraph cfg) {
        this(lattice, cfg, DataFlowDirection.FORWARD);
    }

    /* create dataflow variables for CFG. */
    private void initializeSolver() {
        for (int blockId : this.cfg.blockMap.keySet()) {
            var dfVar = solver.freshVariable();
            this.blockVars.put(blockId, dfVar);
        }

        // add dependency information between dataflow variables
        for (Map.Entry<Integer, BasicBlock> kv: this.cfg.blockMap.entrySet()) {
            var blockDfVar = this.blockVars.get(kv.getKey());

            for (int target : kv.getValue().jump.possibleTargets()) {
                var targetDfVar = this.blockVars.get(target);
                switch (this.direction) {
                    case FORWARD -> this.solver.addDependency(blockDfVar, targetDfVar);
                    case BACKWARD -> this.solver.addDependency(targetDfVar, blockDfVar);
                }
            }
        }
    }

    /** Compute dataflow analysis. */
    HashMap<Integer, T> analyze() {
        // for (Map.Entry<Integer,DataFlowVariable> kv : this.blockVars.entrySet()) {
        //     System.out.printf("block %d => %s\n", kv.getKey(), kv.getValue());
        // }

        var varSolution = this.solver.solve(this.lattice, this);
        var solution = new HashMap<Integer, T>();
        for (Map.Entry<DataFlowVariable, T> kv : varSolution.entrySet()) {
            solution.put(this.blockVars.inverse().get(kv.getKey()), kv.getValue());
        }

        return solution;
    }

    @Override
    public T transfer(DataFlowVariable dfVar, T input) {
        var block = this.cfg.blockMap.get(this.blockVars.inverse().get(dfVar));

        if (block.id == this.cfg.entryBlock) {
            assert(block.statements.size() == 0);
            return entry();
        }

        // assume that CFG blocks are atomic (contains either 0 or 1 statements)
        if (block.statements.size() == 0) {
            // if there is a conditional jump, apply transfer function of the guard
            if (block.jump instanceof ConditionalJump) {
                return transfer(((ConditionalJump)block.jump).guard, input);

            } else {
                return input;
            }

        } else if (block.statements.size() == 1) {
            return transfer(block.statements.element(), input);

        } else {
            throw new RuntimeException("CFG used in dataflow analysis must be atomic");
        }
    }

    /** Value for the entry block. By default, this is lattice.bottom(). */
    T entry() {
        return this.lattice.bottom();
    }

    /** Transfer function for a statement. */
    abstract T transfer(AtomicStatement statement, T input);

    /** Transfer function for a conditional / loop guard. */
    abstract T transfer(Expression guard, T input);
}
