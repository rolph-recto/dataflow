package org.example;

import java.util.*;
import java.util.stream.Collectors;

abstract class Jump {
    /** Replace a target block ID. */
    abstract void replaceTarget(Map<Integer,Integer> substMap);

    /** Get the set of possible targets the jump refers to. */
    abstract Set<Integer> possibleTargets();

    int computeFinalTarget(Map<Integer,Integer> substMap, int target) {
        int cur = target;
        while (substMap.containsKey(cur)) {
            cur = substMap.get(cur);
        }

        return cur;
    }
}

class Halt extends Jump {
    @Override
    public String toString() {
        return "halt";
    }

    @Override
    void replaceTarget(Map<Integer,Integer> substMap) {}

    @Override
    Set<Integer> possibleTargets() {
        return new HashSet<>();
    }
}

class UnconditionalJump extends Jump {
    int target;

    UnconditionalJump(int target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return String.format("goto %d", this.target);
    }

    @Override
    void replaceTarget(Map<Integer,Integer> substMap) {
        this.target = computeFinalTarget(substMap, this.target);
    }

    @Override
    Set<Integer> possibleTargets() {
        return new HashSet<>(this.target);
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

    @Override
    public String toString() {
        return String.format(
            "if (%s) then goto %d else goto %d",
            this.guard.toString(),
            this.trueTarget,
            this.falseTarget
        );
    }

    @Override
    void replaceTarget(Map<Integer,Integer> substMap) {
        this.trueTarget = computeFinalTarget(substMap, this.trueTarget);
        this.falseTarget = computeFinalTarget(substMap, this.falseTarget);
    }

    @Override
    Set<Integer> possibleTargets() {
        return new HashSet<>(List.of(new Integer[]{this.trueTarget, this.falseTarget}));
    }
}

class BasicBlock {
    int id;
    LinkedList<AtomicStatement> statements;
    Jump jump;

    BasicBlock(int id, LinkedList<AtomicStatement> statements, Jump jump) {
        this.id = id;
        this.statements = statements;
        this.jump = jump;
    }

    @Override
    public String toString() {
        if (statements.size() > 0) {
            return String.format(
                "%s;\n%s",
                new Block(new ArrayList<>(this.statements)),
                this.jump
            );

        } else {
            return this.jump.toString();
        }
    }
}

class ControlFlowGraph {
    int curBlockId;
    int entryBlock;
    HashMap<Integer, BasicBlock> blockMap;

    /** Build control flow graph from program. */
    ControlFlowGraph() {
        this.blockMap = new HashMap<>();
    }

    int freshBlockId()  {
        int id = this.curBlockId;
        this.curBlockId += 1;
        return id;
    }

    BasicBlock createBlock(LinkedList<AtomicStatement> statements, Jump jump) {
        int id = freshBlockId();
        var block = new BasicBlock(id, statements, jump);
        this.blockMap.put(id, block);
        return block;
    }

    void setEntryBlock(int blockId) {
        this.entryBlock = blockId;
    }

    /** return a list of basic blocks (. */
    List<BasicBlock> blockList() {
        var idList = new ArrayList<Integer>(this.blockMap.keySet());
        idList.sort(Collections.reverseOrder());
        return idList.stream().map(id -> this.blockMap.get(id)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (BasicBlock block : blockList()) {
            builder.append(block.id);
            builder.append('\n');
            builder.append(block);
            builder.append("\n\n");
        }

        return builder.toString();
    }

    void simplify() {
        var substMap = new HashMap<Integer, Integer>();
        for (Map.Entry<Integer, BasicBlock> kv : this.blockMap.entrySet()) {
            var blockId = kv.getKey();
            var block = kv.getValue();
            if (block.statements.size() == 0 && block.jump instanceof UnconditionalJump) {
                substMap.put(blockId, ((UnconditionalJump)block.jump).target);
            }
        }

        // apply substitutions
        for (Map.Entry<Integer, BasicBlock> kv : this.blockMap.entrySet()) {
            kv.getValue().jump.replaceTarget(substMap);
        }

        // remove unused blocks
        for (int blockId : substMap.keySet()) {
            this.blockMap.remove(blockId);
        }
    }
}