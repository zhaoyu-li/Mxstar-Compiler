package BackEnd;

import IR.*;
import IR.Instruction.*;
import IR.Operand.*;

import java.util.HashMap;
import java.util.HashSet;

import static IR.RegisterSet.*;

public class LocalValueNumberOptimizer implements IRVistor {
    private IRProgram program;
    private Integer curValueNumber;
    private HashMap<Pair, Integer> pairValueMap;

    private HashMap<Integer, HashSet<VirtualRegister>> valueRegisterMap;
    private HashMap<VirtualRegister, Integer> registerValueMap;
    private HashMap<Integer, Integer> valueImmediateMap;
    private HashMap<Integer, Integer> immediateValueMap;

    private class Pair {
        BinaryOperation.BinaryOp op;
        Integer lvalue;
        Integer rvalue;

        private Pair(BinaryOperation.BinaryOp op, Integer lvalue, Integer rvalue) {
            this.op = op;
            this.lvalue = lvalue;
            this.rvalue = rvalue;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof Pair) {
                return this.hashCode() == o.hashCode() && this.lvalue.equals(((Pair) o).lvalue) && this.lvalue.equals(((Pair) o).rvalue);
            } else {
                return false;
            }
        }
    }

    public LocalValueNumberOptimizer(IRProgram program) {
        this.program = program;
        curValueNumber = 0;
        registerValueMap = new HashMap<>();
        pairValueMap = new HashMap<>();
        immediateValueMap = new HashMap<>();
        valueRegisterMap = new HashMap<>();
        valueImmediateMap = new HashMap<>();
    }

    public void run() {
        visit(program);
    }

    private Integer getOperandValue(Operand operand) {
        if(operand instanceof VirtualRegister) {
            return getRegsiterValue((VirtualRegister) operand);
        } else if(operand instanceof IntImmediate) {
            return getImmediateValue(((IntImmediate) operand).getValue());
        } else return curValueNumber++;
    }

    private Integer getImmediateValue(Integer imm) {
        if(!immediateValueMap.containsKey(imm)) {
            valueImmediateMap.put(curValueNumber, imm);
            immediateValueMap.put(imm, curValueNumber++);
        }
        return immediateValueMap.get(imm);
    }

    private Integer getRegsiterValue(VirtualRegister vr) {
        if(!registerValueMap.containsKey(vr)) {
            addRegisterValue(vr, curValueNumber++);
        }
        return registerValueMap.get(vr);
    }

    private Operand getValueOperand(Integer value) {
        if(valueImmediateMap.containsKey(value)) {
            return new IntImmediate(valueImmediateMap.get(value));
        } else if(valueRegisterMap.containsKey(value)) {
            if(!valueRegisterMap.get(value).isEmpty()) {
                return valueRegisterMap.get(value).iterator().next();
            } else return null;
        } else return null;
    }

    private void addRegisterValue(VirtualRegister vr, Integer value) {
        if(!valueRegisterMap.containsKey(value)) {
            valueRegisterMap.put(value, new HashSet<>());
        }
        valueRegisterMap.get(value).add(vr);
        registerValueMap.put(vr, value);
    }

    private void delRegiserValue(VirtualRegister vr) {
        if(registerValueMap.containsKey(vr)) {
            Integer value = registerValueMap.get(vr);
            valueRegisterMap.get(value).remove(vr);
            registerValueMap.remove(vr);
        }
    }

    private void changeRegisterValue(VirtualRegister vr, Integer value) {
        delRegiserValue(vr);
        addRegisterValue(vr, value);
    }


    @Override
    public void visit(IRProgram node) {
        for(Function function : node.getFunctions().values()) {
            if(function.getType() == Function.FuncType.UserDefined){
                function.accept(this);
            }

        }
    }

    @Override
    public void visit(Function node) {
        for(BasicBlock bb : node.getBasicBlocks()) {
            bb.accept(this);
        }
    }

    @Override
    public void visit(BasicBlock node) {
        curValueNumber = 0;
        registerValueMap.clear();
        pairValueMap.clear();
        immediateValueMap.clear();
        valueRegisterMap.clear();
        valueImmediateMap.clear();
        for(Instruction inst = node.getHead(); inst != null; inst = inst.getNext()) {
            inst.accept(this);
        }
    }

    @Override
    public void visit(Jump node) {

    }

    @Override
    public void visit(CJump node) {

    }

    @Override
    public void visit(Return node) {

    }

    private Integer doBinary(BinaryOperation.BinaryOp op, Integer limm, Integer rimm) {
        switch (op) {
            case SUB: return limm - rimm;
            case XOR: return limm ^ rimm;
            case MUL: return limm * rimm;
            case MOD: if(rimm == 0) rimm = 1; return limm % rimm;
            case DIV: if(rimm == 0) rimm = 1; return limm / rimm;
            case AND: return limm & rimm;
            case ADD: return limm + rimm;
            case OR: return limm | rimm;
            case SAL: return limm << rimm;
            case SAR: return limm >> rimm;
            default: assert false; return 0;
        }
    }

    @Override
    public void visit(BinaryOperation node) {
        if(node.getOp() == BinaryOperation.BinaryOp.MUL || node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
            Integer lvalue = getOperandValue(vrax);
            Integer rvalue = getOperandValue(node.getSrc());
            Integer resultValue;
            Pair pair = new Pair(node.getOp(), lvalue, rvalue);
            if(pairValueMap.containsKey(pair)) {
                resultValue = pairValueMap.get(pair);
                Operand operand = getValueOperand(resultValue);
                if(operand != null) {
                    if(node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
                        node.getPrev().remove();
                    }
                    node.replace(new Move(node.getBB(), node.getOp() == BinaryOperation.BinaryOp.DIV ? vrax : vrdx, new IntImmediate(resultValue)));

                }
            } else {
                resultValue = curValueNumber++;
                pairValueMap.put(pair, resultValue);
                if(valueImmediateMap.containsKey(lvalue) && valueImmediateMap.containsKey(rvalue)) {
                    Integer resultImm = doBinary(node.getOp(), valueImmediateMap.get(lvalue), valueImmediateMap.get(rvalue));
                    valueImmediateMap.put(resultValue, resultImm);
                    if(node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
                        node.getPrev().remove();
                    }
                    node.replace(new Move(node.getBB(), node.getOp() == BinaryOperation.BinaryOp.DIV ? vrax : vrdx, new IntImmediate(resultImm)));
                }
            }
            if(node.getOp() == BinaryOperation.BinaryOp.MOD) {
                changeRegisterValue(vrax, curValueNumber++);
                changeRegisterValue(vrdx, resultValue);
            } else {
                changeRegisterValue(vrax, resultValue);
                changeRegisterValue(vrdx, curValueNumber++);
            }
        } else {
            Integer lvalue = getOperandValue(node.getDst());
            Integer rvalue = getOperandValue(node.getSrc());
            Integer resultValue;
            Pair pair = new Pair(node.getOp(), lvalue, rvalue);
            if(pairValueMap.containsKey(pair)) {
                resultValue = pairValueMap.get(pair);
                Operand operand = getValueOperand(resultValue);
                if(operand != null) {
                    node.replace(new Move(node.getBB(), node.getDst(), operand));
                }
            } else {
                resultValue = curValueNumber++;
                pairValueMap.put(pair, resultValue);
                if(valueImmediateMap.containsKey(lvalue) && valueImmediateMap.containsKey(rvalue)) {
                    Integer resultImm = doBinary(node.getOp(), valueImmediateMap.get(lvalue), valueImmediateMap.get(rvalue));
                    valueImmediateMap.put(resultValue, resultImm);
                    node.replace(new Move(node.getBB(), node.getDst(), new IntImmediate(resultImm)));
                }
            }
            if(node.getDst() instanceof VirtualRegister) {
                changeRegisterValue((VirtualRegister) node.getDst(), resultValue);
            }
        }
    }

    private Integer doUnary(UnaryOperation.UnaryOp op, Integer imm) {
        switch (op) {
            case NOT: return ~imm;
            case NEG: return -imm;
            case INC: return imm+1;
            case DEC: return imm-1;
            default: assert false; return 0;
        }
    }

    @Override
    public void visit(UnaryOperation node) {
        Integer value = getOperandValue(node.getDst());
        Integer resultValue;
        if(valueImmediateMap.containsKey(value)) {
            Integer imm = valueImmediateMap.get(value);
            Integer resultImm = doUnary(node.getOp(), imm);
            resultValue = immediateValueMap.containsKey(resultImm) ? immediateValueMap.get(resultImm) : curValueNumber++;
            node.replace(new Move(node.getBB(), node.getDst(), new IntImmediate(resultImm)));
        } else {
            resultValue = curValueNumber++;
        }
        if(node.getDst() instanceof VirtualRegister)
            changeRegisterValue((VirtualRegister) node.getDst(), resultValue);
    }

    @Override
    public void visit(Move node) {
        Integer srcValue = getOperandValue(node.getSrc());
        if(valueImmediateMap.containsKey(srcValue)) {
            node.replace(new Move(node.getBB(), node.getDst(), new IntImmediate(valueImmediateMap.get(srcValue))));
        }
        if(node.getDst() instanceof VirtualRegister) {
            changeRegisterValue((VirtualRegister) node.getDst(), srcValue);
        }
    }

    @Override
    public void visit(Lea node) {
        if(node.getDst() instanceof VirtualRegister) {
            changeRegisterValue((VirtualRegister) node.getDst(), curValueNumber++);
        }
    }

    @Override
    public void visit(Call node) {
        changeRegisterValue(vrax, curValueNumber++);
    }

    @Override
    public void visit(Push node) {

    }

    @Override
    public void visit(Pop node) {
        if(node.getDst() instanceof VirtualRegister)
            changeRegisterValue((VirtualRegister) node.getDst(), curValueNumber++);
    }

    @Override
    public void visit(Leave node) {

    }

    @Override
    public void visit(Cdq node) {
        changeRegisterValue(vrdx, curValueNumber++);
    }

    @Override
    public void visit(Memory node) {

    }

    @Override
    public void visit(StackSlot node) {

    }

    @Override
    public void visit(VirtualRegister node) {

    }

    @Override
    public void visit(PhysicalRegister node) {

    }

    @Override
    public void visit(IntImmediate node) {

    }

    @Override
    public void visit(StaticVariable node) {

    }

    @Override
    public void visit(StaticString node) {

    }

}
