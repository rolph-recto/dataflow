package org.example;

import java.util.HashSet;
import java.util.Set;

/** Return the set of variables referenced in an expression. */
class ExpressionVariables implements ExpressionVisitor<Set<String>> {
    @Override
    public Set<String> visit(Literal expr) {
        return new HashSet<>();
    }

    @Override
    public Set<String> visit(Add expr) {
        var res = new HashSet<String>();
        res.addAll(expr.lhs.accept(this));
        res.addAll(expr.rhs.accept(this));
        return res;
    }

    @Override
    public Set<String> visit(Multiply expr) {
        var res = new HashSet<String>();
        res.addAll(expr.lhs.accept(this));
        res.addAll(expr.rhs.accept(this));
        return res;
    }

    @Override
    public Set<String> visit(Var expr) {
        var res = new HashSet<String>();
        res.add(expr.name);
        return res;
    }
}
