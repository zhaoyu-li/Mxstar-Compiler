package IR.Operand;

import IR.IRVistor;

public class Memory extends Address {
    protected Register base;
    private Register index;
    private int scale;
    protected Constant offset;

    public Memory() {

    }

    public Memory(Register base) {
        this.base = base;
    }

    public Memory(Constant offset) {
        this.offset = offset;
    }

    public Memory(Register base, Constant offset) {
        this.base = base;
        this.index = null;
        this.offset = offset;
        this.scale = 0;
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

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
