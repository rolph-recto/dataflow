package org.example;

import java.util.List;
import java.util.stream.Collectors;

interface AtomicStatementVisitor<T> {
    T visit(Assign stmt);
    T visit(Output stmt);
}

interface StatementVisitor<T> extends AtomicStatementVisitor<T> {
    T visit(Conditional stmt);
    T visit(While stmt);
    T visit(Block stmt);
}

interface ValueAtomicStatementVisitor<S, E> extends AtomicStatementVisitor<S>, ValueExpressionVisitor<E> {
    S visitAssign(String var, E rhs);

    S visitOutput(E rhs);
}

interface ValueStatementVisitor<S, E> extends ValueAtomicStatementVisitor<S, E>, StatementVisitor<S> {
    S visitConditional(E guard, S thenBranch, S elseBranch);

    S visitWhile(E guard, S body);

    S visitBlock(List<S> statements);

    @Override
    default S visit(Assign stmt) {
        return visitAssign(stmt.var, stmt.rhs.accept(this));
    }

    @Override
    default S visit(Output stmt) {
        return visitOutput(stmt.expr.accept(this));
    }

    @Override
    default S visit(Conditional stmt) {
        return visitConditional(
            stmt.guard.accept(this),
            stmt.thenBranch.accept(this),
            stmt.elseBranch.accept(this)
        );
    }

    @Override
    default S visit(While stmt) {
        return visitWhile(
            stmt.guard.accept(this),
            stmt.body.accept(this)
        );
    }

    @Override
    default S visit(Block stmt) {
        return visitBlock(
            stmt.statements.stream()
                .map(child -> child.accept(this))
                .collect(Collectors.toList())
        );
    }
}