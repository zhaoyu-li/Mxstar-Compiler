package IR;

import IR.Instruction.Instruction;

public class BasicBlock {
    private String name;
    private Function function;
    private Instruction head;
    private Instruction tail;

    public BasicBlock() {
        name = null;
        function = null;
        head = null;
        tail = null;
    }

    public BasicBlock(String name, Function function) {
        this.name = name;
        this.function = function;
        head = null;
        tail = null;

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
        head.prepend(inst);
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
}
