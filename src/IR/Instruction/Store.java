package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Operand;

public class Store extends Instruction {
    private Operand value;
    private Operand addr;

    public Store(BasicBlock bb, Operand value, Operand addr) {
        super(bb);
        this.value = value;
        this.addr = addr;
    }

    public Operand getValue() {
        return value;
    }

    public Operand getAddr() {
        return addr;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
