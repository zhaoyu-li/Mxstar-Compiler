package IR.Operand;

import IR.IRVistor;

import java.util.HashMap;
import java.util.LinkedList;

public class Memory extends Address {
    private Register base;
    private Register index;
    private int scale;
    private Constant offset;

    public Memory() {
        this.base = null;
        this.offset = null;
    }

    public Memory(Register base) {
        this.base = base;
        this.offset = null;
    }

    public Memory(Constant offset) {
        this.base = null;
        this.offset = offset;
    }

    public Memory(Register base, Constant offset) {
        this.base = base;
        this.offset = offset;
    }

    public Memory(Register base, int scale, Constant offset) {
        this.base = base;
        this.index = null;
        this.scale = scale;
        this.offset = offset;
    }

    public Memory(Register base, Register index, int scale) {
        this.base = base;
        this.index = index;
        this.scale = scale;
        this.offset = null;
    }

    public Memory(Register base, Register index, int scale, Constant offset) {
        this.base = base;
        this.index = index;
        this.scale = scale;
        this.offset = offset;
    }

    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(base != null) registers.add(base);
        if(index != null) registers.add(index);
        return registers;
    }

    public void setBase(Register base) {
        this.base = base;
    }

    public void setOffset(Constant offset) {
        this.offset = offset;
    }

    public Memory copy() {
        if(this instanceof StackSlot) {
            return this;
        } else {
            return new Memory(base, index, scale, offset);
        }
    }

    public Register getBase() {
        return base;
    }

    public Register getIndex() {
        return index;
    }

    public int getScale() {
        return scale;
    }

    public Constant getOffset() {
        return offset;
    }

    public void renameUseReg(HashMap<Register, Register> renameMap) {
        if(renameMap.containsKey(base))
            base = renameMap.get(base);
        if(renameMap.containsKey(index))
            index = renameMap.get(index);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
