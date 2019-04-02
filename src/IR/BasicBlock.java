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
}
