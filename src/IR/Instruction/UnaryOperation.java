package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;

public class UnaryOperation extends Instruction {
    public enum UnaryOp {
        NEG, NOT, INC, DEC
    }
    private Address dest;
    private UnaryOp op;
    private Operand src;

    public UnaryOperation(BasicBlock bb, Address dest, UnaryOp op, Operand src) {
        super(bb);
        this.dest = dest;
        this.op = op;
        this.src = src;
    }

    public Address getDest() {
        return dest;
    }

    public UnaryOp getOp() {
        return op;
    }

    public Operand getSrc() {
        return src;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
