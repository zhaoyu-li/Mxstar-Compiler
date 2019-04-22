package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.Address;
import IR.Operand.Operand;
import IR.Operand.Register;
import IR.Operand.StackSlot;

import java.util.LinkedList;

public class BinaryOperation extends Instruction {
    public enum BinaryOp {
        ADD, SUB, MUL, DIV, MOD, SAL, SAR, AND, OR, XOR
    }
    private Address dst;
    private BinaryOp op;
    private Operand src;

    public BinaryOperation(BasicBlock bb, Address dst, BinaryOp op, Operand src) {
        super(bb);
        this.dst = dst;
        this.op = op;
        this.src = src;
    }

    public Address getDst() {
        return dst;
    }

    public BinaryOp getOp() {
        return op;
    }

    public Operand getSrc() {
        return src;
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
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
