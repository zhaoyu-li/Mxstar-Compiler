package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.IntImmediate;
import IR.Operand.Memory;
import IR.Operand.Operand;
import IR.Operand.Register;

import java.util.LinkedList;

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

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(lhs instanceof Memory) {
            registers.addAll(((Memory) lhs).getUsedRegisters());
        } else if (lhs instanceof Register) {
            registers.add((Register) lhs);
        }
        if(rhs instanceof Memory) {
            registers.addAll(((Memory) rhs).getUsedRegisters());
        } else if (rhs instanceof Register) {
            registers.add((Register) rhs);
        }
        return registers;
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        return new LinkedList<>();
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
