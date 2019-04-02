package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;

public class Allocate extends Instruction {
    private Address dst;
    private int size;

    public Allocate(BasicBlock bb, Address dst, int size) {
        super(bb);
        this.dst = dst;
        this.size = size;
    }

    public Address getDst() {
        return dst;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
