package IR.Instruction;

import IR.*;
import IR.Operand.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Call extends Instruction {
    private Address dst;
    private Function func;
    private LinkedList<Operand> args;

    public Call(BasicBlock bb, Address dst, Function func, LinkedList<Operand> args) {
        super(bb);
        this.dst = dst;
        this.func = func;
        this.args = args;
        Function caller = this.getBB().getFunction();
        caller.addCallee(func);
        if(func.getName().equals("println") || func.getName().equals("print")) {
            caller.setHasOutput(true);
        }
    }

    public Call(BasicBlock bb, Address dst, Function func, Operand... args) {
        super(bb);
        this.dst = dst;
        this.func = func;
        this.args = new LinkedList<>(Arrays.asList(args));
        Function caller = this.getBB().getFunction();
        caller.addCallee(func);
        if(func.getName().equals("println") || func.getName().equals("print")) {
            caller.setHasOutput(true);
        }
    }

    public Address getDst() {
        return dst;
    }

    public Function getFunc() {
        return func;
    }

    public LinkedList<Operand> getArgs() {
        return args;
    }

    public LinkedList<Register> getAllUsedRegister() {
        LinkedList<Register> registers = new LinkedList<>();
        for(Operand arg : args) {
            if(arg instanceof Memory) {
                registers.addAll(((Memory) arg).getUsedRegisters());
            } else if(arg instanceof VirtualRegister) {
                registers.add((Register) arg);
            }
        }
        return registers;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        return new LinkedList<>(RegisterSet.vargs.subList(0, Integer.min(6, args.size())));
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        return new LinkedList<>(RegisterSet.vcallerSave);
    }

    @Override
    public void renameUsedRegisters(HashMap<Register, Register> renameMap) {

    }

    @Override
    public void renameDefinedRegisters(HashMap<Register, Register> renameMap) {
        if(dst instanceof Register && renameMap.containsKey(dst)) {
            dst = renameMap.get(dst);
        }
    }

    @Override
    public LinkedList<StackSlot> getStackSlots() {
        LinkedList<StackSlot> slots = new LinkedList<>(calcStackSlots(dst));
        for(Operand operand : args) {
            if(operand instanceof StackSlot)
                slots.add((StackSlot) operand);
        }
        return slots;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
