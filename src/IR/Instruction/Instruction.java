package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Register;

import java.util.HashSet;
import java.util.Set;

public abstract class Instruction {
    private BasicBlock bb;
    private Instruction prev;
    private Instruction next;
    private boolean removed;
    protected Set<Register> useRegs;

    public Instruction() {
        bb = null;
        prev = null;
        next = null;
        removed = false;
        useRegs = new HashSet<>();
    }

    public Instruction(BasicBlock bb) {
        this.bb = bb;
        prev = null;
        next = null;
        removed = false;
        useRegs = new HashSet<>();
    }

    public BasicBlock getBB() {
        return bb;
    }

    public Instruction getPrev() {
        return prev;
    }

    public Instruction getNext() {
        return next;
    }

    public Set<Register> getUseRegs() {
        return useRegs;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void prepend(Instruction inst) {
        if(prev == null) {
            inst.next = this;
            this.prev = inst;
            bb.setHead(inst);
        } else {
            prev.next = inst;
            inst.prev = next;
            inst.next = this;
            this.prev = inst;
        }
    }

    public void append(Instruction inst) {
        if(next == null) {
            inst.prev = this;
            this.next = inst;
            bb.setTail(inst);
        } else {
            next.prev = inst;
            inst.prev = this;
            inst.next = next;
            this.next = inst;
        }
    }

    public void remove() {
        if (prev == null) {
            bb.setHead(next);
        } else {
            prev.next = next;
        }
        if (next == null) {
            bb.setTail(prev);
        } else {
            next.prev = prev;
        }
    }

    public void replace(Instruction inst) {
        inst.prev = prev;
        inst.next = next;
        if(prev == null) {
            bb.setHead(inst);
        } else {
            prev.next = inst;
        }
        if(next == null) {
            bb.setTail(inst);
        } else {
            next.prev = inst;
        }
    }



    public abstract void accept(IRVistor vistor);
}
