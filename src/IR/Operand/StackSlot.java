package IR.Operand;

import IR.Function;
import IR.IRVistor;

public class StackSlot extends Address {
    private Function function;
    private String name;

    public StackSlot(Function function, String name) {
        this.function = function;
        this.name = name;
    }

    public Function getFunction() {
        return function;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
