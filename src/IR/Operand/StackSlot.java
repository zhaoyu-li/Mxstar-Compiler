package IR.Operand;

import IR.Function;
import IR.IRVistor;

public class StackSlot extends Memory {
    private Function function;
    private String name;

    public StackSlot(String name, Function function) {
        this.name = name;
        this.function = function;
    }

    public Function getFunction() {
        return function;
    }

    public String getName() {
        return name;
    }

    public void setBase(PhysicalRegister vr) {
        this.base = vr;
    }

    public void setOffset(IntImmediate offset) {
        this.offset = offset;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
