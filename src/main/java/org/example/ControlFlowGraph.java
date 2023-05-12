package org.example;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

abstract class Jump {}

class Halt extends Jump {}

class UnconditionalJump extends Jump {
    int target;

    UnconditionalJump(int target) {
        this.target = target;
    }
}

class ConditionalJump extends Jump {
    Expression guard;
    int trueTarget;
    int falseTarget;

    ConditionalJump(Expression guard, int trueTarget, int falseTarget) {
        this.guard = guard;
        this.trueTarget = trueTarget;
        this.falseTarget = falseTarget;
    }
}

class BasicBlock {
    LinkedList<Assign> statements;
    Jump jump;

    BasicBlock(LinkedList<Assign> statements, Jump jump) {
        this.statements = statements;
        this.jump = jump;
    }
}

class ControlFlowGraph {
    int curBlockId;
    int entryBlock;
    HashMap<Integer, BasicBlock> blockMap;

    /** Build control flow graph from program. */
    ControlFlowGraph(Vector<Statement> program) {
        this.blockMap = new HashMap<>();
        var outContext =
            processStatements(
                program,
                new BasicBlock(new LinkedList<>(), new Halt())
            );

        this.entryBlock = createBlock(outContext);
    }

    private int freshBlockId()  {
        int id = this.curBlockId;
        this.curBlockId += 1;
        return id;
    }

    private int createBlock(BasicBlock block) {
        int id = freshBlockId();
        this.blockMap.put(id, block);
        return id;
    }

    private BasicBlock processStatements(Vector<Statement> stmts, BasicBlock block) {
        BasicBlock curBlock = block;
        for (int i = stmts.size() - 1; i >= 0; i --) {
            curBlock = processStatement(stmts.get(i), curBlock);
        }

        return curBlock;
    }

    private BasicBlock processStatement(Statement stmt, BasicBlock block) {
        if (stmt instanceof Assign) {
            block.statements.addFirst((Assign)stmt);
            return block;

        } else if (stmt instanceof Conditional) {
            int newBlockId = createBlock(block);

            var inBlockThen = new BasicBlock(new LinkedList<>(), new UnconditionalJump(newBlockId));
            var outContextThen = processStatements(((Conditional) stmt).thenBranch, inBlockThen);

            var inBlockElse = new BasicBlock(new LinkedList<>(), new UnconditionalJump(newBlockId));
            var outContextElse = processStatements(((Conditional) stmt).elseBranch, inBlockElse);

            int thenBlockId = createBlock(outContextThen);
            int elseBlockId = createBlock(outContextElse);

            return new BasicBlock(
                new LinkedList<>(),
                new ConditionalJump(((Conditional)stmt).guard, thenBlockId, elseBlockId)
            );

        } else if (stmt instanceof While) {
            int newBlockId = createBlock(block);

            var inBlockBody = new BasicBlock(new LinkedList<>(), new UnconditionalJump(newBlockId));
            var outContextBody = processStatements(((While)stmt).body, inBlockBody);

            var bodyBlockId = createBlock(outContextBody);

            return new BasicBlock(
                new LinkedList<>(),
                new ConditionalJump(((While)stmt).guard, bodyBlockId, newBlockId)
            );
        }

        throw new RuntimeException("unreachable");
    }
}
