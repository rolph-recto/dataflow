package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** lattice where elements are maps from variables to elements of another lattice. */
class StoreLattice<T, L extends CompleteUpperSemiLattice<T>> implements CompleteUpperSemiLattice<Map<String,T>> {
    L lattice;
    Set<String> variables;

    StoreLattice(L lattice, Set<String> variables) {
        this.lattice = lattice;
        this.variables = variables;
    }

    @Override
    public Map<String, T> join(Set<Map<String, T>> elements) {
        HashMap<String, T> res = new HashMap<>();
        for (var v : this.variables) {
            res.put(v, this.lattice.bottom());
        }

        for (var element : elements) {
            for (var v : this.variables) {
                if (element.containsKey(v)) {
                    res.put(v, this.lattice.join(res.get(v), element.get(v)));

                } else {
                    throw new RuntimeException(String.format("store element missing variable %s", v));
                }
            }
        }

        return res;
    }
}
