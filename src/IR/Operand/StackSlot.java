package IR.Operand;

import IR.Function;
import IR.IRVistor;

public class StackSlot extends Memory {
    private Function function;
    private String name;

    public StackSlot(String name) {
        this.name = name;
        function = null;
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
