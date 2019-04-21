package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.IntImmediate;
import IR.Operand.Operand;
import IR.Operand.Register;

public class CJump extends Instruction {
    public enum CompareOp {
        EQ, NE, LT, GT, LE, GE
    }

    private CompareOp op;
    private Operand lhs;
    private Operand rhs;
    private BasicBlock thenBB;
    private BasicBlock elseBB;

    public CJump(BasicBlock bb, Operand lhs, CompareOp op, Operand rhs,
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

    public void getUsedRegs() {
        usedRegs.clear();
        if(lhs instanceof Register) usedRegs.add((Register) lhs);
        if(rhs instanceof Register) usedRegs.add((Register) rhs);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
