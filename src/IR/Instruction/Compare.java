package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.*;

import java.util.HashMap;
import java.util.LinkedList;

public class Compare extends Instruction {
    private CJump.CompareOp op;
    private Operand lhs;
    private Operand rhs;
    private Address dst;

    public Compare(BasicBlock bb, Address dst, Operand lhs, CJump.CompareOp op, Operand rhs) {
        super(bb);
        this.dst = dst;
        this.op = op;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void setOp(CJump.CompareOp op) {
        this.op = op;
    }

    public CJump.CompareOp getOp() {
        return op;
    }

    public Address getDst() {
        return dst;
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
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Register) {
            registers.add((Register) dst);
        }
        return registers;
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
        if(dst instanceof Register && renameMap.containsKey(dst)) {
            dst = renameMap.get(dst);
        }
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
