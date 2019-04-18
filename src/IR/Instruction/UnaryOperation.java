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

    public UnaryOperation(BasicBlock bb, UnaryOp op, Address dst) {
        super(bb);
        this.dst = dst;
        this.op = op;
    }

    public Address getdst() {
        return dst;
    }

    public UnaryOp getOp() {
        return op;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
