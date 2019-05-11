package IR.Operand;

import IR.Function;
import IR.IRVistor;

public class StackSlot extends Memory {
    private String name;
    private Function function;

    public StackSlot() {

    }

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
        super.setBase(vr);
    }

    public void setOffset(IntImmediate offset) {
        super.setOffset(offset);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
