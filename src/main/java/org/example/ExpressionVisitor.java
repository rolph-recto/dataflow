package org.example;

interface ExpressionVisitor<T> {
    T visit(Literal expr);
    T visit(Add expr);
    T visit(Multiply expr);
    T visit(Var expr);
}
