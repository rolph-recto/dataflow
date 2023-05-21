package org.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A complete upper semi-lattice (and any set has a join; the bottom element is the join of the empty set). */
interface CompleteUpperSemiLattice<T> {
    T join(Set<T> elements);

    default T join(T element1, T element2) {
        return this.join(new HashSet<>(List.of(element1, element2)));
    }

    default T bottom() {
        return this.join(new HashSet<>());
    }
}
