package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;

public class Pop extends Instruction {
    private Address dst;

    public Pop(BasicBlock bb, Address dst) {
        super(bb);
        this.dst = dst;
    }

    public Address getDst() {
        return dst;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
