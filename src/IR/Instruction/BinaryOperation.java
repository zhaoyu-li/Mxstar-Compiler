package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;
import IR.Operand.Register;

public class BinaryOperation extends Instruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD, SAL, SAR, AND, OR, XOR
    }
    private Address dst;
    private BinaryOp op;
    private Operand lhs;
    private Operand rhs;

    public BinaryOperation(BasicBlock bb, Address dst, BinaryOp op, Operand lhs, Operand rhs) {
        super(bb);
        this.dst = dst;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
        getUsedRegs();
    }

    public Address getdst() {
        return dst;
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
