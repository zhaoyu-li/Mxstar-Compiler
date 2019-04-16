package IR.Operand;

import IR.IRVistor;

public class Memory extends Address {
    private Register base;
    private Register index;
    private int scale;

    public Memory(Register base, Register index, int scale) {
        this.base = base;
        this.index = index;
        this.scale = scale;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
