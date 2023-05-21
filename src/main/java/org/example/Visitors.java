package org.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Return the set of variables referenced in an expression. */
class ExpressionVariables implements ValueExpressionVisitor<Set<String>> {
    @Override
    public Set<String> visitLiteral(int value) {
        return new HashSet<>();
    }

    @Override
    public Set<String> visitVar(String name) {
        var res = new HashSet<String>();
        res.add(name);
        return res;
    }

    @Override
    public Set<String> visitAdd(Set<String> lhs, Set<String> rhs) {
        var res = new HashSet<String>();
        res.addAll(lhs);
        res.addAll(rhs);
        return res;
    }

    @Override
    public Set<String> visitMultiply(Set<String> lhs, Set<String> rhs) {
        var res = new HashSet<String>();
        res.addAll(lhs);
        res.addAll(rhs);
        return res;
    }
}

class StatementVariables extends ExpressionVariables implements ValueStatementVisitor<Set<String>, Set<String>> {
    @Override
    public Set<String> visitAssign(String var, Set<String> rhs) {
        var res = new HashSet<String>();
        res.add(var);
        res.addAll(rhs);
        return res;
    }

    @Override
    public Set<String> visitConditional(Set<String> guard, Set<String> thenBranch, Set<String> elseBranch) {
        var res = new HashSet<String>();
        res.addAll(guard);
        res.addAll(thenBranch);
        res.addAll(elseBranch);
        return res;
    }

    @Override
    public Set<String> visitWhile(Set<String> guard, Set<String> body) {
        var res = new HashSet<String>();
        res.addAll(guard);
        res.addAll(body);
        return res;
    }

    @Override
    public Set<String> visitBlock(List<Set<String>> statements) {
        var res = new HashSet<String>();
        for (var child : statements) {
            res.addAll(child);
        }
        return res;
    }
}