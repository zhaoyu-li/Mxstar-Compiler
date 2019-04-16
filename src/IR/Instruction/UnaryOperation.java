package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;
import IR.Operand.Register;

public class UnaryOperation extends Instruction {
    public enum UnaryOp {
        NEG, NOT, INC, DEC
    }
    private Address dst;
    private UnaryOp op;
    private Operand src;

    public UnaryOperation(BasicBlock bb, Address dst, UnaryOp op, Operand src) {
        super(bb);
        this.dst = dst;
        this.op = op;
        this.src = src;
        getUsedRegs();
    }

    public Address getdst() {
        return dst;
    }

    public UnaryOp getOp() {
        return op;
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
