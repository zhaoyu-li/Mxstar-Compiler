package IR.Instruction;

import IR.*;
import IR.Operand.Address;
import IR.Operand.Operand;

import java.util.List;

public class Call extends Instruction {
    private Address dest;
    private Function func;
    private List<Operand> args;

    public Call(BasicBlock bb, Address dest, Function func, List<Operand> args) {
        super(bb);
        this.dest = dest;
        this.func = func;
        this.args = args;
    }

    public Address getDest() {
        return dest;
    }

    public Function getFunc() {
        return func;
    }

    public List<Operand> getArgs() {
        return args;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
