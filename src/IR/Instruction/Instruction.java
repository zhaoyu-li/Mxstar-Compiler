package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;

public abstract class Instruction {
    private BasicBlock bb;
    private Instruction prev;
    private Instruction next;

    public Instruction() {
        bb = null;
        prev = null;
        next = null;
    }

    public Instruction(BasicBlock bb) {
        this.bb = bb;
        prev = null;
        next = null;
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
        if(prev == null && next == null) {
            bb.setHead(null);
            bb.setTail(null);
        } else if(prev == null) {
            bb.setHead(next);
            next.prev = null;
        } else if(next == null) {
            bb.setTail(prev);
            prev.next = null;
        } else {
            prev.next = next;
            next.prev = prev;
        }
    }

    public abstract void accept(IRVistor vistor);
}
