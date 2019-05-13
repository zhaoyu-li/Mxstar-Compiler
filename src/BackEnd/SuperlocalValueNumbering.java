package BackEnd;

import IR.*;
import IR.Instruction.*;
import IR.Operand.*;

import java.util.HashMap;
import java.util.HashSet;

import static IR.RegisterSet.*;

public class SuperlocalValueNumbering implements IRVistor {
    private HashSet<BasicBlock> workList;
    private IRProgram program;
    private ValueTable curValueTable;
    private HashSet<BasicBlock> visitedBBs;

    private class ValueTable {
        private Integer curValueNumber;
        private HashMap<Pair, Integer> pairValueMap;
        private HashMap<Integer, HashSet<VirtualRegister>> valueRegisterMap;
        private HashMap<VirtualRegister, Integer> registerValueMap;
        private HashMap<Integer, Integer> valueImmediateMap;
        private HashMap<Integer, Integer> immediateValueMap;

        private ValueTable() {
            this.curValueNumber = 0;
            this.registerValueMap = new HashMap<>();
            this.pairValueMap = new HashMap<>();
            this.immediateValueMap = new HashMap<>();
            this.valueRegisterMap = new HashMap<>();
            this.valueImmediateMap = new HashMap<>();
        }

        private ValueTable(ValueTable other) {
            this.curValueNumber = other.curValueNumber;
            this.registerValueMap = new HashMap<>(other.registerValueMap);
            this.pairValueMap = new HashMap<>(other.pairValueMap);
            this.immediateValueMap = new HashMap<>(other.immediateValueMap);
            this.valueRegisterMap = new HashMap<>(other.valueRegisterMap);
            this.valueImmediateMap = new HashMap<>(other.valueImmediateMap);
        }
    }

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
                return op.equals(((Pair) o).op) && this.lvalue.equals(((Pair) o).lvalue) && this.lvalue.equals(((Pair) o).rvalue);
            } else {
                return false;
            }
        }
    }

    public SuperlocalValueNumbering(IRProgram program) {
        this.program = program;
        this.workList = new HashSet<>();
        this.curValueTable = null;
        this.visitedBBs = new HashSet<>();
    }

    public void run() {
        visit(program);
    }

    private Integer getOperandValue(Operand operand) {
        if(operand instanceof VirtualRegister) {
            return getRegisterValue((VirtualRegister) operand);
        } else if(operand instanceof IntImmediate) {
            return getImmediateValue(((IntImmediate) operand).getValue());
        } else return curValueTable.curValueNumber++;
    }

    private Integer getImmediateValue(Integer imm) {
        if(!curValueTable.immediateValueMap.containsKey(imm)) {
            curValueTable.valueImmediateMap.put(curValueTable.curValueNumber, imm);
            curValueTable.immediateValueMap.put(imm, curValueTable.curValueNumber++);
        }
        return curValueTable.immediateValueMap.get(imm);
    }

    private Integer getRegisterValue(VirtualRegister vr) {
        if(!curValueTable.registerValueMap.containsKey(vr)) {
            addRegisterValue(vr, curValueTable.curValueNumber++);
        }
        return curValueTable.registerValueMap.get(vr);
    }

    private Operand getValueOperand(Integer value) {
        if(curValueTable.valueImmediateMap.containsKey(value)) {
            return new IntImmediate(curValueTable.valueImmediateMap.get(value));
        } else if(curValueTable.valueRegisterMap.containsKey(value)) {
            if(!curValueTable.valueRegisterMap.get(value).isEmpty()) {
                return curValueTable.valueRegisterMap.get(value).iterator().next();
            } else return null;
        } else return null;
    }

    private void addRegisterValue(VirtualRegister vr, Integer value) {
        if(!curValueTable.valueRegisterMap.containsKey(value)) {
            curValueTable.valueRegisterMap.put(value, new HashSet<>());
        }
        curValueTable.valueRegisterMap.get(value).add(vr);
        curValueTable.registerValueMap.put(vr, value);
    }

    private void delRegisterValue(VirtualRegister vr) {
        if(curValueTable.registerValueMap.containsKey(vr)) {
            Integer value = curValueTable.registerValueMap.get(vr);
            curValueTable.valueRegisterMap.get(value).remove(vr);
            curValueTable.registerValueMap.remove(vr);
        }
    }

    private void changeRegisterValue(VirtualRegister vr, Integer value) {
        delRegisterValue(vr);
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
        workList.clear();
        workList.add(node.getHeadBB());
        visitedBBs.clear();
        curValueTable = null;
        ValueTable empty = new ValueTable();
        while(!workList.isEmpty()) {
            BasicBlock bb = workList.iterator().next();
            workList.remove(bb);
            SVN(bb, empty);
        }
    }

    private void SVN(BasicBlock bb, ValueTable table) {
        ValueTable t = new ValueTable(table);
        LVN(bb, t);
        for(BasicBlock succ : bb.getNextBBs()) {
            if(succ.getPrevBBs().size() == 1) {
                SVN(succ, t);
            } else if(!visitedBBs.contains(succ)) {
                workList.add(succ);
            }
        }
    }

    private void LVN(BasicBlock bb, ValueTable t) {
        curValueTable = t;
        visitedBBs.add(bb);
        visit(bb);
    }

    @Override
    public void visit(BasicBlock node) {
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
    public void visit(Compare node) {

    }

    @Override
    public void visit(Return node) {

    }

    private Integer doBinary(BinaryOperation.BinaryOp op, Integer limm, Integer rimm) {
        switch (op) {
            case SUB:
                return limm - rimm;
            case XOR:
                return limm ^ rimm;
            case MUL:
                return limm * rimm;
            case MOD:
                if(rimm == 0) rimm = 1;
                return limm % rimm;
            case DIV:
                if(rimm == 0) rimm = 1;
                return limm / rimm;
            case AND:
                return limm & rimm;
            case ADD:
                return limm + rimm;
            case OR:
                return limm | rimm;
            case SAL:
                return limm << rimm;
            case SAR:
                return limm >> rimm;
            default:
                return 0;
        }
    }

    @Override
    public void visit(BinaryOperation node) {
        if(node.getOp() == BinaryOperation.BinaryOp.MUL || node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
            Integer lvalue = getOperandValue(vrax);
            Integer rvalue = getOperandValue(node.getSrc());
            Integer resultValue;
            Pair pair = new Pair(node.getOp(), lvalue, rvalue);
            if(curValueTable.pairValueMap.containsKey(pair)) {
                resultValue = curValueTable.pairValueMap.get(pair);
                Operand operand = getValueOperand(resultValue);
                if(operand != null) {
                    if(node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
                        node.getPrev().remove();
                    }
                    node.replace(new Move(node.getBB(), node.getOp() == BinaryOperation.BinaryOp.DIV ? vrax : vrdx, new IntImmediate(resultValue)));
                }
            } else {
                resultValue = curValueTable.curValueNumber++;
                curValueTable.pairValueMap.put(pair, resultValue);
                if(curValueTable.valueImmediateMap.containsKey(lvalue) && curValueTable.valueImmediateMap.containsKey(rvalue)) {
                    Integer resultImm = doBinary(node.getOp(), curValueTable.valueImmediateMap.get(lvalue), curValueTable.valueImmediateMap.get(rvalue));
                    curValueTable.valueImmediateMap.put(resultValue, resultImm);
                    if(node.getOp() == BinaryOperation.BinaryOp.DIV || node.getOp() == BinaryOperation.BinaryOp.MOD) {
                        node.getPrev().remove();
                    }
                    node.replace(new Move(node.getBB(), node.getOp() == BinaryOperation.BinaryOp.DIV ? vrax : vrdx, new IntImmediate(resultImm)));
                }
            }
            if(node.getOp() == BinaryOperation.BinaryOp.MOD) {
                changeRegisterValue(vrax, curValueTable.curValueNumber++);
                changeRegisterValue(vrdx, resultValue);
            } else {
                changeRegisterValue(vrax, resultValue);
                changeRegisterValue(vrdx, curValueTable.curValueNumber++);
            }
        } else {
            Integer lvalue = getOperandValue(node.getDst());
            Integer rvalue = getOperandValue(node.getSrc());
            Integer resultValue;
            Pair pair = new Pair(node.getOp(), lvalue, rvalue);
            if(curValueTable.pairValueMap.containsKey(pair)) {
                resultValue = curValueTable.pairValueMap.get(pair);
                Operand operand = getValueOperand(resultValue);
                if(operand != null) {
                    node.replace(new Move(node.getBB(), node.getDst(), operand));
                }
            } else {
                resultValue = curValueTable.curValueNumber++;
                curValueTable.pairValueMap.put(pair, resultValue);
                if(curValueTable.valueImmediateMap.containsKey(lvalue) && curValueTable.valueImmediateMap.containsKey(rvalue)) {
                    Integer resultImm = doBinary(node.getOp(), curValueTable.valueImmediateMap.get(lvalue), curValueTable.valueImmediateMap.get(rvalue));
                    curValueTable.valueImmediateMap.put(resultValue, resultImm);
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
            case NOT:
                return ~imm;
            case NEG:
                return -imm;
            case INC:
                return imm + 1;
            case DEC:
                return imm - 1;
            default:
                return 0;
        }
    }

    @Override
    public void visit(UnaryOperation node) {
        Integer value = getOperandValue(node.getDst());
        Integer resultValue;
        if(curValueTable.valueImmediateMap.containsKey(value)) {
            Integer imm = curValueTable.valueImmediateMap.get(value);
            Integer resultImm = doUnary(node.getOp(), imm);
            resultValue = curValueTable.immediateValueMap.containsKey(resultImm) ? curValueTable.immediateValueMap.get(resultImm) : curValueTable.curValueNumber++;
            node.replace(new Move(node.getBB(), node.getDst(), new IntImmediate(resultImm)));
        } else {
            resultValue = curValueTable.curValueNumber++;
        }
        if(node.getDst() instanceof VirtualRegister)
            changeRegisterValue((VirtualRegister) node.getDst(), resultValue);
    }

    @Override
    public void visit(Move node) {
        Integer srcValue = getOperandValue(node.getSrc());
        if(curValueTable.valueImmediateMap.containsKey(srcValue)) {
            node.replace(new Move(node.getBB(), node.getDst(), new IntImmediate(curValueTable.valueImmediateMap.get(srcValue))));
        }
        if(node.getDst() instanceof VirtualRegister) {
            changeRegisterValue((VirtualRegister) node.getDst(), srcValue);
        }
    }

    @Override
    public void visit(Lea node) {
        if(node.getDst() instanceof VirtualRegister) {
            changeRegisterValue((VirtualRegister) node.getDst(), curValueTable.curValueNumber++);
        }
    }

    @Override
    public void visit(Call node) {
        changeRegisterValue(vrax, curValueTable.curValueNumber++);
    }

    @Override
    public void visit(Push node) {

    }

    @Override
    public void visit(Pop node) {
        if(node.getDst() instanceof VirtualRegister)
            changeRegisterValue((VirtualRegister) node.getDst(), curValueTable.curValueNumber++);
    }

    @Override
    public void visit(Leave node) {

    }

    @Override
    public void visit(Cdq node) {
        changeRegisterValue(vrdx, curValueTable.curValueNumber++);
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
