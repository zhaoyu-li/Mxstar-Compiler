package IR;

import IR.Instruction.CJump;
import IR.Instruction.Instruction;
import IR.Instruction.Jump;

import java.util.LinkedList;

public class BasicBlock {
    private String name;
    private Function function;
    private Instruction head;
    private Instruction tail;
    private LinkedList<BasicBlock> prevBBs;
    private LinkedList<BasicBlock> nextBBs;

    public BasicBlock(String name, Function function) {
        this.name = name;
        this.function = function;
        this.head = null;
        this.tail = null;
        this.function.addBasicBlock(this);
        this.prevBBs = new LinkedList<>();
        this.nextBBs = new LinkedList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setHead(Instruction head) {
        this.head = head;
    }

    public Instruction getHead() {
        return head;
    }

    public void setTail(Instruction tail) {
        this.tail = tail;
    }

    public Instruction getTail() {
        return tail;
    }

    public void addPrevInst(Instruction inst) {
        if(head == null) {
            inst.setPrev(null);
            inst.setNext(null);
            head = inst;
            tail = inst;
        } else {
            head.prepend(inst);
        }
    }

    public void addNextInst(Instruction inst) {
        if(head == null) {
            inst.setPrev(null);
            inst.setNext(null);
            head = inst;
            tail = inst;
        } else {
            tail.append(inst);
        }
    }

    public void addNextJumpInst(Instruction inst) {
        addNextInst(inst);
        if(inst instanceof Jump) {
            nextBBs.add(((Jump) inst).getTargetBB());
            ((Jump) inst).getTargetBB().prevBBs.add(this);
        } else if(inst instanceof CJump) {
            nextBBs.add(((CJump) inst).getElseBB());
            ((CJump) inst).getElseBB().prevBBs.add(this);
            nextBBs.add(((CJump) inst).getThenBB());
            ((CJump) inst).getThenBB().prevBBs.add(this);
        }
    }

    public Function getFunction() {
        return function;
    }

    public LinkedList<BasicBlock> getPrevBBs() {
        return prevBBs;
    }

    public LinkedList<BasicBlock> getNextBBs() {
        return nextBBs;
    }

    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
