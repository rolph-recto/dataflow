package org.example;

interface StatementVisitor<T> {
    T visit(Assign stmt);
    T visit(Conditional stmt);
    T visit(While stmt);
}
