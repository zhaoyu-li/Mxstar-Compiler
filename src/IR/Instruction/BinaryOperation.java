package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Operand;

public class BinaryOperation extends Instruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD, SAL, SAR, AND, OR, XOR
    }
    private BinaryOp op;
    private Operand lhs;
    private Operand rhs;

    public BinaryOperation(BasicBlock bb, BinaryOp op, Operand lhs, Operand rhs) {
        super(bb);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
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
