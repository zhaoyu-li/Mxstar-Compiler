package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Memory;
import IR.Operand.Register;
import IR.Operand.StackSlot;

import java.util.HashMap;
import java.util.LinkedList;

public class Leave extends Instruction {
    public Leave(BasicBlock bb) {
        super(bb);
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        return new LinkedList<>();
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        return new LinkedList<>();
    }

    @Override
    public void renameUsedRegisters(HashMap<Register, Register> renameMap) {

    }

    @Override
    public void renameDefinedRegisters(HashMap<Register, Register> renameMap) {

    }

    @Override
    public LinkedList<StackSlot> getStackSlots() {
        return new LinkedList<>();
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}