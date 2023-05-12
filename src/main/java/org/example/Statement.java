package org.example;

import java.util.Vector;

abstract class Statement {
    abstract <T> T visit(StatementVisitor<T> visitor);
}

class Assign extends Statement {
    String var;
    Expression rhs;

    Assign(String var, Expression rhs) {
        this.var = var;
        this.rhs = rhs;
    }

    @Override
    <T> T visit(StatementVisitor<T> v) {
        return v.visit(this);
    }
}

class Conditional extends Statement {
    Expression guard;
    Vector<Statement> thenBranch;
    Vector<Statement> elseBranch;

    Conditional(Expression guard, Vector<Statement> thenBranch, Vector<Statement> elseBranch) {
        this.guard = guard;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    <T> T visit(StatementVisitor<T> v) {
        return v.visit(this);
    }
}

class While extends Statement {
    Expression guard;
    Vector<Statement> body;

    While(Expression guard, Vector<Statement> body) {
        this.guard = guard;
        this.body = body;
    }

    @Override
    <T> T visit(StatementVisitor<T> v) {
        return v.visit(this);
    }
}
