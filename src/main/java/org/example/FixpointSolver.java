package org.example;

import java.util.*;
import java.util.stream.Collectors;

/** Transfer function for dataflow variables. */
interface TransferFunction<T> {
    T transfer(DataFlowVariable var, T input);
}

class DataFlowVariable {
    private final int id;

    DataFlowVariable(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }

        DataFlowVariable tother = (DataFlowVariable)other;
        return this.id == tother.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return String.format("var(%d)", this.id);
    }
}

/** Computes fixed points of lattice equations. */
class FixpointSolver {
    int nextVariableId;

    // maps variables to dependencies
    HashMap<DataFlowVariable, HashSet<DataFlowVariable>> children;

    // maps variables to variable to which it depends
    HashMap<DataFlowVariable, HashSet<DataFlowVariable>> parents;

    FixpointSolver() {
        this.nextVariableId = 1;
        this.children = new HashMap<>();
        this.parents = new HashMap<>();
    }

    /** create and return a fresh dataflow variable. */
    DataFlowVariable freshVariable() {
        var dfVar = new DataFlowVariable(this.nextVariableId);
        this.children.put(dfVar, new HashSet<>());
        this.parents.put(dfVar, new HashSet<>());

        this.nextVariableId += 1;
        return dfVar;
    }

    /** add a dependency from {@param fromVar} to {@param toVar}. */
    void addDependency(DataFlowVariable fromVar, DataFlowVariable toVar) {
        assert(this.children.containsKey(fromVar) && this.parents.containsKey(toVar));

        this.children.get(fromVar).add(toVar);
        this.parents.get(toVar).add(fromVar);
    }

    /** Iterative algorithm to compute least fixpoint of equations over {@param lattice} defined by {@param transferFunction}. */
    <T, L extends CompleteUpperSemiLattice<T>>
    HashMap<DataFlowVariable, T> solve(L lattice, TransferFunction<T> transferFunction) {
        HashMap<DataFlowVariable, T> solution = new HashMap<>();
        LinkedList<DataFlowVariable> worklist = new LinkedList<>();

        // initialize all variable solutions to bottom
        for (DataFlowVariable dfVar : this.children.keySet()) {
            // the join of the empty set equals bottom
            solution.put(dfVar, lattice.join(new HashSet<>()));
            worklist.addLast(dfVar);
        }

        while (worklist.size() > 0) {
            DataFlowVariable dfVar = worklist.removeFirst();

            T input = lattice.join(
                this.parents.get(dfVar).stream().map(solution::get).collect(Collectors.toSet())
            );

            T output = transferFunction.transfer(dfVar, input);

            // update solution, add dependencies to worklist
            if (!solution.get(dfVar).equals(output)) {
                solution.put(dfVar, output);

                for (DataFlowVariable child : this.children.get(dfVar)) {
                    worklist.addLast(child);
                }
            }
        }

        return solution;
    }
}
