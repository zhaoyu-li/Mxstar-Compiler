package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Operand;

public class CJump extends Instruction {
    public enum CompareOp {
        EQ, NEQ, LT, GT, LE, GE
    }

    private CompareOp op;
    private Operand lhs;
    private Operand rhs;
    private BasicBlock thenBB;
    private BasicBlock elseBB;

    public CJump(BasicBlock bb, CompareOp op, Operand lhs, Operand rhs,
                 BasicBlock thenBB, BasicBlock elseBB) {
        super(bb);
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        this.thenBB = thenBB;
        this.elseBB = elseBB;
    }

    public CompareOp getOp() {
        return op;
    }

    public Operand getLhs() {
        return lhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    public BasicBlock getElseBB() {
        return elseBB;
    }

    public BasicBlock getThenBB() {
        return thenBB;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}