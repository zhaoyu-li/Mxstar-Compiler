package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;

public class Move extends Instruction {
    private Address dest;
    private Operand src;

    public Move(BasicBlock bb, Address dest, Operand src) {
        super(bb);
        this.dest = dest;
        this.src = src;
    }

    public Address getDest() {
        return dest;
    }

    public Operand getSrc() {
        return src;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}