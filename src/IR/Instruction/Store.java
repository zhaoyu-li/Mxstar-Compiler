package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Operand;
import IR.Operand.Register;

public class Store extends Instruction {
    private Operand value;
    private Operand addr;

    public Store(BasicBlock bb, Operand value, Operand addr) {
        super(bb);
        this.value = value;
        this.addr = addr;
        getUsedRegs();
    }

    public Operand getValue() {
        return value;
    }

    public Operand getAddr() {
        return addr;
    }

    public void getUsedRegs() {
        usedRegs.clear();
        if(value instanceof Register) usedRegs.add((Register) value);
        if(addr instanceof Register) usedRegs.add((Register) addr);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
