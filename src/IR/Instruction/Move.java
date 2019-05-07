package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.*;

import java.util.HashMap;
import java.util.LinkedList;

public class Move extends Instruction {
    private Address dst;
    private Operand src;

    public Move(BasicBlock bb, Address dst, Operand src) {
        super(bb);
        this.dst = dst;
        this.src = src;
    }

    public Address getDst() {
        return dst;
    }

    public void setDst(Address dst) {
        this.dst = dst;
    }


    public Operand getSrc() {
        return src;
    }

    public void setSrc(Operand src) {
        this.src = src;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        }
        if(src instanceof Memory) {
            registers.addAll(((Memory) src).getUsedRegisters());
        } else if(src instanceof Register) {
            registers.add((Register) src);
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
        if(src instanceof Memory) {
            src = ((Memory) src).copy();
            ((Memory) src).renameUseReg(renameMap);
        } else if(src instanceof Register && renameMap.containsKey(src)) {
            src = renameMap.get(src);
        }
        if(dst instanceof Memory) {
            dst = ((Memory) dst).copy();
            ((Memory) dst).renameUseReg(renameMap);
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
        return calcStackSlots(dst, src);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
