package IR.Instruction;

import IR.*;
import IR.Operand.Operand;

import java.util.List;

public class Call extends Instruction {
    private Function function;
    private List<Operand> parameters;

    public Call(BasicBlock bb, Function function, List<Operand> parameters) {
        super(bb);
        this.function = function;
        this.parameters = parameters;
    }

    public Function getFunction() {
        return function;
    }

    public List<Operand> getParameters() {
        return parameters;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
