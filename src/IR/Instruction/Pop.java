package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Memory;
import IR.Operand.Register;

import java.util.LinkedList;

public class Pop extends Instruction {
    private Address dst;

    public Pop(BasicBlock bb, Address dst) {
        super(bb);
        this.dst = dst;
    }

    public Address getDst() {
        return dst;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        }
        return registers;
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        }
        return registers;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
