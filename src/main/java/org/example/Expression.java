package org.example;

import java.util.Objects;

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

    @Override
    public String toString() {
        return String.valueOf(this.val);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof Literal otherLiteral) {
            return this.val == otherLiteral.val;

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.val);
    }
}

class Input extends Expression {
    @Override
    <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;

        }  else{
            return other instanceof Input;
        }
    }

    @Override
    public String toString() {
        return "input";
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

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof Var otherVar) {
            return Objects.equals(this.name, otherVar.name);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
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

    @Override
    public String toString() {
        return String.format("(%s + %s)", this.lhs.toString(), this.rhs.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof Add otherAdd) {
            return Objects.equals(this.lhs, otherAdd.lhs) && Objects.equals(this.rhs, otherAdd.rhs);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lhs, this.rhs);
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

    @Override
    public String toString() {
        return String.format("(%s * %s)", this.lhs.toString(), this.rhs.toString());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (this == other) {
            return true;
        }

        if (other instanceof Multiply otherMul) {
            return Objects.equals(this.lhs, otherMul.lhs) && Objects.equals(this.rhs, otherMul.rhs);

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lhs, this.rhs);
    }
}
