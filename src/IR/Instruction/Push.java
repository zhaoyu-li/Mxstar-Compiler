package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Memory;
import IR.Operand.Operand;
import IR.Operand.Register;

import java.util.LinkedList;

public class Push extends Instruction {
    private Operand src;

    public Push(BasicBlock bb, Operand src) {
        super(bb);
        this.src = src;
    }

    public Operand getSrc() {
        return src;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(src instanceof Memory) {
            registers.addAll(((Memory) src).getUsedRegisters());
        }
        return registers;
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        LinkedList<Register> registers = new LinkedList<>();
        if(src instanceof Memory) {
            registers.addAll(((Memory) src).getUsedRegisters());
        }
        return registers;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
