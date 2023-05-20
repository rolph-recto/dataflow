package org.example;

import java.util.Set;

/** A complete upper semi-lattice (and any set has a join; the bottom element is the join of the empty set). */
interface CompleteUpperSemiLattice<T> {
    T join(Set<T> elements);
}
