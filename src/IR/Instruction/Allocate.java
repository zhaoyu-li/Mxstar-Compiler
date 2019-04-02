package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;

public class Allocate extends Instruction {
    private Address dest;
    private int size;

    public Allocate(BasicBlock bb, Address dest, int size) {
        super(bb);
        this.dest = dest;
        this.size = size;
    }

    public Address getDest() {
        return dest;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
