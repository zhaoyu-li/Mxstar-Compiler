package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.*;

import java.util.HashMap;
import java.util.LinkedList;

public class UnaryOperation extends Instruction {
    public enum UnaryOp {
        NEG, NOT, INC, DEC
    }
    private Address dst;
    private UnaryOp op;

    public UnaryOperation(BasicBlock bb, UnaryOp op, Address dst) {
        super(bb);
        this.dst = dst;
        this.op = op;
    }

    public Address getDst() {
        return dst;
    }

    public UnaryOp getOp() {
        return op;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        } else if(dst instanceof Register) {
            registers.add((Register) dst);
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
        if(dst instanceof Register && renameMap.containsKey(dst)) {
            dst = renameMap.get(dst);
        } else if(dst instanceof Memory) {
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
        return calcStackSlots(dst);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
