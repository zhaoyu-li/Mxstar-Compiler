package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;
import IR.Operand.Register;

public class Move extends Instruction {
    private Address dst;
    private Operand src;

    public Move(BasicBlock bb, Address dst, Operand src) {
        super(bb);
        this.dst = dst;
        this.src = src;
    }

    public Address getdst() {
        return dst;
    }

    public Operand getSrc() {
        return src;
    }

    public void getUsedRegs() {
        usedRegs.clear();
        if(src instanceof Register) usedRegs.add((Register) src);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
