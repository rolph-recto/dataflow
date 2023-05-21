package org.example;

import java.util.Set;

enum Sign { NO_SIGN, UNKNOWN, POS, NEG, ZERO }

public class SignLattice implements CompleteUpperSemiLattice<Sign> {
    @Override
    public Sign join(Sign element1, Sign element2) {
        if (element1 == Sign.UNKNOWN || element2 == Sign.UNKNOWN) {
            return Sign.UNKNOWN;

        } else if (element1 == Sign.NO_SIGN) {
            return element2;

        } else if (element2 == Sign.NO_SIGN) {
            return element1;

        } else if (element1 == element2) {
            return element1;

        } else {
            return Sign.UNKNOWN;
        }
    }

    @Override
    public Sign join(Set<Sign> elements) {
        if (elements.size() == 0) {
            return Sign.NO_SIGN;

        } else if (elements.size() == 1) {
            return elements.iterator().next();

        } else {
            return elements.stream().reduce(Sign.NO_SIGN, this::join);
        }
    }
}
