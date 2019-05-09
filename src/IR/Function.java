package IR;

import IR.Instruction.*;
import IR.Operand.PhysicalRegister;
import IR.Operand.Register;
import IR.Operand.StackSlot;
import IR.Operand.VirtualRegister;
import Scope.VariableEntity;

import java.util.HashSet;
import java.util.LinkedList;

import static IR.RegisterSet.*;

public class Function {
    public enum FuncType {
        UserDefined, Library, External
    }
    private FuncType type;
    private String name;
    private boolean hasReturnValue;
    private boolean isGlobal;
    private boolean hasOutput;
    private boolean canBeMemorized;

    private BasicBlock headBB;
    private BasicBlock tailBB;
    private LinkedList<VirtualRegister> parameters;
    private LinkedList<StackSlot> temporaries;

    private HashSet<Function> callees;
    private HashSet<Function> visitedFunction;

    private HashSet<VariableEntity> usedGlobalVariables;
    private HashSet<VariableEntity> usedRecursiveVariables;
    private HashSet<PhysicalRegister> usedPhysicalRegisters;
    private LinkedList<Return> returnList;

    private LinkedList<BasicBlock> basicBlocks;
    private LinkedList<BasicBlock> reversePostOrder;
    private LinkedList<BasicBlock> reversePrevOrder;
    private HashSet<BasicBlock> visitedBB;

    public Function(FuncType type, String name, boolean hasReturnValue, boolean isGlobal) {
        this.type = type;
        this.name = name;
        this.hasReturnValue = hasReturnValue;
        this.isGlobal = isGlobal;

        hasOutput = false;
        canBeMemorized = false;
        parameters = new LinkedList<>();
        temporaries = new LinkedList<>();
        callees = new HashSet<>();
        visitedFunction = new HashSet<>();
        usedGlobalVariables = new HashSet<>();
        usedRecursiveVariables = new HashSet<>();
        usedPhysicalRegisters = new HashSet<>();
        usedRecursiveVariables = new HashSet<>();
        returnList = new LinkedList<>();

        basicBlocks = new LinkedList<>();
        reversePostOrder = new LinkedList<>();
        reversePrevOrder = new LinkedList<>();
        visitedBB = new HashSet<>();


        if(type != FuncType.UserDefined) {
            for(PhysicalRegister pr : RegisterSet.allRegs) {
                if(!pr.getName().equals("rbp") || pr.getName().equals("rsp")) {
                    usedPhysicalRegisters.add(pr);
                }
            }
        }
    }

    public FuncType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Boolean hasReturnValue() {
        return hasReturnValue;
    }

    public void setHeadBB(BasicBlock headBB) {
        this.headBB = headBB;
    }

    public BasicBlock getHeadBB() {
        return headBB;
    }

    public void setTailBB(BasicBlock tailBB) {
        this.tailBB = tailBB;
    }

    public BasicBlock getTailBB() {
        return tailBB;
    }

    public void addParameter(VirtualRegister parameter) {
        parameters.add(parameter);
    }

    public LinkedList<VirtualRegister> getParameters() {
        return parameters;
    }

    public void addTemporary(StackSlot temporary) {
        temporaries.add(temporary);
    }

    public LinkedList<StackSlot> getTemporaries() {
        return temporaries;
    }

    public void addCallee(Function function) {
        callees.add(function);
    }

    public HashSet<Function> getCallees() {
        return callees;
    }

    public void addGlobalVariable(VariableEntity var) {
        usedGlobalVariables.add(var);
    }

    public void setUsedGlobalVariables(HashSet<VariableEntity> usedGlobalVariables) {
        this.usedGlobalVariables = usedGlobalVariables;
    }

    public HashSet<VariableEntity> getUsedGlobalVariables() {
        return usedGlobalVariables;
    }

    public void calcUsedRecursiveVariables() {
        visitedFunction.clear();
        addUsedRecursiveVariables(this);
    }

    public void addUsedRecursiveVariables(Function function) {
        if(visitedFunction.contains(function)) return;
        visitedFunction.add(function);
        for(Function func : function.callees) {
            addUsedRecursiveVariables(func);
        }
        usedRecursiveVariables.addAll(function.usedGlobalVariables);
    }

    public HashSet<VariableEntity> getUsedRecursiveVariables() {
        return usedRecursiveVariables;
    }

    public void calcUsedPhysicalRegisters() {
        for(BasicBlock bb : basicBlocks) {
            for(Instruction inst = bb.getHead(); inst != null; inst = inst.getNext()) {
                if(inst instanceof Return) continue;
                if(inst instanceof Call) {
                    usedPhysicalRegisters.addAll(callerSave);
                } else if(inst instanceof BinaryOperation) {
                    if(((BinaryOperation) inst).getOp() == BinaryOperation.BinaryOp.MUL ||
                    ((BinaryOperation) inst).getOp() == BinaryOperation.BinaryOp.DIV ||
                    ((BinaryOperation) inst).getOp() == BinaryOperation.BinaryOp.MOD) {
                        if(((BinaryOperation) inst).getSrc() instanceof Register) {
                            usedPhysicalRegisters.add((PhysicalRegister) ((BinaryOperation) inst).getSrc());
                        }
                        usedPhysicalRegisters.add(rax);
                        usedPhysicalRegisters.add(rdx);
                    }
                } else {
                    for(Register reg : inst.getUsedRegisters()) {
                        PhysicalRegister pr = (PhysicalRegister) reg;
                        usedPhysicalRegisters.add(pr);
                    }
                    for(Register reg : inst.getDefinedRegisters()) {
                        PhysicalRegister pr = (PhysicalRegister) reg;
                        usedPhysicalRegisters.add(pr);
                    }

                }
            }
        }
    }

    public HashSet<PhysicalRegister> getUsedPhysicalRegisters() {
        return usedPhysicalRegisters;
    }

    public void addReturn(Return ret) {
        returnList.add(ret);
    }

    public LinkedList<Return> getReturnList() {
        return returnList;
    }

    public void addBasicBlock(BasicBlock bb) {
        basicBlocks.add(bb);
    }

    public LinkedList<BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    private void dfsReversePostOrder(BasicBlock bb) {
        if(visitedBB.contains(bb)) return;
        visitedBB.add(bb);
        for(BasicBlock next : bb.getNextBBs()) {
            dfsReversePostOrder(next);
        }
        reversePostOrder.addFirst(bb);
    }

    public void calcReversePostOrder() {
        visitedBB.clear();
        reversePostOrder.clear();
        dfsReversePostOrder(headBB);
    }

    public LinkedList<BasicBlock> getReversePostOrder() {
        return reversePostOrder;
    }

    private void dfsReversePrevOrder(BasicBlock bb) {
        if(visitedBB.contains(bb)) return;
        visitedBB.add(bb);
        for(BasicBlock prev : bb.getPrevBBs()) {
            dfsReversePrevOrder(prev);
        }
        reversePrevOrder.addFirst(bb);
    }

    public void calcReversePrevOrder() {
        visitedBB.clear();
        reversePrevOrder.clear();
        dfsReversePrevOrder(tailBB);
    }

    public LinkedList<BasicBlock> getReversePrevOrder() {
        return reversePrevOrder;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setHasOutput(boolean hasOutput) {
        this.hasOutput = hasOutput;
    }

    public boolean hasOutput() {
        return hasOutput;
    }

    public void setCanBeMemorized(boolean canBeMemorized) {
        this.canBeMemorized = canBeMemorized;
    }

    public boolean canBeMemorized() {
        return canBeMemorized;
    }

    public void accept(IRVistor vistor) {
        vistor.visit(this);
    }
}
