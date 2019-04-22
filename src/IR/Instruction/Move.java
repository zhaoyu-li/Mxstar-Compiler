package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Memory;
import IR.Operand.Operand;
import IR.Operand.Register;

import java.util.LinkedList;

public class Move extends Instruction {
    private Address dst;
    private Operand src;

    public Move(BasicBlock bb, Address dst, Operand src) {
        super(bb);
        this.dst = dst;
        this.src = src;
    }

    public Address getDst() {
        return dst;
    }

    public Operand getSrc() {
        return src;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        }
        if(src instanceof Memory) {
            registers.addAll(((Memory) src).getUsedRegisters());
        } else if(src instanceof Register) {
            registers.add((Register) src);
        }
        return registers;
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Register) {
            registers.add((Register) dst);
        }
        return registers;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
