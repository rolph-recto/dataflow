package org.example;

import java.util.HashSet;
import java.util.Set;

/** powerset lattice ordered by inclusion (bottom is the empty set). */
class PowersetLattice<T> implements CompleteUpperSemiLattice<Set<T>> {
    @Override
    public Set<T> join(Set<Set<T>> elements) {
        var res = new HashSet<T>();
        for (Set<T> element : elements) {
            res.addAll(element);
        }
        return res;
    }
}
