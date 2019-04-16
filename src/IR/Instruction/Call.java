package IR.Instruction;

import IR.*;
import IR.Operand.Address;
import IR.Operand.Operand;
import IR.Operand.Register;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Call extends Instruction {
    private Address dst;
    private Function func;
    private List<Operand> args;

    public Call(BasicBlock bb, Address dst, Function func, List<Operand> args) {
        super(bb);
        this.dst = dst;
        this.func = func;
        this.args = args;
    }

    public Call(BasicBlock bb, Address dst, Function func, Operand... args) {
        super(bb);
        this.dst = dst;
        this.func = func;
        this.args = new LinkedList<>(Arrays.asList(args));
    }

    public Address getdst() {
        return dst;
    }

    public Function getFunc() {
        return func;
    }

    public List<Operand> getArgs() {
        return args;
    }

    public void getUsedRegs() {
        usedRegs.clear();
        for(Operand arg : args) {
            if(arg instanceof Register) usedRegs.add((Register) arg);
        }
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
