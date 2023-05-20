package org.example;

import java.util.HashSet;
import java.util.Set;

/** reverse powerset lattice ordered by reverse (bottom is the universe set). */
class ReversePowersetLattice<T> implements CompleteUpperSemiLattice<Set<T>> {
    Set<T> universe;

    ReversePowersetLattice(Set<T> universe) {
        this.universe = universe;
    }

    @Override
    public Set<T> join(Set<Set<T>> elements) {
        var res = new HashSet<>(this.universe);
        for (Set<T> element : elements) {
            res.retainAll(element);
        }
        return res;
    }
}
