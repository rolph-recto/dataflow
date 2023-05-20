package org.example;

import java.util.Set;

enum SecurityLevel { PUBLIC, SECRET }

/** trivial two-point lattice of security labels. */
public class SecurityLattice implements CompleteUpperSemiLattice<SecurityLevel> {
    @Override
    public SecurityLevel join(Set<SecurityLevel> elements) {
        var cur = SecurityLevel.PUBLIC;
        for (SecurityLevel element : elements) {
            cur = element == SecurityLevel.SECRET ? SecurityLevel.SECRET : cur;
        }
        return cur;
    }
}
