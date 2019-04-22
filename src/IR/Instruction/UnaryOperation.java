package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Memory;
import IR.Operand.Operand;
import IR.Operand.Register;

import java.util.LinkedList;

public class UnaryOperation extends Instruction {
    public enum UnaryOp {
        NEG, NOT, INC, DEC
    }
    private Address dst;
    private UnaryOp op;

    public UnaryOperation(BasicBlock bb, UnaryOp op, Address dst) {
        super(bb);
        this.dst = dst;
        this.op = op;
    }

    public Address getdst() {
        return dst;
    }

    public UnaryOp getOp() {
        return op;
    }

    @Override
    public LinkedList<Register> getUsedRegisters(){
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        } else if(dst instanceof Register) {
            registers.add((Register) dst);
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
