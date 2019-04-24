package IR.Instruction;

import IR.BasicBlock;
import IR.IRVistor;
import IR.Operand.*;

import java.util.HashMap;
import java.util.LinkedList;

import static IR.RegisterSet.*;

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
        LinkedList<Register> registers = new LinkedList<>();
        if(src instanceof Memory) {
            registers.addAll(((Memory) src).getUsedRegisters());
        } else if(src instanceof Register) {
            registers.add((Register) src);
        }
        if(dst instanceof Memory) {
            registers.addAll(((Memory) dst).getUsedRegisters());
        } else if(dst instanceof Register) {
            registers.add((Register) dst);
        }
        if(op == BinaryOp.MUL) {
            if (!registers.contains(vrax)) {
                registers.add(vrax);
            }
        } else if(op == BinaryOp.DIV || op == BinaryOp.MOD) {
            if(!registers.contains(vrax)) {
                registers.add(vrax);
            }
            if(!registers.contains(vrdx)) {
                registers.add(vrdx);
            }
        }
        return registers;
    }

    @Override
    public LinkedList<Register> getDefinedRegisters() {
        LinkedList<Register> registers = new LinkedList<>();
        if(dst instanceof Register) {
            registers.add((Register) dst);
        }
        if(op == BinaryOp.MUL || op == BinaryOp.DIV || op == BinaryOp.MOD) {
            if(!registers.contains(vrax)) {
                registers.add(vrax);
            }
            if(!registers.contains(vrdx)) {
                registers.add(vrdx);
            }
        }
        return registers;
    }

    @Override
    public void renameUsedRegisters(HashMap<Register, Register> renameMap) {
        if(src instanceof Memory) {
            src = ((Memory) src).copy();
            ((Memory) src).renameUseReg(renameMap);
        } else if(src instanceof Register && renameMap.containsKey(src)) {
            src = renameMap.get(src);
        }
        if(dst instanceof Memory) {
            dst = ((Memory) dst).copy();
            ((Memory) dst).renameUseReg(renameMap);
        } else if(dst instanceof Register && renameMap.containsKey(dst)) {
            dst = renameMap.get(dst);
        }
    }

    @Override
    public void renameDefinedRegisters(HashMap<Register, Register> renameMap) {
        if(dst instanceof Register && renameMap.containsKey(dst)) {
            dst = renameMap.get(dst);
        }
    }

    @Override
    public LinkedList<StackSlot> getStackSlots() {
        return calcStackSlots(dst, src);
    }

    @Override
    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
