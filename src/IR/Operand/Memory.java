package IR.Operand;

import IR.IRVistor;

import java.util.LinkedList;

public class Memory extends Address {
    protected Register base;
    private Register index;
    private int scale;
    protected Constant offset;

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

    public LinkedList<Register> getDefinedRegisters() {
        LinkedList<Register> registers = new LinkedList<>();
        return registers;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
