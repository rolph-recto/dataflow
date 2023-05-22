package org.example;

import java.util.ArrayList;

abstract class Statement {
    abstract <T> T accept(StatementVisitor<T> visitor);
}

abstract class AtomicStatement extends Statement {}

class Assign extends AtomicStatement {
    String var;
    Expression rhs;

    Assign(String var, Expression rhs) {
        this.var = var;
        this.rhs = rhs;
    }

    @Override
    <T> T accept(StatementVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s := %s", this.var, this.rhs);
    }
}

class Output extends AtomicStatement {
    Expression expr;

    Output(Expression expr) {
        this.expr = expr;
    }

    @Override
    <T> T accept(StatementVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return String.format("output(%s)", this.expr);
    }
}

class Block extends Statement {
    ArrayList<Statement> statements;

    Block(ArrayList<Statement> statements) {
        this.statements = statements;
    }

    @Override
    <T> T accept(StatementVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (Statement s : this.statements) {
            if (builder.length() > 0) {
                builder.append(";\n");
            }
            builder.append(s.toString());
        }

        return builder.toString();
    }
}


class Conditional extends Statement {
    Expression guard;
    Block thenBranch;
    Block elseBranch;

    Conditional(Expression guard, Block thenBranch, Block elseBranch) {
        this.guard = guard;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    <T> T accept(StatementVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return String.format(
            "if (%s) then {\n%s\n} else {\n%s\n}",
            this.guard.toString(),
            this.thenBranch.toString(),
            this.elseBranch.toString()
        );
    }
}

class While extends Statement {
    Expression guard;
    Block body;

    While(Expression guard, Block body) {
        this.guard = guard;
        this.body = body;
    }

    @Override
    <T> T accept(StatementVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return String.format("while (%s) {\n%s\n}", this.guard.toString(), this.body.toString());
    }
}