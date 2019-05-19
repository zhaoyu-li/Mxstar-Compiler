package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.*;

import java.util.HashMap;
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

    public BasicBlock doCompare() {
        int v1 = ((IntImmediate) lhs).getValue();
        int v2 = ((IntImmediate) rhs).getValue();
        boolean r;
        switch(op) {
            case NE:
                r = v1 != v2;
                break;
            case LE:
                r = v1 <= v2;
                break;
            case GE:
                r = v1 >= v2;
                break;
            case LT:
                r = v1 < v2;
                break;
            case GT:
                r = v1 > v2;
                break;
            case EQ:
                r = v1 == v2;
                break;
            default:
                r = false;
                break;
        }
        return r ? thenBB : elseBB;
    }

    public CompareOp getReverseCompareOp() {
        switch(op) {
            case EQ:
                return CompareOp.EQ;
            case GT:
                return CompareOp.LE;
            case LT:
                return CompareOp.GE;
            case GE:
                return CompareOp.LT;
            case LE:
                return CompareOp.GT;
            case NE:
                return CompareOp.NE;
            default:
                return null;
        }
    }

    public CompareOp getNegativeCompareOp() {
        switch (op) {
            case EQ:
                return CompareOp.NE;
            case GT:
                return CompareOp.LE;
            case LT:
                return CompareOp.GE;
            case GE:
                return CompareOp.LT;
            case LE:
                return CompareOp.GT;
            case NE:
                return CompareOp.EQ;
            default:
                return null;
        }

    }

    public void setOp(CompareOp op) {
        this.op = op;
    }

    public CompareOp getOp() {
        return op;
    }

    public void setLhs(Operand lhs) {
        this.lhs = lhs;
    }

    public Operand getLhs() {
        return lhs;
    }

    public void setRhs(Operand rhs) {
        this.rhs = rhs;
    }

    public Operand getRhs() {
        return rhs;
    }

    public void setElseBB(BasicBlock elseBB) {
        this.elseBB = elseBB;
    }

    public BasicBlock getElseBB() {
        return elseBB;
    }

    public void setThenBB(BasicBlock thenBB) {
        this.thenBB = thenBB;
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
    public void renameUsedRegisters(HashMap<Register, Register> renameMap) {
        if(lhs instanceof Memory) {
            lhs = ((Memory) lhs).copy();
            ((Memory) lhs).renameUseReg(renameMap);
        } else if(lhs instanceof Register && renameMap.containsKey(lhs)) {
            lhs = renameMap.get(lhs);
        }
        if(rhs instanceof Memory) {
            rhs = ((Memory) rhs).copy();
            ((Memory) rhs).renameUseReg(renameMap);
        } else if(rhs instanceof Register && renameMap.containsKey(rhs)) {
            rhs = renameMap.get(rhs);
        }
    }

    @Override
    public void renameDefinedRegisters(HashMap<Register, Register> renameMap) {

    }

    @Override
    public LinkedList<StackSlot> getStackSlots() {
        return calcStackSlots(lhs, rhs);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
