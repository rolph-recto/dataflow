package org.example;

interface ExpressionVisitor<T> {
    T visit(Literal expr);
    T visit(Add expr);
    T visit(Multiply expr);
    T visit(Var expr);
}

/** base class that contains abstract traversal logic for an expression visitor. */
interface ValueExpressionVisitor<T> extends ExpressionVisitor<T> {
    T visitLiteral(int value);
    T visitVar(String name);
    T visitAdd(T lhs, T rhs);
    T visitMultiply(T lhs, T rhs);

    @Override
    default T visit(Literal expr) {
        return visitLiteral(expr.val);
    }

    @Override
    default T visit(Var expr) {
        return visitVar(expr.name);
    }

    @Override
    default T visit(Add expr) {
        return visitAdd(expr.lhs.accept(this), expr.rhs.accept(this));
    }

    @Override
    default T visit(Multiply expr) {
        return visitMultiply(expr.lhs.accept(this), expr.rhs.accept(this));
    }
}