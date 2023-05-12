package org.example;

abstract class Expression {
    abstract <T> T accept(ExpressionVisitor<T> visitor);
}

class Literal extends Expression {
    int val;

    Literal(int val) {
        this.val = val;
    }

    @Override
    <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Var extends Expression {
    String name;

    Var(String name) {
        this.name = name;
    }

    @Override
    <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Add extends Expression {
    Expression lhs;
    Expression rhs;

    Add(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Multiply extends Expression {
    Expression lhs;
    Expression rhs;

    Multiply(Expression lhs, Expression rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
