package org.example;

import java.util.ArrayList;
import java.util.LinkedList;

abstract class CFGBuilder {
    protected final ControlFlowGraph cfg;

    CFGBuilder() {
        this.cfg = new ControlFlowGraph();
    }

    public ControlFlowGraph buildCFG(Block program) {
        var endBlock = this.cfg.createBlock(new LinkedList<>(), new Halt());
        var outContext = processStatements(program, endBlock);
        this.cfg.setEntryBlock(outContext.id);
        this.cfg.simplify();
        return this.cfg;
    }

    protected abstract BasicBlock processStatements(Block statement, BasicBlock block);

    protected BasicBlock processStatement(Statement stmt, BasicBlock block) {
        if (stmt instanceof Assign) {
            block.statements.addFirst((Assign) stmt);
            return block;

        } else if (stmt instanceof Conditional) {
            var inBlockThen = this.cfg.createBlock(new LinkedList<>(), new UnconditionalJump(block.id));
            var outContextThen = processStatements(((Conditional) stmt).thenBranch, inBlockThen);

            var inBlockElse = this.cfg.createBlock(new LinkedList<>(), new UnconditionalJump(block.id));
            var outContextElse = processStatements(((Conditional) stmt).elseBranch, inBlockElse);

            return this.cfg.createBlock(
                new LinkedList<>(),
                new ConditionalJump(((Conditional) stmt).guard, outContextThen.id, outContextElse.id)
            );

        } else if (stmt instanceof While) {
            var inBlockBody = this.cfg.createBlock(new LinkedList<>(), new UnconditionalJump(block.id));
            var outContextBody = processStatements(((While) stmt).body, inBlockBody);

            return this.cfg.createBlock(
                new LinkedList<>(),
                new ConditionalJump(((While) stmt).guard, outContextBody.id, block.id)
            );
        }

        throw new RuntimeException("unreachable");
    }
}

/** Creates a CFG of basic blocks from a program. */
class BasicBlockCFGBuilder extends CFGBuilder {
    @Override
    protected BasicBlock processStatements(Block block, BasicBlock nextBB) {
        BasicBlock curBB = nextBB;
        for (int i = block.statements.size() - 1; i >= 0; i--) {
            curBB = processStatement(block.statements.get(i), curBB);
        }

        return curBB;
    }
}

/** Creates a CFG of atomic statements from a program. */
class AtomicCFGBuilder extends CFGBuilder {
    @Override
    protected BasicBlock processStatements(Block block, BasicBlock nextBB) {
        BasicBlock curBB = nextBB;
        for (int i = block.statements.size() - 1; i >= 0; i--) {
            var newBlock = processStatement(block.statements.get(i), curBB);
            curBB = this.cfg.createBlock(new LinkedList<>(), new UnconditionalJump(newBlock.id));
        }

        return curBB;
    }
}
