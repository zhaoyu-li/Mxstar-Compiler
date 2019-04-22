package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Memory;
import IR.Operand.Register;

import java.util.LinkedList;

public class Lea extends Instruction {
    private Register dst;
    private Memory src;

    public Lea(BasicBlock bb, Register dst, Memory src) {
        super(bb);
        this.dst = dst;
        this.src = src;
    }

    public Register getDst() {
        return dst;
    }

    public Memory getSrc() {
        return src;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>(src.getUsedRegisters());
        registers.add(dst);
        return registers;
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        LinkedList<Register> registers = new LinkedList<>();
        registers.add(dst);
        return registers;
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
