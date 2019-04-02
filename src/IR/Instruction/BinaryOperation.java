package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;

public class BinaryOperation extends Instruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD, SAL, SAR, AND, OR, XOR
    }
    private Address dest;
    private BinaryOp op;
    private Operand lhs;
    private Operand rhs;

    public BinaryOperation(BasicBlock bb, Address dest, BinaryOp op, Operand lhs, Operand rhs) {
        super(bb);
        this.dest = dest;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Address getDest() {
        return dest;
    }

    public BinaryOp getOp() {
        return op;
    }

    public Operand getLhs() {
        return lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
